package world.nations.mod.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.ps.PS;

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
						p.sendMessage("ßaVous venez de cr√©er le pays ßeßl" + availableCountryName + " ßrßa aux coordonn√©es ßeßl null");
						return true;
					}
					
					else
						p.sendMessage("ßcIl existe d√©j√† un pays avec ce nom");
						
					
				}
				else if(args.length == 2 && args[0].equals("pos")) {
					String availableCountryName = args[1];
					
					if(Core.availableCountries.containsKey(availableCountryName)) {
						Core.availableCountries.replace(availableCountryName, p.getLocation());
						p.sendMessage("ßaVous venez de changer les coordonn√©es du pays ßeßl" + availableCountryName);
						p.sendMessage("ßaLes nouvelles coordonnÈees sont ßeßlx: ßb" + (int)p.getLocation().getX() + " ßlßey: ßb" + (int)p.getLocation().getY() + " ßlßez: ßb" + (int)p.getLocation().getZ() + "ßrßa dans le monde ße" + p.getLocation().getWorld().getName());
						return true;
					}
					else
						p.sendMessage("ßcIl y a aucun pays avec ce nom l‡†");
				}
			}
			
			
		}
		
		return false;
	}

}
