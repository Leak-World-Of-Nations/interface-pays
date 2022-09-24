package world.nations.mod;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;

import world.nations.Core;
import world.nations.assault.data.AssaultData;
import world.nations.bank.data.BankData;
import world.nations.stats.data.FactionData;

public class PacketFunctions {


	public static void getFactionList(ByteArrayDataInput in,FactionColl coll,ByteArrayDataOutput buf,Player p) {
		boolean existCountry = in.readBoolean();
    	FactionColl fColl = coll;

		int sizeFac = 0;

		for(Faction fac : fColl.getAll()) {
			if(fac != null && fac.getLeader() != null && !fac.getName().equalsIgnoreCase("Â§aWilderness") && !fac.getName().equalsIgnoreCase("warzone") && !fac.getName().equalsIgnoreCase("safezone")) {
				if(existCountry)
					sizeFac++;
			}
		}

		if(!existCountry)
			sizeFac += Core.availableCountries.size();


		buf.writeInt(sizeFac);

    	for(Faction fac : fColl.getAll()) {
    		if(fac != null && fac.getLeader() != null &&  !fac.getName().equalsIgnoreCase("Â§aWilderness") && !fac.getName().equalsIgnoreCase("warzone") && !fac.getName().equalsIgnoreCase("safezone")) {
    			if(existCountry) {
            		buf.writeUTF(fac.getName());
                	buf.writeBoolean(existCountry);
        			buf.writeInt(fac.getUPlayers().size());

        			long ageMillis = fac.getCreatedAtMillis() - System.currentTimeMillis();
        			LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(ageMillis, TimeUnit.getAllButMillis()), 3);
        			String ageString = TimeDiffUtil.formatedVerboose(ageUnitcounts, "<i>");

        			buf.writeUTF(ageString);
        		}
    		}
		}

    	if(!existCountry) {
    		for(String availableCountryName : Core.availableCountries.keySet()) {
    			buf.writeUTF(availableCountryName);
    			buf.writeBoolean(existCountry);
    		}
    	}


		p.sendPluginMessage(Core.plugin, "sendFList", buf.toByteArray());

	}

	public static void getFactionList(boolean exist,FactionColl coll,ByteArrayDataOutput buf,Player p) {
		boolean existCountry = exist;
		FactionColl fColl = coll;

		int sizeFac = 0;

		for(Faction fac : fColl.getAll()) {
			if(fac != null && fac.getLeader() != null && !fac.getName().equalsIgnoreCase("Â§aWilderness") && !fac.getName().equalsIgnoreCase("warzone") && !fac.getName().equalsIgnoreCase("safezone")) {
				if(existCountry)
					sizeFac++;
			}
		}

		if(!existCountry)
			sizeFac += Core.availableCountries.size();


		buf.writeInt(sizeFac);

    	for(Faction fac : fColl.getAll()) {
    		if(fac != null && fac.getLeader() != null &&  !fac.getName().equalsIgnoreCase("Â§aWilderness") && !fac.getName().equalsIgnoreCase("warzone") && !fac.getName().equalsIgnoreCase("safezone")) {
    			if(existCountry) {
            		buf.writeUTF(fac.getName());
                	buf.writeBoolean(existCountry);
        			buf.writeInt(fac.getUPlayers().size());

        			long ageMillis = fac.getCreatedAtMillis() - System.currentTimeMillis();
        			LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(ageMillis, TimeUnit.getAllButMillis()), 3);
        			String ageString = TimeDiffUtil.formatedVerboose(ageUnitcounts, "<i>");

        			buf.writeUTF(ageString);
        		}
    		}
		}

    	if(!existCountry) {
    		for(String availableCountryName : Core.availableCountries.keySet()) {
    			buf.writeUTF(availableCountryName);
    			buf.writeBoolean(existCountry);
    		}
    	}



		p.sendPluginMessage(Core.plugin, "sendFList", buf.toByteArray());
	}

	public static void getOnlinePlayersDatas(ByteArrayDataOutput buf,Player player) {
		buf.writeInt(Bukkit.getOnlinePlayers().length);

    	for(Player p : Bukkit.getOnlinePlayers()) {
    		UPlayer uPlayer = UPlayer.get(p.getUniqueId());

    		buf.writeUTF(p.getName());
    		buf.writeDouble(uPlayer.getPower());
    		buf.writeUTF(uPlayer.getFactionName());
    	}

    	player.sendPluginMessage(Core.plugin, "sendOPlayers", buf.toByteArray());

	}

	public static void getFactionInfo(ByteArrayDataOutput buf,FactionColl coll,String name,Player player) {
		// RÃ©cupÃ©rer la faction en fonction du nom de la fac
    	Faction targetFaction = coll.getByName(name);
    	buf.writeUTF(name);
    	buf.writeInt(targetFaction.getUPlayers().size());

    	buf.writeInt(targetFaction.getLandCount());
    	buf.writeDouble(targetFaction.getPowerMax());
    	buf.writeDouble(targetFaction.getPower());
    	buf.writeUTF(targetFaction.getLeader().getName());


    	buf.writeInt(targetFaction.getOnlinePlayers().size());
    	for(Player p : targetFaction.getOnlinePlayers()) {
    		UPlayer uPlayer = UPlayer.get(p.getUniqueId());
    		String prefix = uPlayer.getRole() == Rel.LEADER ? "**" : uPlayer.getRole() == Rel.OFFICER ? "*" : uPlayer.getRole() == Rel.MEMBER ? "+" : "-";
    		buf.writeUTF(prefix + p.getName());
    	}

    	ArrayList<UPlayer> leaders = (ArrayList<UPlayer>) targetFaction.getUPlayersWhereRole(Rel.LEADER);
    	ArrayList<UPlayer> officers = (ArrayList<UPlayer>) targetFaction.getUPlayersWhereRole(Rel.OFFICER);
    	ArrayList<UPlayer> members = (ArrayList<UPlayer>) targetFaction.getUPlayersWhereRole(Rel.MEMBER);
    	ArrayList<UPlayer> recruits = (ArrayList<UPlayer>) targetFaction.getUPlayersWhereRole(Rel.RECRUIT);

    	for(UPlayer p : leaders) {
    		String prefix = p.getRole() == Rel.LEADER ? "**" : p.getRole() == Rel.OFFICER ? "*" : p.getRole() == Rel.MEMBER ? "+" : "-";
    		buf.writeUTF(prefix + p.getName());
    	}

    	for(UPlayer p : officers) {
    		String prefix = p.getRole() == Rel.LEADER ? "**" : p.getRole() == Rel.OFFICER ? "*" : p.getRole() == Rel.MEMBER ? "+" : "-";
    		buf.writeUTF(prefix + p.getName());
    	}

    	for(UPlayer p : members) {
    		String prefix = p.getRole() == Rel.LEADER ? "**" : p.getRole() == Rel.OFFICER ? "*" : p.getRole() == Rel.MEMBER ? "+" : "-";
    		buf.writeUTF(prefix + p.getName());
    	}

    	for(UPlayer p : recruits) {
    		String prefix = p.getRole() == Rel.LEADER ? "**" : p.getRole() == Rel.OFFICER ? "*" : p.getRole() == Rel.MEMBER ? "+" : "-";
    		buf.writeUTF(prefix + p.getName());
    	}

    	long ageMillis = targetFaction.getCreatedAtMillis() - System.currentTimeMillis();
		LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(ageMillis, TimeUnit.getAllButMillis()), 3);
		String ageString = TimeDiffUtil.formatedVerboose(ageUnitcounts, "<i>");

		buf.writeBoolean(name.equalsIgnoreCase(UPlayer.get(player.getUniqueId()).getFactionName()));
		if(!name.equalsIgnoreCase(UPlayer.get(player.getUniqueId()).getFactionName())) {
			buf.writeUTF(targetFaction.getRelationTo(UPlayer.get(player.getUniqueId()).getFaction()).toString());
		}

		buf.writeUTF(ageString);

		buf.writeBoolean(UPlayer.get(player.getUniqueId()).hasFaction());

    	player.sendPluginMessage(Core.plugin, "sendFInfo", buf.toByteArray());
	}

	public static void getFactionAllies(FactionColl coll,ByteArrayDataOutput buf,String name,Player player) {
		Faction targetFaction = coll.getByName(name);

		ArrayList<Faction> allyFac = new ArrayList<Faction>();
		ArrayList<Faction> truceFac = new ArrayList<Faction>();

		for(Faction fac : coll.getAll()) {
			if(fac.getName().contains("SafeZone") || fac.getName().contains("Wilderness") || fac.getName().contains("WarZone"))
				continue;

			if(fac.getRelationTo(targetFaction) == Rel.ALLY)
				allyFac.add(fac);
			if(fac.getRelationTo(targetFaction) == Rel.TRUCE)
				truceFac.add(fac);
		}



    	buf.writeInt(allyFac.size());
    	for(Faction fac : allyFac)
    		buf.writeUTF(fac.getName());

    	buf.writeInt(truceFac.size());
    	for(Faction fac : truceFac)
    		buf.writeUTF(fac.getName());

    	player.sendPluginMessage(Core.plugin, "sendAllies", buf.toByteArray());
	}

	public static void getFactionStats(FactionColl coll,ByteArrayDataOutput buf,String name,Player player) {
		Faction fac = coll.getByName(name);

		// Recup le FactionData
		FactionData data = Core.plugin.getStatsManager().getFaction(fac.getName());

		if(data != null) {

			buf.writeInt(data.getKills());
			buf.writeInt(data.getDeaths());
			buf.writeUTF(data.getKDR());

			buf.writeInt(data.getWins());
			buf.writeInt(data.getLoses());
			buf.writeInt(data.getRatio());

			long ageMillis = fac.getCreatedAtMillis() - System.currentTimeMillis();
			LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(ageMillis, TimeUnit.getAllButMillis()), 3);
			String ageString = TimeDiffUtil.formatedVerboose(ageUnitcounts, "<i>");

			buf.writeUTF(ageString);

			if (Core.plugin.getEconomyManager().getFactionsNames().contains(fac.getName())) {
				buf.writeDouble(Core.plugin.getEconomyManager().getBalance(fac.getName()));
				System.out.println("account: " + Core.plugin.getEconomyManager().getBalance(fac.getName()));
			}
			else
				buf.writeDouble(0d);

			buf.writeInt(data.getPoints());

			player.sendPluginMessage(Core.plugin, "sendStats", buf.toByteArray());
		}
		else
			player.sendMessage("Â§cUne erreur s'est produite lors de l'ouverture du menu");
	}

	public static void getFactionWars(FactionColl coll,String facName,ByteArrayDataOutput buf,Player player) {
		Faction fac = coll.getByName(facName);

		ArrayList<AssaultData> assaults =new ArrayList<AssaultData>();
		ArrayList<Faction> ennemies =new ArrayList<Faction>();
		boolean hasPendingWars = false;

		for(String facId : fac.getRelationWishes().keySet()) {
			if(fac.getRelationWish(facId) == Rel.ENEMY)
				ennemies.add(coll.get(facId));
		}

		for(Faction ennemy : ennemies) {
			for(AssaultData data : Core.plugin.getAssaultManager().getAssaultList()) {
				if((data.getFactionName().equals(ennemy.getName()) && data.getFactionTarget().equals(fac.getName()))|| (data.getFactionTarget().equals(ennemy.getName())) && data.getFactionName().equals(fac.getName())) {
					if(data.getTimer().secondsLeft == 0)
						assaults.add(data);
					else
						hasPendingWars = true;
				}


			}
		}


		FactionData fData = Core.plugin.getStatsManager().getFaction(fac.getName());

		if(fData != null) {
			buf.writeInt(assaults.size());
			for(AssaultData data : assaults) {
				buf.writeUTF(data.getFactionName());
				buf.writeUTF(data.getFactionTarget());
				buf.writeInt(data.getPoints());
				buf.writeInt(data.getPointsTarget());
				buf.writeBoolean(data.getTimer().secondsLeft == 0);
				buf.writeUTF(data.getTimer().secondsLeft == 0 ? data.getPoints() > data.getPointsTarget() ? data.getFactionName() : data.getPointsTarget() > data.getPoints() ? data.getFactionTarget() : "" : "");

				buf.writeInt(data.getTimer().seconds);
			}

			buf.writeInt(ennemies.size());
			for(Faction facEnnemy : ennemies) {
				if(facEnnemy != null)
					buf.writeUTF(facEnnemy.getName());
			}


			buf.writeInt(fData.getWins());
			buf.writeInt(fData.getLoses());

			UPlayer uPlayer = UPlayer.get(player.getUniqueId());

			buf.writeBoolean(uPlayer != null && (uPlayer.getRole() == Rel.LEADER || uPlayer.getRole() == Rel.OFFICER));
			buf.writeBoolean(hasPendingWars);

			player.sendPluginMessage(Core.plugin, "sendWars", buf.toByteArray());
		}
		else
			player.sendMessage("Â§cUne erreur s'est produite lors de l'ouverture du menu");

	}

	public static void getFactionExchanges(FactionColl coll,String facName,ByteArrayDataOutput buf,Player player) {
		Faction fac = coll.getByName(facName);
		ArrayList<FactionExchange> facExchange = Core.factionExchanges.get(fac.getId());
		ArrayList<BankData> datas = getBankData(fac.getName());
		ArrayList<Player> owners = new ArrayList<Player>();


		for(BankData data : datas) {
			for(UUID uuid : data.getOwners()) {
				if(!owners.contains(Bukkit.getPlayer(uuid))) {
					owners.add(Bukkit.getPlayer(uuid));
				}
			}

		}

		if(facExchange != null) {
			buf.writeUTF(fac.getName());
			Core.plugin.getEconomyManager().getFactionsMap().get(0).getOwners();
			buf.writeInt(facExchange.size());
			for(FactionExchange exchange : facExchange) {
				buf.writeInt(exchange.amount);


				buf.writeUTF(exchange.sender);
				buf.writeUTF(exchange.getDate().toString());
			}

			if (Core.plugin.getEconomyManager().getFactionsNames().contains(fac.getName()))
				buf.writeInt((int)Core.plugin.getEconomyManager().getBalance(fac.getName()));
			else
				buf.writeInt(0);

			int ownersSize = 0;
			
			for(Player p : owners) {
				if(p == null || p.getDisplayName() == null)
					continue;
				
				ownersSize++;
			}
			
			buf.writeInt(ownersSize);

			for(Player p : owners) {
				if(p == null || p.getDisplayName() == null)
					continue;
				
				String name = p.getDisplayName();

				if(name.startsWith("§"))
					name = name.substring(2);

				if(name.substring(name.length() - 2,name.length() - 1).equals("§"))
					name = name.substring(0,name.length() - 2);


				buf.writeUTF(name);
			}

			player.sendPluginMessage(Core.plugin, "sendFExchanges", buf.toByteArray());
		}
	}

	private static ArrayList<BankData> getBankData(String facName) {
		 ArrayList<BankData> bankData = new ArrayList<BankData>();
		 for(BankData data : Core.plugin.getEconomyManager().getFactionsMap()) {
			 if(data.getFactionName().equalsIgnoreCase(facName)) {
				 bankData.add(data);
			 }
		 }

		 return bankData;
	}



}
