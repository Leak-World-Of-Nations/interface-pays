package world.nations.stats.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsNameChange;

import world.nations.Core;
import world.nations.stats.StatsManager;
import world.nations.stats.data.FactionData;

public class StatsListener implements Listener {
	
	private Core plugin;
	
	public StatsListener(Core plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory() != null && event.getInventory().getName() != null && event.getInventory().getName().contains("Pays")) {
            event.setCancelled(true);
        }
    }
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		 
		if (!(player.getKiller() instanceof Player)) {
			return;
		}

		Player target = player.getKiller();
		
		UPlayer fplayer = UPlayer.get(player);
		UPlayer ftarget = UPlayer.get(target);
		
		
		if (fplayer.hasFaction() && ftarget.hasFaction()) {
			StatsManager manager = this.plugin.getStatsManager();
			FactionData playerFaction = manager.getFaction(fplayer.getFactionName());
			FactionData targetFaction = manager.getFaction(ftarget.getFactionName());
			
			if (playerFaction == null || targetFaction == null) return;
			
			playerFaction.addDeath();
			targetFaction.addKill();
			
			if (playerFaction.getRatio() <= -30) {
				fplayer.getFaction().detach();
				new EventFactionsDisband(Bukkit.getConsoleSender(), ftarget.getFaction());
			} else if (targetFaction.getRatio() <= -30) {
				ftarget.getFaction().detach();
			}
		}
	}
	
	@EventHandler
	public void onCreateFaction(final EventFactionsCreate event) {
		StatsManager manager = this.plugin.getStatsManager();
		
		if (!manager.contains(event.getFactionName())) {
			manager.getFactionsData().add(new FactionData(event.getFactionName()));
		}
	}
	
	@EventHandler
	public void onDisbandFaction(final EventFactionsDisband event) {
		StatsManager manager = this.plugin.getStatsManager();
		
		if (manager.contains(event.getFaction().getName())) {
			manager.getFactionsData().remove(manager.getFaction(event.getFaction().getName()));
			this.plugin.getChestManager().removeChest(event.getFaction().getName());
		}
	}
	
	@EventHandler
	public void onNameChange(EventFactionsNameChange event) {
		StatsManager manager = this.plugin.getStatsManager();
		
		String fname = event.getFaction().getName();
		String oldFname = event.getNewName();
		
		if (manager.getFaction(oldFname) != null) {
			manager.getFaction(oldFname).setFactionName(fname);
		}
	}
}
