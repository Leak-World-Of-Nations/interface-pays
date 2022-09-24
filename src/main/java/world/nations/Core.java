package world.nations;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.cmd.CmdFactionsClaim;
import com.massivecraft.factions.entity.UConf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import world.nations.assault.AssaultManager;
import world.nations.assault.command.AssaultCommand;
import world.nations.bank.EconomyManager;
import world.nations.bank.command.EconomyCommand;
import world.nations.bank.data.BankData;
import world.nations.bank.data.EconomyData;
import world.nations.bank.listener.FactionListener;
import world.nations.chest.VirtualChestManager;
import world.nations.chest.commands.ChestCommand;
import world.nations.command.UpdateCommand;
import world.nations.mod.FactionExchange;
import world.nations.mod.PacketListener;
import world.nations.mod.commands.CountryAddCommand;
import world.nations.stats.StatsManager;
import world.nations.stats.command.StatsCommand;
import world.nations.stats.data.StatsData;
import world.nations.stats.listener.StatsListener;
import world.nations.utils.command.CommandFramework;
import world.nations.utils.json.FileUtils;
import world.nations.utils.timer.TimerManager;

public class Core extends JavaPlugin {
    public static Core plugin;

    @Getter
    private CommandFramework framework;
    @Getter
    private TimerManager timerManager;
    @Getter
    private AssaultManager assaultManager;
    @Getter
    private VirtualChestManager chestManager;

    @Getter
    private EconomyManager economyManager;
    @Getter
    private StatsManager statsManager;

    private Economy econ = null;


    public static HashMap<String, ArrayList<FactionExchange>> factionExchanges = new HashMap<String, ArrayList<FactionExchange>>();
    public static HashMap<String, Location> availableCountries = new HashMap<String, Location>();

    private File facFile, countriesFile/*,lootboxFile*/;
    private YamlConfiguration facConfig, countriesConfig/*,lootboxConfig*/;

    //public ArrayList<LootboxItem> lootboxItems = new ArrayList<LootboxItem>();

    @SuppressWarnings("unlikely-arg-type")
    @Override
    public void onEnable() {
        plugin = this;

        if (!setupEconomy()) {
            getServer().getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.saveDefaultConfig();

        this.registerManager();
        this.registerListener();
        this.registerCommand();
        this.registerPackets();

        this.getCommand("country").setExecutor(new CountryAddCommand());
        //	this.getCommand("lootbox").setExecutor(new LootBoxCommand());

        Factions.get().getOuterCmdFactions().addSubCommand(new AssaultCommand(this));
        //Factions.get().getOuterCmdFactions().addSubCommand(new AllianceCommand(this));
        Factions.get().getOuterCmdFactions().addSubCommand(new StatsCommand(this));

        facFile = new File(getDataFolder(), "exchanges.yml");
        countriesFile = new File(getDataFolder(), "countries.yml");
        //lootboxFile = new File(getDataFolder(),"lootbox.yml");

        try {
            FileUtils.createFile(facFile);
            FileUtils.createFile(countriesFile);
            //FileUtils.createFile(lootboxFile);
        } catch (IOException e) {
        }

        facConfig = YamlConfiguration.loadConfiguration(facFile);
        countriesConfig = YamlConfiguration.loadConfiguration(countriesFile);
        //lootboxConfig = YamlConfiguration.loadConfiguration(lootboxFile);

        initDatas();


        for (BankData data : getEconomyManager().getFactionsMap()) {
            FactionColl coll = PacketListener.getFactionColl("factions_faction@default");
            //System.out.println(data.getOwners().contains(coll.getByName(data.getFactionName()).getLeader().getName()));
            if (coll.getByName(data.getFactionName()) != null && !data.getOwners().contains(coll.getByName(data.getFactionName()).getLeader().getName())) {
                data.getOwners().add(coll.getByName(data.getFactionName()).getLeader().getUuid());
            }

        }

        super.onEnable();
    }
    @Override
    public void onDisable() {
        this.statsManager.saveFactionsData();
        this.chestManager.save();
        this.timerManager.saveTimerData();
        this.economyManager.saveEconomyData();

        facFile.delete();
        countriesFile.delete();
        //	lootboxFile.delete();

        try {
            FileUtils.createFile(facFile);
            FileUtils.createFile(countriesFile);
            //FileUtils.createFile(lootboxFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int index = 0;
        for (String id : factionExchanges.keySet()) {
            facConfig.set("factions." + id + ".exchanges." + index + ".id", id);

            int indexExchange = 0;
            for (FactionExchange exchange : factionExchanges.get(id)) {
                facConfig.set("factions." + id + ".exchanges." + index + ".exchangesList." + indexExchange + ".sender",
                        exchange.getSender());
                facConfig.set("factions." + id + ".exchanges." + index + ".exchangesList." + indexExchange + ".amount",
                        exchange.getAmount());
                facConfig.set("factions." + id + ".exchanges." + index + ".exchangesList." + indexExchange + ".date",
                        exchange.getDate().toString());
                indexExchange++;
            }

            index++;
        }

        int indexCountries = 0;
        for (String countryName : availableCountries.keySet()) {
            Location loc = availableCountries.get(countryName);
            countriesConfig.set("countries." + indexCountries + ".name", countryName);
            countriesConfig.set("countries." + indexCountries + ".pos.world", loc.getWorld().getName());
            countriesConfig.set("countries." + indexCountries + ".pos.x", loc.getX());
            countriesConfig.set("countries." + indexCountries + ".pos.y", loc.getY());
            countriesConfig.set("countries." + indexCountries + ".pos.z", loc.getZ());
            indexCountries++;
        }

		/*int indexLBItem = 0;
		for(LootboxItem item : lootboxItems) {
			String key = "lbItems." + indexLBItem;

			lootboxConfig.set(key + ".lootboxID", item.getLootboxID());
			lootboxConfig.set(key + ".percentage", item.getPercentage());
			lootboxConfig.set(key + ".item", item.getItemToDrop());

			indexLBItem++;
		}
*/


        try {
            facConfig.save(facFile);
            countriesConfig.save(countriesFile); /*lootboxConfig.save(lootboxFile);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDisable();
    }

    private void registerPackets() {

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "factionData");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "sendFList");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "sendOPlayers");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "sendFInfo");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "sendFExchanges");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "sendAllies");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "sendWars");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "sendStats");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "displayInvite");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "sendBox");

        Bukkit.getMessenger().registerIncomingPluginChannel(this, "getOPlayers", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "setLeader", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "getFList", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "getFInfo", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "getFExchanges", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "getAllies", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "getWars", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "getStats", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "createFac", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "getRedBox", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "greenBox", new PacketListener());
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "getBlackBox", new PacketListener());
    }

    private final void registerCommand() {
        this.framework.registerCommands(new EconomyCommand(this));
        this.framework.registerCommands(new UpdateCommand(this));
        this.framework.registerCommands(new ChestCommand(chestManager));
    }

    private final void registerManager() {
        this.framework = new CommandFramework(this);
        this.timerManager = new TimerManager(this);
        this.economyManager = new EconomyData(this);
        this.statsManager = new StatsData(this);
        this.assaultManager = new AssaultManager(this);

        File chestFolder = new File(getDataFolder(), "chests");
        this.chestManager = new VirtualChestManager(chestFolder, getLogger());
    }

    private final void registerListener() {
        PluginManager manager = this.getServer().getPluginManager();

        manager.registerEvents(new FactionListener(this), this);
        manager.registerEvents(new StatsListener(this), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    public static Core getPlugin() {
        return plugin;
    }

    private void initDatas() {
        FactionColl coll = PacketListener.getFactionColl("factions_faction@default");

	/*	if(lootboxConfig.getConfigurationSection("lbItems") != null) {
			for(String id : lootboxConfig.getConfigurationSection("lbItems").getKeys(false)) {
				int lootboxID = lootboxConfig.getInt("lbItems." + id + ".lootboxID");
				int percentage = lootboxConfig.getInt("lbItems." + id + ".percentage");
				ItemStack item = lootboxConfig.getItemStack("lbItems." + id + ".item");

				LootboxItem lbItem = new LootboxItem(lootboxID,percentage,item);
				lootboxItems.add(lbItem);
			}
		}
*/
        if (facConfig.getConfigurationSection("factions") != null) {
            for (String id : facConfig.getConfigurationSection("factions").getKeys(false)) {
                String keyFac = "factions." + id;

                if (facConfig.isSet(keyFac + ".exchanges")) {
                    for (String exchangeid : facConfig.getConfigurationSection(keyFac + ".exchanges").getKeys(false)) {
                        String key2 = keyFac + ".exchanges." + exchangeid;
                        String exchangeID = facConfig.getString(key2 + ".id");
                        ArrayList<FactionExchange> allExchanges = new ArrayList<FactionExchange>();

                        if (facConfig.getConfigurationSection(key2 + ".exchangesList") != null) {
                            for (String id2 : facConfig.getConfigurationSection(key2 + ".exchangesList").getKeys(false)) {
                                String key3 = key2 + ".exchangesList." + id2;

                                String sender = facConfig.getString(key3 + ".sender");
                                int amount = facConfig.getInt(key3 + ".amount");
                                LocalDateTime date = LocalDateTime.parse(facConfig.getString(key3 + ".date"));


                                FactionExchange exchange = new FactionExchange(amount, sender, date);
                                allExchanges.add(exchange);
                            }

                            factionExchanges.put(exchangeID, allExchanges);
                        }
                    }
                }
            }
        }

        if (countriesConfig.getConfigurationSection("countries") != null) {
            for (String countryID : countriesConfig.getConfigurationSection("countries").getKeys(false)) {
                String key = "countries." + countryID;

                String countryName = countriesConfig.getString(key + ".name");
                String worldName = countriesConfig.getString(key + ".pos.world");
                World world = Bukkit.getWorld(worldName);
                int x = countriesConfig.getInt(key + ".pos.x");
                int y = countriesConfig.getInt(key + ".pos.y");
                int z = countriesConfig.getInt(key + ".pos.z");

                Location loc = new Location(world, x, y, z);

                availableCountries.put(countryName, loc);
            }
        }


        for (Faction fac : coll.getAll()) {
            if (!fac.getName().equalsIgnoreCase("§aWilderness") && !fac.getName().equalsIgnoreCase("warzone") && !fac.getName().equalsIgnoreCase("safezone")) {
                if (!factionExchanges.containsKey(fac.getId()))
                    factionExchanges.put(fac.getId(), new ArrayList<>());
            }
        }
    }
}
