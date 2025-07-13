package plasma.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import java.io.IOException;

public class CapeSelection extends GuiScreen {
    private static final ResourceLocation[] CAPES = {
        new ResourceLocation("client/capes/cape1.png"),
        new ResourceLocation("client/capes/Rickroll.gif"),
        new ResourceLocation("client/capes/cape3.png")
    };
    
    private int selectedCape = 0;

    @Override
    public void initGui() {
        this.buttonList.clear();
        
        // Add cape selection buttons
        int buttonWidth = 100;
        int buttonHeight = 20;
        int centerX = this.width / 2 - buttonWidth / 2;
        
        this.buttonList.add(new GuiButton(0, centerX, this.height / 2 - 30, buttonWidth, buttonHeight, "Previous Cape"));
        this.buttonList.add(new GuiButton(1, centerX, this.height / 2, buttonWidth, buttonHeight, "Next Cape"));
        this.buttonList.add(new GuiButton(2, centerX, this.height / 2 + 30, buttonWidth, buttonHeight, "Apply Cape"));
        this.buttonList.add(new GuiButton(3, centerX, this.height / 2 + 60, buttonWidth, buttonHeight, "Back"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: // Previous
                selectedCape = (selectedCape - 1 + CAPES.length) % CAPES.length;
                break;
            case 1: // Next
                selectedCape = (selectedCape + 1) % CAPES.length;
                break;
            case 2: // Apply
                // Save selected cape to config
                CapeManager.setCurrentCape(CAPES[selectedCape]);
                this.mc.displayGuiScreen(null);
                break;
            case 3: // Back
                this.mc.displayGuiScreen(null);
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        
        
        // Draw current cape preview
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CAPES[selectedCape]);
        Gui.drawModalRectWithCustomSizedTexture(
            this.width / 2 - 50, this.height / 2 - 80,
            0, 0,
            100, 50,
            100, 50
        );
     // TODO make this work somehow :::: drawEntityOnScreen(70, 30, 20, 50, 0, mc.thePlayer);

        
        // Draw cape name
        this.drawCenteredString(this.fontRendererObj, "Cape " + (selectedCape + 1), this.width / 2, this.height / 2 - 90, 0xFFFFFF);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}