package plasma.mod.impl;

import plasma.mod.Category;
import plasma.mod.Mod;

public class TestMod extends Mod{

	public TestMod() {
		super("TestMod", "Test Mod", Category.MISC, false, 0 ,0);
	}
	@Override
	public void onEnable() {
		super.onEnable();
		
		System.out.println("I WORK!!!");
	}
}
	