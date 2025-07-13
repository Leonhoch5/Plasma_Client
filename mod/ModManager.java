package plasma.mod;

import java.util.ArrayList;

import plasma.hud.mod.impl.*;
import plasma.mod.impl.*;

public class ModManager {

    public TestMod testMod;
    public ToggleSprint toggleSprint;

    public ArrayList<Mod> mods;
  
    
    public TestModHUD testModHUD;
    public FPSMod fpsMod;
    public TargetHUD targetHud;
    public CPSMod cpsMod;

    public ModManager() {
        mods = new ArrayList<>();
   

        // Regular mods
        mods.add(testMod = new TestMod());
        mods.add(toggleSprint = new ToggleSprint());
        // HUD mods
        mods.add(testModHUD = new TestModHUD());
        mods.add(fpsMod = new FPSMod());
        mods.add(targetHud = new TargetHUD());
        mods.add(cpsMod = new CPSMod());
    
    }
	public void renderMods() {

		for (Mod mod : mods) {
			if (mod.isEnabled()) {
				mod.draw();
			}
		}		
	}
}
