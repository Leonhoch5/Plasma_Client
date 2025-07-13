package plasma.gui;

import net.minecraft.client.Minecraft;
import plasma.auth.AuthManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiMicrosoftLogin extends GuiScreen {
    private GuiButton microsoftLoginButton;
    private GuiButton cancelButton;
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        this.microsoftLoginButton = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, 
            I18n.format("Login with Microsoft"));
        this.cancelButton = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, 
            I18n.format("gui.cancel"));
            
        this.buttonList.add(this.microsoftLoginButton);
        this.buttonList.add(this.cancelButton);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            // Microsoft login
            AuthManager.startAuth();
            this.mc.displayGuiScreen(null);
        } else if (button.id == 1) {
            // Cancel
            this.mc.displayGuiScreen(null);
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Minecraft Login", this.width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
