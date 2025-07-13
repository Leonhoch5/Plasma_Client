package plasma.event.events;

import net.minecraft.world.World;
import plasma.event.Event;

public class WorldUnloadEvent extends Event {

	public final World world; 
	public WorldUnloadEvent(World world) {
		this.world = world;
	}
	public World getWorld() {
		return world;
	}
}
