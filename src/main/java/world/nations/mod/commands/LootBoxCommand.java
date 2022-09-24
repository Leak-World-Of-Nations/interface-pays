package world.nations.mod.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.io.ByteStreams;

import world.nations.Core;
import world.nations.mod.PacketFunctions;

public class LootBoxCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		
		if(sender instanceof Player) {
			
			Player p = (Player)sender;
			
			if(args.length == 2) {
				
				if(p.isOp()) {
					int lootboxID = -1;
					int percentage = -1;
					
					try {
						lootboxID = Integer.valueOf(args[0]);
						percentage = Integer.valueOf(args[1]);
					}
					catch(Exception e) { e.printStackTrace(); }
					
					if(lootboxID >= 0 && percentage > 0) {
						if(p.getItemInHand() != null) {
							LootboxItem itemToAdd = new LootboxItem(lootboxID,percentage,p.getItemInHand());
							
							//Core.plugin.lootboxItems.add(itemToAdd);
							
							p.sendMessage("§aL'item " + itemToAdd.getItemToDrop().getType() + " vient d'être ajouter à la lootbox " + itemToAdd.getLootboxID() + " avec un pourcentage de " + itemToAdd.getPercentage());
							
					
						}
						else 
							p.sendMessage("§4Merci de bien vouloir avoir un item en main");
					}
					else
						p.sendMessage("§4Merci de bien vouloir entrer des valeurs correctes");
				}
				else
					p.sendMessage("§4Vous devez être op pour faire cette commande");
			}
			else {
				if(args.length == 3) {
					if(args[2].equals("remove")) {
						if(p.getItemInHand() != null) {
							ArrayList<LootboxItem> erase = new ArrayList<LootboxItem>();
							/*for(LootboxItem item : Core.plugin.lootboxItems) {
								if(item.getItemToDrop().getType() == p.getItemInHand().getType()) {
									erase.add(item);
								}
							}*/
							
							for(LootboxItem item : erase) {
								//Core.plugin.lootboxItems.remove(item);
								p.sendMessage("§aL'item " + item.getItemToDrop().getItemMeta().getDisplayName() + " vient d'être supprimé de la lootbox " + item.getLootboxID());
							}
						}
						else 
							p.sendMessage("§4Merci de bien vouloir avoir un item en main");
					}
				}
				else
					p.sendMessage("§4Merci de bien vouloir entrer une commande valide");
			}
			return true;
		}
		return false;
	}

}
