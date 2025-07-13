package plasma.gui.clickgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Mouse;
import plasma.Client;
import plasma.gui.clickgui.comp.ModButton;
import plasma.mod.Mod;
import plasma.mod.Category;

public class ClickGUI extends GuiScreen {
    // Responsive layout variables
    private int PANEL_WIDTH;
    private int PANEL_HEIGHT;
    private int HEADER_HEIGHT = 60;
    private int SEARCH_HEIGHT = 40;
    private int CATEGORY_WIDTH = 150;
    private int BUTTON_WIDTH;
    private int BUTTON_HEIGHT = 36;
    private int BUTTON_GAP = 8;
    private int BUTTONS_PER_ROW;
    
    // Color scheme
    private static final Color PANEL_BG = new Color(49, 54, 59, 230);
    private static final Color HEADER_BG = new Color(61, 174, 233, 220);
    private static final Color SEARCH_BG = new Color(35, 38, 41, 200);
    private static final Color CATEGORY_BG = new Color(43, 47, 51, 200);
    private static final Color CATEGORY_SELECTED = new Color(61, 174, 233, 150);
    private static final Color SCROLL_BAR = new Color(61, 174, 233, 180);
    private GuiTextField searchField;
    private final Map<Category, List<ModButton>> categorizedButtons = new HashMap<>();
    private List<ModButton> visibleButtons = new ArrayList<>();
    private Category selectedCategory = Category.ALL;
    private int panelX, panelY;
    private int scrollOffset = 0;
    private int maxScroll = 0;

    @Override
    public void initGui() {
        calculateDimensions();
        categorizedButtons.clear();
        visibleButtons.clear();

        // Center the panel
        panelX = (this.width - PANEL_WIDTH) / 2;
        panelY = (this.height - PANEL_HEIGHT) / 2;

        // Initialize search field
        this.searchField = new GuiTextField(0, this.fontRendererObj, 
            panelX + CATEGORY_WIDTH + 20, panelY + HEADER_HEIGHT + 10, 
            PANEL_WIDTH - CATEGORY_WIDTH - 40, SEARCH_HEIGHT - 20);
        this.searchField.setMaxStringLength(100);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setTextColor(0xFFFFFFFF);

        // Initialize categories
        for (Category category : Category.values()) {
            categorizedButtons.put(category, new ArrayList<>());
        }

        // Categorize mods
        for (Mod mod : Client.INSTANCE.modManager.mods) {
            ModButton button = new ModButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT, mod);
            categorizedButtons.get(mod.getCategory()).add(button);
            categorizedButtons.get(Category.ALL).add(button);
            
            if (mod.isHUD()) {
                categorizedButtons.get(Category.HUD).add(button);
            }
        }

        updateVisibleButtons();
    }

    private void calculateDimensions() {
        // Responsive sizing
        PANEL_WIDTH = Math.min(700, this.width - 40);
        PANEL_HEIGHT = Math.min(550, this.height - 40);
        
        // Calculate buttons per row based on available width
        int contentWidth = PANEL_WIDTH - CATEGORY_WIDTH - 40;
        BUTTON_WIDTH = 120;
        BUTTONS_PER_ROW = Math.max(2, contentWidth / (BUTTON_WIDTH + BUTTON_GAP));
        
        // Adjust button width to fill available space
        BUTTON_WIDTH = (contentWidth - (BUTTONS_PER_ROW - 1) * BUTTON_GAP) / BUTTONS_PER_ROW;
    }

    private void updateVisibleButtons() {
        visibleButtons.clear();
        
        // Get buttons from selected category
        List<ModButton> categoryButtons = new ArrayList<>(categorizedButtons.get(selectedCategory));
        
        // Apply search filter
        String searchText = searchField.getText().toLowerCase();
        if (!searchText.isEmpty()) {
            for (ModButton button : categoryButtons) {
                if (button.getMod().getName().toLowerCase().contains(searchText)) {
                    visibleButtons.add(button);
                }
            }
        } else {
            visibleButtons.addAll(categoryButtons);
        }
        
        // Calculate grid layout
        int rows = (int) Math.ceil((double)visibleButtons.size() / BUTTONS_PER_ROW);
        int contentHeight = rows * (BUTTON_HEIGHT + BUTTON_GAP) - BUTTON_GAP;
        int availableHeight = PANEL_HEIGHT - HEADER_HEIGHT - SEARCH_HEIGHT - 20;
        maxScroll = Math.max(0, contentHeight - availableHeight);
        scrollOffset = Math.min(scrollOffset, maxScroll);
        
        // Position buttons in grid
        int startX = panelX + CATEGORY_WIDTH + 20;
        int startY = panelY + HEADER_HEIGHT + SEARCH_HEIGHT + 20 - scrollOffset;
        
        for (int i = 0; i < visibleButtons.size(); i++) {
            int row = i / BUTTONS_PER_ROW;
            int col = i % BUTTONS_PER_ROW;
            
            int x = startX + col * (BUTTON_WIDTH + BUTTON_GAP);
            int y = startY + row * (BUTTON_HEIGHT + BUTTON_GAP);
            
            visibleButtons.get(i).setPosition(x, y);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Draw panel background
        drawRect(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, PANEL_BG.getRGB());
        
        // Draw header
        drawRect(panelX, panelY, panelX + PANEL_WIDTH, panelY + HEADER_HEIGHT, HEADER_BG.getRGB());
        fontRendererObj.drawString("Plasma Mod Manager", panelX + 24, panelY + 20, 0xFFFFFFFF);
        
        // Draw search bar
        drawRect(panelX + CATEGORY_WIDTH, panelY + HEADER_HEIGHT, 
                panelX + PANEL_WIDTH, panelY + HEADER_HEIGHT + SEARCH_HEIGHT, SEARCH_BG.getRGB());
        searchField.drawTextBox();
        
        // Draw category sidebar
        drawRect(panelX, panelY + HEADER_HEIGHT, 
                panelX + CATEGORY_WIDTH, panelY + PANEL_HEIGHT, CATEGORY_BG.getRGB());
        
        // Draw categories
        int categoryY = panelY + HEADER_HEIGHT + 20;
        for (Category category : Category.values()) {
            if (category == selectedCategory) {
                drawRect(panelX + 10, categoryY - 5, 
                        panelX + CATEGORY_WIDTH - 10, categoryY + 25, CATEGORY_SELECTED.getRGB());
            }
            fontRendererObj.drawString(category.toString(), panelX + 20, categoryY, 0xFFFFFFFF);
            categoryY += 30;
        }
        
        // Draw scrollable content area
        int contentStartY = panelY + HEADER_HEIGHT + SEARCH_HEIGHT;
        int contentEndY = panelY + PANEL_HEIGHT;
        
        // Draw scroll fade effects
        this.drawGradientRect(panelX + CATEGORY_WIDTH, contentStartY, 
                            panelX + PANEL_WIDTH, contentStartY + 10, 0xAA000000, 0x00000000);
        this.drawGradientRect(panelX + CATEGORY_WIDTH, contentEndY - 10, 
                            panelX + PANEL_WIDTH, contentEndY, 0x00000000, 0xAA000000);
        
        // Draw visible buttons
        for (ModButton button : visibleButtons) {
            if (button.getY() + BUTTON_HEIGHT >= contentStartY && button.getY() <= contentEndY) {
                button.draw();
            }
        }
        
        // Draw scroll bar if needed
        if (maxScroll > 0) {
            int scrollAreaHeight = PANEL_HEIGHT - HEADER_HEIGHT - SEARCH_HEIGHT;
            int scrollBarHeight = (int)(scrollAreaHeight * (scrollAreaHeight / (float)(maxScroll + scrollAreaHeight)));
            int scrollBarY = (int)(contentStartY + (scrollAreaHeight - scrollBarHeight) * (scrollOffset / (float)maxScroll));
            
            drawRect(panelX + PANEL_WIDTH - 6, scrollBarY, 
                    panelX + PANEL_WIDTH - 2, scrollBarY + scrollBarHeight, SCROLL_BAR.getRGB());
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        searchField.mouseClicked(mouseX, mouseY, mouseButton);
        
        // Check category selection
        if (mouseX >= panelX && mouseX <= panelX + CATEGORY_WIDTH && 
            mouseY >= panelY + HEADER_HEIGHT && mouseY <= panelY + PANEL_HEIGHT) {
            int categoryY = panelY + HEADER_HEIGHT + 20;
            for (Category category : Category.values()) {
                if (mouseY >= categoryY - 5 && mouseY <= categoryY + 25) {
                    selectedCategory = category;
                    scrollOffset = 0;
                    updateVisibleButtons();
                    return;
                }
                categoryY += 30;
            }
        }
        
        // Check mod buttons
        for (ModButton button : visibleButtons) {
            if (button.isMouseOver(mouseX, mouseY)) {
                button.onClick(mouseX, mouseY, mouseButton);
                return;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (searchField.textboxKeyTyped(typedChar, keyCode)) {
            updateVisibleButtons();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            if (mouseX >= panelX + CATEGORY_WIDTH && mouseX <= panelX + PANEL_WIDTH && 
                mouseY >= panelY + HEADER_HEIGHT + SEARCH_HEIGHT && mouseY <= panelY + PANEL_HEIGHT) {
                scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (scroll > 0 ? 20 : -20)));
                updateVisibleButtons();
            }
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        searchField.updateCursorCounter();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        scrollOffset = 0;
    }
}