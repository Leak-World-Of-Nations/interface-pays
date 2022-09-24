package world.nations.bank;

import java.util.List;
import java.util.UUID;

import world.nations.bank.data.BankData;

public interface EconomyManager {

	List<BankData> getFactionsMap();
	List<String> getFactionsNames();
	
	double getBalance(String faction);
	
	double setBalance(String faction, double amount);
	
	double addBalance(String faction, double amount);
	
	double substractBalance(String faction, double amount);
	
	void addOwner(String faction, UUID uuid);
	
	void removeOwner(String faction, UUID uuid);

	void reloadEconomyData();

	void saveEconomyData();
}
