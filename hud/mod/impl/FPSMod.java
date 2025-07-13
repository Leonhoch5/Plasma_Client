package plasma.hud.mod.impl;

import plasma.mod.Category;
import plasma.mod.Mod;

public class FPSMod extends Mod {

    public FPSMod() {
		super("FPSMod", "FPS Mod", Category.HUD, true, 10, 30);
    }

    @Override
    public void draw() {
        fr.drawString("FPS: " + mc.getDebugFPS(), getX(), getY(), -1);
    }

    @Override
    public void renderDummy(int mouseX, int mouseY) {
        fr.drawString(name, getX(), getY(), -1);
        super.renderDummy(mouseX, mouseY);
    }
    @Override
    public int getWidth() {
    	return fr.getStringWidth("FPS: " + mc.getDebugFPS());
    }
    @Override
    public int getHeight() {
    	return fr.FONT_HEIGHT;
    }
}
