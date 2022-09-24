package world.nations.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer {
	public static List<String> stringifyLocation(final List<Location> location) {
		final List<String> list = new ArrayList<String>();
		location.forEach(loc -> list.add(stringifyLocation(loc)));
		return list;
	}

	public static List<Location> destringifyLocation(final List<String> location) {
		final List<Location> list = new ArrayList<Location>();
		location.forEach(string -> list.add(destringifyLocation(string)));
		return list;
	}

	public static String stringifyLocation(final Location location) {
		return "[" + location.getWorld().getName() + "," + location.getX() + "," + location.getY() + ","
				+ location.getZ() + "]";
	}

	public static Location destringifyLocation(final String string) {
		final String[] split = string.substring(1, string.length() - 2).split(",");
		final World world = Bukkit.getWorld(split[0]);
		if (world == null) {
			return null;
		}
		final double x = Double.parseDouble(split[1]);
		final double y = Double.parseDouble(split[2]);
		final double z = Double.parseDouble(split[3]);
		return new Location(world, x, y, z);
	}

	public static String stringifyDeepLocation(final Location location) {
		return "[" + location.getWorld().getName() + "," + location.getX() + "," + location.getY() + ","
				+ location.getZ() + "," + location.getYaw() + "," + location.getPitch() + "]";
	}

	public static Location destringifyDeepLocation(final String string) {
		final String[] split = string.substring(1, string.length() - 2).split(",");
		final World world = Bukkit.getWorld(split[0]);
		if (world == null) {
			return null;
		}
		final double x = Double.parseDouble(split[1]);
		final double y = Double.parseDouble(split[2]);
		final double z = Double.parseDouble(split[3]);
		final float yaw = Float.parseFloat(split[4]);
		final float pitch = Float.parseFloat(split[5]);
		return new Location(world, x, y, z, yaw, pitch);
	}
}