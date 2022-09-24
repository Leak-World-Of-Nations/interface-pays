package world.nations.utils;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Utils {

	public static final String LINE = "§7" + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);

	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static File getFormatedFile(JavaPlugin plugin, String fileName) {
		return new File(plugin.getDataFolder(), fileName);
	}
	
	public static Integer tryParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
