package world.nations.stats.data;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.massivecore.xlib.gson.reflect.TypeToken;

import world.nations.stats.StatsManager;
import world.nations.utils.json.FileUtils;
import world.nations.utils.json.Serialize;

public class StatsData implements StatsManager {
	
	private JavaPlugin plugin;
	
	private List<FactionData> datas = new ArrayList<FactionData>();
	
	public StatsData(JavaPlugin plugin) {
		this.plugin = plugin;
		this.reloadFactionsData();
	}

	@Override
	public List<FactionData> getFactionsData() {
		return this.datas;
	}
	
	@Override
	public FactionData getFaction(String faction) {
		for (FactionData data : datas)
			if (data.getFactionName().equalsIgnoreCase(faction))
				return data;

		FactionData data = new FactionData(faction);
		datas.add(data);
		return data;
	}
	
	@Override
	public boolean contains(String faction) {
		for (FactionData data : datas)
			if (data.getFactionName().equalsIgnoreCase(faction))
				return true;
		return false;
	}
	
	@Override
	public void reloadFactionsData() {
		//String json = FileUtils.loadFile(new File(plugin.getDataFolder(), "factions.json"));
		//FactionData[] enums = Serialize.deserializeFactionData(json);
		
		if (!(new File(plugin.getDataFolder(), "factions.json").exists())) {
			return;
		}
		
		String json = FileUtils.loadFile(new File(plugin.getDataFolder(), "factions.json"));
		Type type = (new TypeToken<List<FactionData>>() {}).getType();
		
	    this.datas = (List<FactionData>) Serialize.deserialize(json, type);
	    //System.out.println("[FactionsDatas] loaded " + this.datas.size() + " datas");
		
		//if (enums != null)
			//this.datas = new ArrayList<FactionData>(Arrays.asList(enums));		
	    if (!datas.isEmpty())
	    	this.datas.forEach(fac -> fac.setPoints(0));
	}

	@Override
	public void saveFactionsData() {
		String json = Serialize.serialize(this.datas);
		FileUtils.saveFile(new File(plugin.getDataFolder(), "factions.json"), json);
	    System.out.println("[FactionsDatas] saved " + this.datas.size() + " datas");
	}

}
