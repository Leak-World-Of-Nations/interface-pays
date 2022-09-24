package world.nations.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import com.massivecraft.factions.FactionListComparator;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;

import world.nations.Core;
import world.nations.stats.data.FactionData;
import world.nations.utils.command.Command;
import world.nations.utils.command.CommandArgs;

public class UpdateCommand {

	private Core plugin;
	
	public UpdateCommand(Core plugin) {
		this.plugin = plugin;
	}
	
	@Command(name = "fupdate", permission = "fupdate.use", inGameOnly = false)
	public void onCommand(CommandArgs args) {
		ArrayList<Faction> factionList = new ArrayList<Faction>(FactionColls.get().get(args.getSender()).getAll(null, FactionListComparator.get()));
		
		factionList.forEach(consumer -> {
			if (!this.plugin.getStatsManager().contains(consumer.getName())) {
				this.plugin.getStatsManager().getFactionsData().add(new FactionData(consumer.getName()));
				System.out.println(ChatColor.GOLD + "[WorldOfNations] faction: " + consumer.getName() + " ajouté avec succès !");
			} else {
				System.out.println(ChatColor.GOLD + "[WorldOfNations] Faction: " + consumer.getName() + " OK");
			}
		});
	}
}
