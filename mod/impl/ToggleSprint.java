package plasma.mod.impl;

import plasma.mod.Category;
import plasma.mod.Mod;

public class ToggleSprint extends Mod{

	public ToggleSprint() {
		super("Sprint Toggle", "Toggles Sprint whenever possible", Category.MISC, false, 0, 0);
	}
	@Override
	public void onEnable() {
		if(this.isEnabled() && !mc.thePlayer.isBlocking() && !mc.thePlayer.isSneaking()) {
			mc.thePlayer.setSprinting(true);
		}
	}
	@Override
	public void onDisable() {
		mc.thePlayer.setSprinting(false);
		super.onDisable();
	}
}
	