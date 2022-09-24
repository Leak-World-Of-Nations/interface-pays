package world.nations.bank.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import world.nations.bank.EconomyManager;
import world.nations.utils.json.FileUtils;
import world.nations.utils.json.Serialize;

public class EconomyData implements EconomyManager {
	
	private final JavaPlugin plugin;
	
	private List<BankData> factionsMap = new ArrayList<>();
	private List<String> factionsNames = new ArrayList<>();
	
	public EconomyData(JavaPlugin plugin) {
        this.plugin = plugin;
        this.reloadEconomyData();
    }
	
	@Override
	public List<BankData> getFactionsMap() {
		return this.factionsMap;
	}

	@Override
	public List<String> getFactionsNames() {
		return this.factionsNames;
	}

	@Override
	public double getBalance(String faction) {
		for (BankData object : factionsMap) {
			if (object.getFactionName().equals(faction)) {
				return object.getFactionBank();
			}
		}
		return 0;
	}

	@Override
	public double setBalance(String faction, double amount) {
		for (BankData object : factionsMap) {
			if (object.getFactionName().equals(faction)) {
				object.setFactionBank(amount);
				return amount;
			}
		}
		return 0;
	}

	@Override
	public double addBalance(String faction, double amount) {
		for (BankData object : factionsMap) {
			if (object.getFactionName().equals(faction)) {
				return setBalance(faction, getBalance(faction) + amount);
			}
		}
		return 0;
	}

	@Override
	public double substractBalance(String faction, double amount) {
		for (BankData object : factionsMap) {
			if (object.getFactionName().equals(faction)) {
				return setBalance(faction, getBalance(faction) - amount);
			}
		}
		return 0;
	}

	@Override
	public void addOwner(String faction, UUID uuid) {
		for (BankData object : factionsMap) {
			if (object.getFactionName().equals(faction)) {
				object.getOwners().add(uuid);
			}
		}
	}
	
	@Override
	public void removeOwner(String faction, UUID uuid) {
		for (BankData object : factionsMap) {
			if (object.getFactionName().equals(faction)) {
				object.getOwners().removeIf(id -> (id == uuid));
			}
		}
	}

	@Override
	public void reloadEconomyData() {
		String json = FileUtils.loadFile(new File(plugin.getDataFolder(), "bank.json"));
		BankData[] enums = Serialize.deserializeBankData(json);
		
		if (enums != null)
			this.factionsMap = new ArrayList<BankData>(Arrays.asList(enums));
		
		if (this.factionsMap == null)
			this.factionsMap = new ArrayList<BankData>();
		
		if (!this.factionsMap.isEmpty())
			for (BankData obj : this.factionsMap)
				this.factionsNames.add(obj.getFactionName());
	}

	@Override
	public void saveEconomyData() {
		String json = Serialize.serialize(this.factionsMap);
		FileUtils.saveFile(new File(plugin.getDataFolder(), "bank.json"), json);
	}

}
