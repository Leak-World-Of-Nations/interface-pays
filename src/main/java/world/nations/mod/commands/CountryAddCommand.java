package world.nations.mod.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.FactionColl;

import world.nations.Core;
import world.nations.mod.PacketListener;

public class CountryAddCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(sender instanceof Player) {
			
			Player p = (Player)sender;
			FactionColl coll = PacketListener.getFactionColl("factions_faction@default");
			
			if(p.isOp()) {
				if(args.length == 2 && args[0].equals("add")) {
					String availableCountryName = args[1];
					
					if(!Core.availableCountries.containsKey(availableCountryName) || coll.getByName(availableCountryName) == null) {
						Core.availableCountries.put(availableCountryName, p.getLocation());
						p.sendMessage("§aVous venez de créer le pays §e§l" + availableCountryName + " §r§a aux coordonnées §e§l null");
						return true;
					}
					
					else
						p.sendMessage("§cIl existe déjà un pays avec ce nom");
						
					
				}
				else if(args.length == 2 && args[0].equals("pos")) {
					String availableCountryName = args[1];
					
					if(Core.availableCountries.containsKey(availableCountryName)) {
						Core.availableCountries.replace(availableCountryName, p.getLocation());
						p.sendMessage("§aVous venez de changer les coordonnées du pays §e§l" + availableCountryName);
						p.sendMessage("§aLes nouvelles coordonnées sont §e§lx: §b" + (int)p.getLocation().getX() + " §l§ey: §b" + (int)p.getLocation().getY() + " §l§ez: §b" + (int)p.getLocation().getZ() + "§r§a dans le monde §e" + p.getLocation().getWorld().getName());
						return true;
					}
					else
						p.sendMessage("§cIl y a aucun pays avec ce nom là");
				}
				else 
					return false;
			}
			else
				p.sendMessage("§cVous n'êtes pas op");
			
		}
		return false;
	}

}
