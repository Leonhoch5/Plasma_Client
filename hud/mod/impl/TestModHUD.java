package plasma.hud.mod.impl;

import plasma.mod.Category;
import plasma.mod.Mod;

public class TestModHUD extends Mod {

    public TestModHUD() {
		super("Plasma Info", "What version of plasma am i usign", Category.HUD, true, 10 , 10);
    }

    @Override
    public void draw() {
        fr.drawString(name, getX(), getY(), -1);
    }

    @Override
    public void renderDummy(int mouseX, int mouseY) {
        fr.drawString(name, getX(), getY(), -1);
        super.renderDummy(mouseX, mouseY);
    }
    @Override
    public int getWidth() {
    	return fr.getStringWidth(name);
    }
    @Override
    public int getHeight() {
    	return fr.FONT_HEIGHT;
    }
    
}
