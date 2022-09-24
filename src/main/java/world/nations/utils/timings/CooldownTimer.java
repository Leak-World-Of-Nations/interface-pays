package world.nations.utils.timings;

import java.util.HashMap;

import com.massivecraft.factions.entity.Faction;

public class CooldownTimer {

	private static HashMap<String, HashMap<Faction, Long>> cooldown = new HashMap<String, HashMap<Faction, Long>>();
	
	public static void clearCooldowns() {
		cooldown.clear();
	}

	private static void createCooldown(final String k) {
		if (cooldown.containsKey(k)) {
			throw new IllegalArgumentException("Ce cooldown existe d\u00e9j\u00e0");
		}
		cooldown.put(k, new HashMap<Faction, Long>());
	}

	public static HashMap<Faction, Long> getCooldownMap(final String k) {
		if (cooldown.containsKey(k)) {
			return cooldown.get(k);
		}
		return null;
	}

	public static void addCooldown(final String k, final Faction faction, final int seconds) {
		if (!cooldown.containsKey(k)) {
			createCooldown(k);
		}
		final long next = System.currentTimeMillis() + seconds * 1000L;
		cooldown.get(k).put(faction, next);
	}

	public static boolean isOnCooldown(final String k, final Faction faction) {
		return cooldown.containsKey(k) && cooldown.get(k).containsKey(faction) && System.currentTimeMillis() <= cooldown.get(k).get(faction);
	}

	public static int getCooldownForPlayerInt(final String k, final Faction faction) {
		return (int) ((cooldown.get(k).get(faction) - System.currentTimeMillis()) / 1000L);
	}

	public static long getCooldownForPlayerLong(final String k, final Faction faction) {
		return cooldown.get(k).get(faction) - System.currentTimeMillis();
	}

	public static void removeCooldown(final String k, final Faction faction) {
		if (!cooldown.containsKey(k)) {
			throw new IllegalArgumentException(String.valueOf(String.valueOf(k)) + " n'existe pas");
		}
		cooldown.get(k).remove(faction);
	}
	
	public static void setPaused(String k, Faction faction, boolean paused) {
		if (paused) {
			removeCooldown(k, faction);			
		} else {
			int time = getCooldownForPlayerInt(k, faction);
			addCooldown(k, faction, time);
		}
	}
	
}