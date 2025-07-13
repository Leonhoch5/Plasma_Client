package plasma.auth;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class AuthManager {
    private static final String CLIENT_ID = "d1956a33-cca4-4042-8a7b-b2d4e111f3d0";
    private static final String SCOPE = "XboxLive.signin offline_access";
    private static final String REDIRECT_URI = "http://localhost:8888";
    private static final String TOKEN_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token";
    private static final String AUTH_URL = "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize";
    private static final String XBOX_AUTH_URL = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String XSTS_AUTH_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String MC_AUTH_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String MC_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
    private static final String MC_ENTITLEMENTS_URL = "https://api.minecraftservices.com/entitlements/mcstore";

    private static String codeVerifier;
    private static String authCode;

    public static CompletableFuture<Boolean> startAuth() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                codeVerifier = generateCodeVerifier();
                String codeChallenge = generateCodeChallenge(codeVerifier);
                
                String authUrl = String.format("%s?client_id=%s&response_type=code&redirect_uri=%s" +
                        "&response_mode=query&scope=%s&code_challenge=%s&code_challenge_method=S256",
                        AUTH_URL, 
                        URLEncoder.encode(CLIENT_ID, "UTF-8"),
                        URLEncoder.encode(REDIRECT_URI, "UTF-8"),
                        URLEncoder.encode(SCOPE, "UTF-8"),
                        codeChallenge);
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(URI.create(authUrl));
                } else {
                    throw new RuntimeException("Desktop not supported - cannot open browser");
                }
                
                LocalAuthServer.start();
                
                while (LocalAuthServer.getAuthCode() == null) {
                    Thread.sleep(100);
                }
                
                authCode = LocalAuthServer.getAuthCode();
                LocalAuthServer.stop();
                
                return completeAuth();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    private static String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.encodeBase64URLSafeString(codeVerifier);
    }

    private static String generateCodeChallenge(String codeVerifier) {
        byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
        byte[] digest = DigestUtils.sha256(bytes);
        return Base64.encodeBase64URLSafeString(digest);
    }

    private static boolean completeAuth() {
        try {
            TokenResponse tokens = exchangeCodeForTokens(authCode, codeVerifier);
            if (tokens.accessToken == null) throw new RuntimeException("Failed to get access token");

            XboxAuthResponse xboxAuth = authenticateWithXbox(tokens.accessToken);
            if (xboxAuth.token == null) throw new RuntimeException("Failed to get Xbox token");

            XSTSAuthResponse xstsAuth = authenticateWithXSTS(xboxAuth.token);
            String userHash = xstsAuth.getUserHash();
            if (xstsAuth.token == null || userHash == null) {
                throw new RuntimeException("Failed to get XSTS token");
            }

            MinecraftAuthResponse mcAuth = authenticateWithMinecraft(xstsAuth.token, userHash);
            if (mcAuth.accessToken == null) throw new RuntimeException("Failed to get Minecraft token");

            if (!checkGameOwnership(mcAuth.accessToken)) {
                throw new RuntimeException("Account doesn't own Minecraft");
            }

            MinecraftProfile mcProfile = getMinecraftProfile(mcAuth.accessToken);
            if (mcProfile.username == null || mcProfile.uuid == null) {
                throw new RuntimeException("Failed to get Minecraft profile");
            }

            Minecraft.getMinecraft().session = new Session(
                mcProfile.username,
                mcProfile.uuid,
                mcAuth.accessToken,
                "mojang"
            );

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static TokenResponse exchangeCodeForTokens(String code, String verifier) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(TOKEN_URL);

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", CLIENT_ID));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("code_verifier", verifier));

        httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        HttpResponse response = httpClient.execute(httpPost);

        String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return new Gson().fromJson(json, TokenResponse.class);
    }

    private static XboxAuthResponse authenticateWithXbox(String accessToken) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(XBOX_AUTH_URL);

        JsonObject properties = new JsonObject();
        properties.addProperty("AuthMethod", "RPS");
        properties.addProperty("SiteName", "user.auth.xboxlive.com");
        properties.addProperty("RpsTicket", "d=" + accessToken);

        JsonObject request = new JsonObject();
        request.add("Properties", properties);
        request.addProperty("RelyingParty", "http://auth.xboxlive.com");
        request.addProperty("TokenType", "JWT");

        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(new Gson().toJson(request), StandardCharsets.UTF_8));

        HttpResponse response = httpClient.execute(httpPost);
        String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return new Gson().fromJson(json, XboxAuthResponse.class);
    }

    private static XSTSAuthResponse authenticateWithXSTS(String xboxToken) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(XSTS_AUTH_URL);

        JsonObject properties = new JsonObject();
        properties.add("SandboxId", new JsonPrimitive("RETAIL"));
        
        JsonArray userTokens = new JsonArray();
        userTokens.add(new JsonPrimitive(xboxToken));
        properties.add("UserTokens", userTokens);

        JsonObject request = new JsonObject();
        request.add("Properties", properties);
        request.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
        request.addProperty("TokenType", "JWT");

        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(new Gson().toJson(request), StandardCharsets.UTF_8));

        HttpResponse response = httpClient.execute(httpPost);
        String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return new Gson().fromJson(json, XSTSAuthResponse.class);
    }

    private static MinecraftAuthResponse authenticateWithMinecraft(String xstsToken, String userHash) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(MC_AUTH_URL);

        JsonObject request = new JsonObject();
        request.addProperty("identityToken", String.format("XBL3.0 x=%s;%s", userHash, xstsToken));

        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(new Gson().toJson(request), StandardCharsets.UTF_8));

        HttpResponse response = httpClient.execute(httpPost);
        String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return new Gson().fromJson(json, MinecraftAuthResponse.class);
    }

    private static boolean checkGameOwnership(String mcToken) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(MC_ENTITLEMENTS_URL);

        httpGet.setHeader("Authorization", "Bearer " + mcToken);
        httpGet.setHeader("Accept", "application/json");

        HttpResponse response = httpClient.execute(httpGet);
        String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        
        JsonObject jsonResponse = new Gson().fromJson(json, JsonObject.class);
        JsonArray items = jsonResponse.getAsJsonArray("items");
        return items != null && items.size() > 0;
    }

    private static MinecraftProfile getMinecraftProfile(String mcToken) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(MC_PROFILE_URL);

        httpGet.setHeader("Authorization", "Bearer " + mcToken);
        httpGet.setHeader("Accept", "application/json");

        HttpResponse response = httpClient.execute(httpGet);
        String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        return new Gson().fromJson(json, MinecraftProfile.class);
    }

    public static void setAuthCode(String code) {
        authCode = code;
    }

    private static class TokenResponse {
        @SerializedName("access_token")
        String accessToken;
        @SerializedName("refresh_token")
        String refreshToken;
        @SerializedName("token_type")
        String tokenType;
        @SerializedName("expires_in")
        int expiresIn;
    }

    private static class XboxAuthResponse {
        @SerializedName("Token")
        String token;
        @SerializedName("DisplayClaims")
        JsonObject displayClaims;
    }

    private static class XSTSAuthResponse {
        @SerializedName("Token")
        String token;
        @SerializedName("DisplayClaims")
        JsonObject displayClaims;
        
        String getUserHash() {
            if (displayClaims != null && displayClaims.has("xui")) {
                JsonArray xui = displayClaims.getAsJsonArray("xui");
                if (xui.size() > 0) {
                    return xui.get(0).getAsJsonObject().get("uhs").getAsString();
                }
            }
            return null;
        }
    }

    private static class MinecraftAuthResponse {
        @SerializedName("access_token")
        String accessToken;
        @SerializedName("expires_in")
        int expiresIn;
    }

    private static class MinecraftProfile {
        @SerializedName("name")
        String username;
        @SerializedName("id")
        String uuid;
        @SerializedName("skins")
        JsonArray skins;
    }
}