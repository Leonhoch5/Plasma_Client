package plasma.gui.clickgui.comp;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import plasma.mod.Mod;

public class ModButton {
    public int x, y, w, h;
    public Mod mod;

    public ModButton(int x, int y, int w, int h, Mod m) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.mod = m;
    }

    public void draw() {
        Gui.drawRect(x, y, x + w, y + h, new Color(0, 0, 0, 170).getRGB());
        Minecraft.getMinecraft().fontRendererObj.drawString(mod.name, x + 2, y + 2, getColor());
    }

    public int getColor() {
        return mod.isEnabled() ? new Color(0, 255, 0, 170).getRGB() : new Color(255, 0, 0, 170).getRGB();
    }

    public void onClick(int mouseX, int mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            mod.setEnabled(!mod.isEnabled());
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Mod getMod() {
        return mod;
    }

    public int getY() {
        return y;
    }
}