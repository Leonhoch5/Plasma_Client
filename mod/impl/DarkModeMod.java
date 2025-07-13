package plasma.mod.impl;

import net.minecraft.util.ResourceLocation;
import plasma.mod.Category;
import plasma.mod.Mod;

public class DarkModeMod extends Mod{

	public DarkModeMod() {
		super("Dark Mode", "Changes Menu and More to dark mode", Category.HUD, false, 0, 0);
	}
    public ResourceLocation Background = new ResourceLocation("client/plasma-background.png");

 // In DarkModeMod.java
    public static boolean isEnabled = false;

    @Override
    public void onEnable() {
        super.onEnable();
        isEnabled = true;
        Background = new ResourceLocation("client/plasma-background-dark.png");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        isEnabled = false;
        Background = new ResourceLocation("client/plasma-background-light.png");
    }

   

}
	