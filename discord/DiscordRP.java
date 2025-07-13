package plasma.discord;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class DiscordRP {
	private boolean running = true;
	private long created = 0;
	
	public void start() {
		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
		@Override
		public void apply(DiscordUser user) {
				System.out.println("Discord RPC is ready! User: " + user.username + "#" + user.discriminator);
			}
		}).build();
		
		DiscordRPC.discordInitialize("1386428726878802021", handlers, true);
		
		new Thread(() -> {
			while (running) {
				DiscordRPC.discordRunCallbacks();
				if (created == 0) {
					created = System.currentTimeMillis();
				}
				long currentTime = System.currentTimeMillis();
				DiscordRPC.discordUpdatePresence(new DiscordRichPresence.Builder("Playing Plasma Client")
						.setDetails("In the game")
						.setStartTimestamps(created)
						.setBigImage("large_image", "Plasma Client")
						.build());
			}
		}).start();
	}
	public void shutdown() {
		running = false;
		DiscordRPC.discordShutdown();
		System.out.println("Discord RPC has been shut down.");
	}
	public void update(String firstLine, String secondLine) {
		DiscordRichPresence presence = new DiscordRichPresence.Builder(firstLine)
				.setDetails(secondLine)
				.setStartTimestamps(created)
				.setBigImage("large_image", "Plasma Client")
				.build();
		DiscordRPC.discordUpdatePresence(presence);
		System.out.println("Discord RPC updated: " + firstLine + " | " + secondLine);
	}
	
}
