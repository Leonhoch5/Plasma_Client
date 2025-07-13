package plasma.hud;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import plasma.Client;
import plasma.gui.clickgui.ClickGUI;
import plasma.mod.Mod;

public class HUDConfigScreen extends GuiScreen {

    private GuiButton modsButton;

    @Override
    public void initGui() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        modsButton = new GuiButton(0, centerX - 60, centerY - 10, 120, 20, "Mods");
        this.buttonList.clear(); 
        this.buttonList.add(modsButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground(); 

        // Draw dummy mods
        for (Mod m : Client.INSTANCE.modManager.mods) {
        	if (m.isEnabled()) {
            m.renderDummy(mouseX, mouseY);
        	}
        }

        // Draw the standard button
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
           mc.displayGuiScreen(new ClickGUI()); 
        }
    }
}

