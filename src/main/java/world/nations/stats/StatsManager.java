package world.nations.stats;

import java.util.List;

import world.nations.stats.data.FactionData;

public interface StatsManager {
	List<FactionData> getFactionsData();
	
	FactionData getFaction(String faction);
	
	void reloadFactionsData();

	void saveFactionsData();
	
	boolean contains(String faction);

}
