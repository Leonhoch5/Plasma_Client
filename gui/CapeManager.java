package plasma.gui;

import net.minecraft.util.ResourceLocation;

public class CapeManager {
    public static ResourceLocation currentCape = new ResourceLocation("client/capes/cape1.png");

    public static void setCurrentCape(ResourceLocation cape) {
        currentCape = cape;
    }

    public static ResourceLocation getCurrentCape() {
        return currentCape;
    }
}
