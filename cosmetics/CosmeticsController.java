package plasma.cosmetics;

import net.minecraft.client.entity.AbstractClientPlayer;
import plasma.http.HTTPFunctions;
import plasma.http.gsonobjs.ObjUserCosmetics;

public class CosmeticsController {

	
	private static ObjUserCosmetics[] userCosmetics;
	
	public static boolean shouldRenderTopHat(AbstractClientPlayer player) {
		ObjUserCosmetics uc = getUserCosmetics(player);
		if (uc == null) {
			return false;
		}
		return uc.getHat().isEnabled();
	}
	
	public static float[] getTopHatColor(AbstractClientPlayer player) {
		ObjUserCosmetics uc = getUserCosmetics(player);
		if (uc == null) {
			return new float[] {1, 1, 1}; // Default color
		}
		return uc.getHat().getColor();
	}
	
	public static boolean shouldRenderEyes(AbstractClientPlayer player) {
		ObjUserCosmetics uc = getUserCosmetics(player);
		if (uc == null) {
			return false;
		}
		return uc.isGooglyEyesEnabled();	}
	
	public static ObjUserCosmetics getUserCosmetics(AbstractClientPlayer player) {
		for (ObjUserCosmetics uc : userCosmetics) {
			if (player.getGameProfile().getId().equals(uc.getUuid())) {
				return uc;
			}
		}
		return null;
	}
	
	public static void downloadUserCosmetics() {
		userCosmetics = HTTPFunctions.downloadCosmetics();
	}
	
}
// 42