package plasma.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class SessionChanger {

    private static SessionChanger instance;

    public static SessionChanger getInstance() {
        if (instance == null) {
            instance = new SessionChanger();
        }
        return instance;
    }

    // Sets a Minecraft session directly
    public void setUserSession(Session session) {
        Minecraft.getMinecraft().session = session;
    }

    // Offline mode
    public void setUserOffline(String username) {
        Session session = new Session(username, username, "0", "legacy");
        setUserSession(session);
    }
}
