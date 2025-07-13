package plasma.mod;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import plasma.Client;
import plasma.hud.DraggableComponent;
import plasma.FileManager;

public class Mod {
    @Expose(serialize = false)
    public Minecraft mc = Minecraft.getMinecraft();

    @Expose(serialize = false)
    public FontRenderer fr = mc.fontRendererObj;

    public String name, description;

    public boolean enabled;

    @Expose(serialize = false)
    public Category category;

    @Expose(serialize = false)
    public boolean isHUD;

    @Expose(serialize = false)
    public DraggableComponent drag;

    public int x;
    public int y;

    public Mod(String name, String description, Category category, boolean isHUD, int x, int y) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.isHUD = isHUD;
        this.enabled = true;

        this.fr = this.mc.fontRendererObj;
        this.x = x;
        this.y = y;
        this.drag = new DraggableComponent(x, y, x + this.getWidth(), y + this.getHeight(), (new Color(0, 0, 0, 0)).getRGB());
    }

    public void onEnable() {
        Client.INSTANCE.eventManager.register(this);
    }
    public void onDisable() {
        Client.INSTANCE.eventManager.unregister(this);
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }
    public void toggle() {
        setEnabled(!this.enabled);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Category getCategory() {
        return category;
    }
    public boolean isHUD() {
        return isHUD;
    }

    public int getWidth() {
        return mc.fontRendererObj.getStringWidth(name) + 4;
    }

    public int getHeight() {
        return mc.fontRendererObj.FONT_HEIGHT + 4;
    }

    public int getX() {
        return drag.getxPosition();
    }
    public int getY() {
        return drag.getyPosition();
    }

    public DraggableComponent getDrag() {
        return new DraggableComponent(0, 0, getWidth(), getHeight(), new Color(0, 0, 0, 0).getRGB());
    }

    public void draw() {

    }

    public void renderDummy(int mouseX, int mouseY) {
        this.drag.draw(mouseX, mouseY);
    }

    // --- Persistence logic below ---

    public static class ModState {
        public String name;
        public int x;
        public int y;
        public boolean enabled;

        public ModState(String name, int x, int y, boolean enabled) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.enabled = enabled;
        }
    }

    public static void saveModsState(List<Mod> mods) {
        List<ModState> states = new ArrayList<>();
        for (Mod mod : mods) {
            states.add(new ModState(mod.getName(), mod.getX(), mod.getY(), mod.isEnabled()));
        }
        File file = new File(FileManager.getModsDir(), "mods_state.json");
        FileManager.writeJsonToFile(file, states);
    }

    public static void loadModsState(List<Mod> mods) {
        File file = new File(FileManager.getModsDir(), "mods_state.json");
        ModState[] states = FileManager.readJsonFromFile(file, ModState[].class);
        if (states == null) return;
        for (ModState state : states) {
            for (Mod mod : mods) {
                if (mod.getName().equals(state.name)) {
                    mod.x = state.x;
                    mod.y = state.y;
                    mod.setEnabled(state.enabled);
                    if (mod.drag != null) {
                        mod.drag.setxPosition(state.x);
                        mod.drag.setyPosition(state.y);
                    }
                }
            }
        }
    }
}
