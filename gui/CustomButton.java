package plasma.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class CustomButton extends Gui {
    public int xPosition;
    public int yPosition;
    public int width;
    public int height;
    public String displayString;
    public int id;
    public boolean enabled;
    public boolean visible;
    protected boolean hovered;

    // Colors
    private static final int BG_NORMAL = 0x1A3DAEE9; // 10% opacity
    private static final int BG_HOVER = 0x553DAEE9;  // 33% opacity
    private static final int BORDER = 0xFF3DAEE9;    // Solid blue
    private static final int TEXT_NORMAL = 0xFFBDC3C7;
    private static final int TEXT_HOVER = 0xFFEFF0F1;
    private static final int TEXT_DISABLED = 0xFFA0A0A0;
    private static final int GLOW = 0x883DAEE9;      // 50% opacity glow

    public CustomButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public CustomButton(int buttonId, int x, int y, int width, int height, String buttonText) {
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = width;
        this.height = height;
        this.displayString = buttonText;
        this.enabled = true;
        this.visible = true;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) return;
        
        this.hovered = isMouseOver(mouseX, mouseY);
        
        // Draw glow effect first (behind button)
        if (hovered && enabled) {
            drawRect(xPosition - 2, yPosition - 2, 
                    xPosition + width + 2, 
                    yPosition + height + 2, 
                    GLOW);
        }
        
        // Draw main button background
        int bgColor = enabled ? (hovered ? BG_HOVER : BG_NORMAL) : BG_NORMAL;
        drawRect(xPosition, yPosition, 
                xPosition + width, 
                yPosition + height, 
                bgColor);
        
        // Draw border
        int borderColor = enabled ? BORDER : (BORDER & 0x00FFFFFF) | 0x80000000;
        drawBorder(xPosition, yPosition, width, height, borderColor);
        
        // Draw text
        int textColor = enabled ? (hovered ? TEXT_HOVER : TEXT_NORMAL) : TEXT_DISABLED;
        drawCenteredString(mc.fontRendererObj, displayString, 
                         xPosition + width / 2, 
                         yPosition + (height - 8) / 2, 
                         textColor);
    }

    private void drawBorder(int x, int y, int width, int height, int color) {
        // Top
        drawRect(x, y, x + width, y + 1, color);
        // Bottom
        drawRect(x, y + height - 1, x + width, y + height, color);
        // Left
        drawRect(x, y, x + 1, y + height, color);
        // Right
        drawRect(x + width - 1, y, x + width, y + height, color);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= xPosition && mouseY >= yPosition && 
               mouseX < xPosition + width && 
               mouseY < yPosition + height;
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return enabled && visible && isMouseOver(mouseX, mouseY);
    }

    // Add other necessary methods from GuiButton...
}