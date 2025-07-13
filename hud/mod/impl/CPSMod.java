package plasma.hud.mod.impl;

import org.lwjgl.input.Mouse;
import plasma.mod.Category;
import plasma.mod.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CPSMod extends Mod {
    private final List<Long> clickTimestamps = new ArrayList<>();
    private boolean wasPressed = false;

    public CPSMod() {
        super("CPSMod", "CPS Mod", Category.HUD, true, 10, 40);
    }

    // Poll for mouse clicks
    private void checkMouseClick() {
        boolean isPressed = Mouse.isButtonDown(0);  // Left mouse button (0)

        if (isPressed && !wasPressed) {
            clickTimestamps.add(System.currentTimeMillis());
        }

        wasPressed = isPressed;
    }

    private int calculateCPS() {
        long currentTime = System.currentTimeMillis();

        // Remove clicks older than 1 second
        Iterator<Long> iterator = clickTimestamps.iterator();
        while (iterator.hasNext()) {
            if (currentTime - iterator.next() > 1000) {
                iterator.remove();
            }
        }

        return clickTimestamps.size();
    }

    @Override
    public void draw() {
        checkMouseClick();
        int cps = calculateCPS();

        fr.drawString("CPS: " + cps, getX() + 2, getY() + 2, -1);
        super.draw();
    }

    @Override
    public void renderDummy(int mouseX, int mouseY) {
        fr.drawString("CPS Mod", getX() + 2, getY() + 2, -1);  // Example dummy CPS
        super.renderDummy(mouseX, mouseY);
    }

    @Override
    public int getWidth() {
        return 50;
    }

    @Override
    public int getHeight() {
        return fr.FONT_HEIGHT;
    }
}
