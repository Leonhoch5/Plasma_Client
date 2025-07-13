package plasma;

import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;
import plasma.cosmetics.CosmeticsController;
import plasma.discord.DiscordRP;
import plasma.event.EventManager;
import plasma.event.EventTarget;
import plasma.event.events.ClientTick;
import plasma.gui.GuiHWIDBanned;
import plasma.gui.GuiWhitelisted;
import plasma.http.HTTPFunctions;
import plasma.http.gsonobjs.ObjGlobalSettings;
import plasma.hud.HUDConfigScreen;
import plasma.mod.Mod;
import plasma.mod.ModManager;
import plasma.util.SessionChanger;

public class Client {
		public String name = "Plasma Client", version = "1.0";
		public static Client INSTANCE = new Client();
		public Minecraft mc = Minecraft.getMinecraft();
		
		private DiscordRP discordRP = new DiscordRP();
		
		public EventManager eventManager;
		public ModManager modManager;

		
		
		public volatile boolean isBanned;
		public boolean isWhitelisted;
		public ObjGlobalSettings globalSettings;
		
		
		
		public void startup() {
		    FileManager.init();
		    eventManager = new EventManager();
		    modManager = new ModManager();
		    Display.setTitle(name + " | " + version);
		    EventManager.register(this);
		    discordRP.start();
		    
		    
		    
		   

		}

		public void shutdown() {
		    // Save mod states (positions, enabled status)
		    Mod.saveModsState(modManager.mods);
		    discordRP.shutdown();

		    EventManager.unregister(this);
		}
		public DiscordRP getDiscordRP() {
			return discordRP;
		}
		
		
		private boolean modsLoaded = false;

		@EventTarget
		public void onTick(ClientTick event) {
		/*	if (isBanned && !(mc.currentScreen instanceof GuiHWIDBanned)) {
		        mc.displayGuiScreen(new GuiHWIDBanned("You are banned from Plasma Client!"));
		        return;
		    }
			if (globalSettings.isWhitelisted && !isWhitelisted && !(mc.currentScreen instanceof GuiWhitelisted)) {
		        mc.displayGuiScreen(new GuiWhitelisted());
		    }
		    */
		    if (!modsLoaded && mc.thePlayer != null) {
		        Mod.loadModsState(modManager.mods);
		        modsLoaded = true;
		    }
		    if(mc.gameSettings.PLASMA_TEST.isPressed()) {
		        modManager.testMod.toggle();
		    }
		    if(mc.gameSettings.keyBindSprint.isPressed()) {
		        modManager.toggleSprint.toggle();
		    }
		    if(mc.gameSettings.HUD_CONFIG.isPressed()) {
		        mc.displayGuiScreen(new HUDConfigScreen());
		    }
		} 
		
}
