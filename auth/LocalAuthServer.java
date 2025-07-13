package plasma.auth;

import org.apache.http.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.impl.DefaultBHttpServerConnectionFactory;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class LocalAuthServer {
    private static volatile boolean running = true;
    private static volatile String authCode;

    public static void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(8888)) {
                while (running) {
                    try (Socket socket = serverSocket.accept()) {
                        DefaultBHttpServerConnection connection = new DefaultBHttpServerConnectionFactory()
                            .createConnection(socket);

                        try {
                            HttpRequest request = connection.receiveRequestHeader();
                            handleRequest(request);
                            sendResponse(connection);
                        } catch (HttpException e) {
                            sendErrorResponse(connection, HttpStatus.SC_BAD_REQUEST, "Bad Request");
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                connection.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        if (running) e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void handleRequest(HttpRequest request) {
        String uri = request.getRequestLine().getUri();
        if (uri.contains("code=")) {
            String[] parts = uri.split("code=");
            if (parts.length > 1) {
                authCode = parts[1].split("&")[0];
            }
        }
    }

    private static void sendResponse(HttpServerConnection conn) {
        try {
            HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
                HttpStatus.SC_OK, "OK");

            String html = "<html><body>You can now return to Minecraft</body></html>";
            StringEntity entity = new StringEntity(html, StandardCharsets.UTF_8);
            response.setEntity(entity);
            response.setHeader("Content-Type", "text/html");
            response.setHeader("Content-Length", String.valueOf(entity.getContentLength()));

            conn.sendResponseHeader(response);
            conn.sendResponseEntity(response);
        } catch (HttpException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendErrorResponse(HttpServerConnection conn, int statusCode, String reason) {
        try {
            HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
                statusCode, reason);
            
            String html = "<html><body><h1>" + statusCode + " " + reason + "</h1></body></html>";
            StringEntity entity = new StringEntity(html, StandardCharsets.UTF_8);
            response.setEntity(entity);
            response.setHeader("Content-Type", "text/html");
            response.setHeader("Content-Length", String.valueOf(entity.getContentLength()));

            conn.sendResponseHeader(response);
            conn.sendResponseEntity(response);
        } catch (HttpException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        running = false;
    }

    public static String getAuthCode() {
        return authCode;
    }
}