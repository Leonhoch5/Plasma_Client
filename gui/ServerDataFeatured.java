package plasma.gui;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ResourceLocation;

public class ServerDataFeatured extends ServerData {
	public static final ResourceLocation FEATURED_ICON = new ResourceLocation("client/heartFull.png");

	public ServerDataFeatured(String serverName, String serverIP) {
		super(serverName, serverIP, false);
	}
	
}
