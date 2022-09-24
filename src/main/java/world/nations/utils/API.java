package world.nations.utils;

import com.massivecraft.factions.entity.Faction;

import world.nations.Core;
import world.nations.stats.data.FactionData;
import world.nations.utils.timings.CooldownTimer;

public class API {

	public static boolean isCountryInAssault(Faction faction) {
		if (CooldownTimer.isOnCooldown("assault", faction))
			return true;
		return false;
	}
	
	public static FactionData getFactionStats(String faction) {
		return Core.getPlugin().getStatsManager().getFaction(faction);
	}	
}
