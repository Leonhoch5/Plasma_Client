package plasma.hud.mod.impl;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import plasma.mod.Category;
import plasma.mod.Mod;

public class TargetHUD extends Mod {

	EntityLivingBase target;
	
    public TargetHUD() {
		super("TargetHUD", "Whoom am i looking at?", Category.HUD, true, 10 , 20);
    }

    @Override
    public void draw() {
       renderTargetHUD();
       
       super.draw();
    }

    @Override
    public void renderDummy(int mouseX, int mouseY) {
        fr.drawString("TargetName", getX() + 2, getY()+ 2, -1);
        fr.drawString("0" + " \u2764", getX() + 2, getY()+ 2 + fr.FONT_HEIGHT, -1);
        GuiInventory.drawEntityOnScreen(getX() + 70, getY() + 30, 20, 50, 0, mc.thePlayer);
        super.renderDummy(mouseX, mouseY);
    }
    @Override
    public int getWidth() {
    	return 100;
    }
    @Override
    public int getHeight() {
    	return fr.FONT_HEIGHT * 2 + 4;
    }
    
    public void renderTargetHUD() {
    	target = (EntityLivingBase) mc.pointedEntity;
    	if(target != null) {
	    	fr.drawString(target.getName(), getX() + 2, getY()+ 2, -1);
	        fr.drawString(target.getHealth() + " \u2764", getX() + 2, getY()+ 2 + fr.FONT_HEIGHT, -1);
	        GuiInventory.drawEntityOnScreen(getX() + 70, getY() + 30, 20, 50, 0, target);

    	}
	}
}
