package plasma.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

import net.minecraft.client.Minecraft;
import plasma.http.gsonobjs.ObjGlobalSettings;
import plasma.http.gsonobjs.ObjIsBanned;
import plasma.http.gsonobjs.ObjIsWhitelisted;
import plasma.http.gsonobjs.ObjUserCosmetics;

public class HTTPFunctions {
	private static final Gson gson = new Gson();
	
	public static void sendHWIDMap() {
		Minecraft mc = Minecraft.getMinecraft();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uuid", mc.getSession().getProfile().getId().toString()));
		params.add(new BasicNameValuePair("name", mc.getSession().getProfile().getName()));
		params.add(new BasicNameValuePair("hwid", HWID.get()));
		HTTPUtils.sendPostAsync(HTTPEndpoints.MAP_UUID, params);
	}
	
	public static boolean isAPIup() {
		HTTPReply reply = HTTPUtils.sendGet(HTTPEndpoints.BASE);
		if (reply.getStatusCode() == 200) {
			return true;
		} else {
			System.out.println("API is down! Status code: " + reply.getStatusCode());
			return false;
		}
		
	}
	public static boolean isBanned() {
		Minecraft mc = Minecraft.getMinecraft();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hwid", HWID.get()));
		HTTPReply reply = HTTPUtils.sendGet(HTTPEndpoints.IS_BANNED, params);
		if (reply.getStatusCode() == 200) {
			ObjIsBanned obj = gson.fromJson(reply.getBody(), ObjIsBanned.class);
			return obj.isBanned();
		} else {
			System.out.println("Error checking ban status: " + reply.getStatusCode());
			return false;
		}
	}
	public static boolean isWhitelisted() {
		Minecraft mc = Minecraft.getMinecraft();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("hwid", HWID.get()));
		HTTPReply reply = HTTPUtils.sendGet(HTTPEndpoints.IS_WHITELISTED, params);
		if (reply.getStatusCode() == 200) {
			ObjIsWhitelisted obj = gson.fromJson(reply.getBody(), ObjIsWhitelisted.class);
			return obj.isWhitelisted();
		} else {
			System.out.println("Error checking whitelist status: " + reply.getStatusCode());
			return false;
		}
	}
	public static ObjUserCosmetics[] downloadCosmetics() {
		return gson.fromJson(
			HTTPUtils.sendGet(HTTPEndpoints.COSMETICS).getBody(),
			ObjUserCosmetics[].class
		);
	}
	public static ObjGlobalSettings downloadGlobalSettings() {
		return gson.fromJson(
			HTTPUtils.sendGet(HTTPEndpoints.GLOBAL_SETTINGS).getBody(),
			ObjGlobalSettings.class
		);
	}
}
