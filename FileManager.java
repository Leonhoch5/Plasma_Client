package plasma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;

public class FileManager {
	private static Gson gson = new Gson();
	
	private static File ROOT_DIR = new File("Plasma Client");
	private static File MODS_DIR = new File(ROOT_DIR, "Mods");
	
	public static void init() {
		if (!ROOT_DIR.exists()) {
			ROOT_DIR.mkdir();
		}
		if (!MODS_DIR.exists()) {
			MODS_DIR.mkdir();
		}
	}
	public static Gson getGson() {
		return gson;
	}
	
	public static File getModsDir() {
		return MODS_DIR;
	}
	
	public static boolean writeJsonToFile(File file, Object object) {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(gson.toJson(object).getBytes());
			fos.flush();
			fos.close();
			
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static <T extends Object> T readJsonFromFile(File file, Class<T> c) {
		try {
			
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			isr.close();
			fis.close();
			
			return gson.fromJson(sb.toString(), c);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
}
