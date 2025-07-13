package plasma.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class MainMenu extends GuiScreen {
    @Override
    public void initGui() {
        // Clear existing buttons
        this.buttonList.clear();
        
        // Calculate positions
        int buttonWidth = 200;
        int buttonHeight = 20;
        int centerX = this.width / 2 - buttonWidth / 2;
        int startY = this.height / 4 + 48; 
        
        this.buttonList.add(new GuiButton(0, centerX, startY, buttonWidth, buttonHeight, "Singleplayer"));
        this.buttonList.add(new GuiButton(1, centerX, startY + 24, buttonWidth, buttonHeight, "Multiplayer"));
        this.buttonList.add(new GuiButton(2, centerX, startY + 48, buttonWidth, buttonHeight, "Options"));
        this.buttonList.add(new GuiButton(3, centerX, startY + 72, buttonWidth, buttonHeight, "Skin Editor"));
        this.buttonList.add(new GuiButton(4, centerX, startY + 96, buttonWidth, buttonHeight, "Quit Game"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw default background
        this.drawDefaultBackground();
        
        // Draw title
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        String title = "PLASMA CLIENT";
        int titleWidth = this.fontRendererObj.getStringWidth(title);
        this.drawCenteredString(this.fontRendererObj, title, this.width / 4, 30, 0x1A3DAEE9);
        GlStateManager.popMatrix();
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        String version = "Minecraft 1.8.9";
        this.fontRendererObj.drawString(version, 10, this.height - 20, 0xA0A0A0);

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                // Singleplayer
                this.mc.displayGuiScreen(new net.minecraft.client.gui.GuiSelectWorld(this));
                break;
            case 1:
                // Multiplayer
                this.mc.displayGuiScreen(new net.minecraft.client.gui.GuiMultiplayer(this));
                break;
            case 2:
                // Options
                this.mc.displayGuiScreen(new net.minecraft.client.gui.GuiOptions(this, this.mc.gameSettings));
                break;
            case 3:
                // TODO: make this shit skin viewer/editor
                this.mc.displayGuiScreen(new GuiMicrosoftLogin());

                break;
            case 4:
                // Quit Game
                this.mc.shutdown();
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}