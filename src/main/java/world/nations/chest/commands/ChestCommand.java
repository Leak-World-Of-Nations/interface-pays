package world.nations.chest.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.UPlayer;

import world.nations.Core;
import world.nations.chest.VirtualChestManager;
import world.nations.utils.command.Command;
import world.nations.utils.command.CommandArgs;

public class ChestCommand {

	private final VirtualChestManager chestManager;

	public ChestCommand(VirtualChestManager chestManager) {
		this.chestManager = chestManager;
	}

	@Command(name = "chest", permission = "chest.use", inGameOnly = true)
	public void chestCommand(CommandArgs args) {
		Player player = args.getPlayer();

		if (player.getGameMode().equals(GameMode.CREATIVE) && !player.hasPermission("chest.staff")) {
			player.sendMessage("§cVous devez être en gamemode 0");
			return;
		}

		switch (args.getArgs().length) {
		case 0:
			// Open the player's own chest
			UPlayer fplayer = UPlayer.get(player);
			if (!fplayer.hasFaction()) {
				player.sendMessage("§cVous n'avez pas de faction !");
			}

			if (fplayer.getRole() == Rel.LEADER || (Core.getPlugin().getStatsManager().getFaction(fplayer.getFactionName()).getBankAcces().contains(player.getName()))) {
				Inventory chest = chestManager.getChest(fplayer.getFactionName());
				player.openInventory(chest);
			} else {
				player.sendMessage("§cVous devez être owner de la faction !");
			}

			return;
		case 1:
			// Open someone else's chest
			if (player.hasPermission("chest.staff")) {
				//Cannot cast Faction heres
				Faction faction = ARFaction.get(args.getArgs(0)).read(args.getArgs(0)).getResult();

				if (faction == null) {
					player.sendMessage(String.format("Le coffre de %s n'a pas été trouvé", args.getArgs(0)));
					return;
				}

				Inventory chest = chestManager.getChest(faction.getName());
				player.openInventory(chest);
			} else {
				player.sendMessage("§cVous n'avez pas la permission !");
			}
		}
	}

	@Command(name = "chest.addowner", permission = "chest.use", inGameOnly = true)
	public void chestAddowner(CommandArgs args) {
		Player player = args.getPlayer();

		if (UPlayer.get(player).getRole() != Rel.LEADER) {
			player.sendMessage("§cVous devez être le leader du pays !");
			return;
		}

		if (args.length() < 1) {
			player.sendMessage("§e/chest addowner <player>");
			return;
		}

		Player target = Bukkit.getPlayer(args.getArgs(0));
		if (target == null) {
			player.sendMessage("§cCe joueur n'existe pas !");
			return;
		}

		if (target.getName().equalsIgnoreCase(player.getName())) {
			player.sendMessage("Vous ne pouvez pas vous choisir !");
			return;
		}

		if (!UPlayer.get(target).getFactionName().equalsIgnoreCase(UPlayer.get(player).getFactionName())) {
			player.sendMessage("§cSeuls les membres de votre faction peuvent être ajoutés !");
			return;
		}

		if (Core.getPlugin().getStatsManager().getFaction(UPlayer.get(player).getFactionName()).getBankAcces().contains(target.getName())) {
			player.sendMessage("Ce joueur est déjà dans vos owners !");
			return;
		}

		Core.getPlugin().getStatsManager().getFaction(UPlayer.get(player).getFactionName()).getBankAcces().add(target.getName());
		player.sendMessage("§eVous venez d'ajouter §6" + target.getName() + "§e aux owners !");
		return;
	}

	@Command(name = "chest.removeowner", permission = "chest.use", inGameOnly = true)
	public void chestremoveowner(CommandArgs args) {
		Player player = args.getPlayer();

		Player target = Bukkit.getPlayer(args.getArgs(0));

		if (UPlayer.get(player).getRole() != Rel.LEADER) {
			player.sendMessage("§cVous devez être le leader du pays !");
			return;
		}

		if (args.length() < 1) {
			player.sendMessage("§e/chest removeowner <player>");
			return;
		}

		if (target == null) {
			player.sendMessage("§cCe joueur n'existe pas !");
			return;
		}

		if (target.getName().equalsIgnoreCase(player.getName())) {
			player.sendMessage("Vous ne pouvez pas vous choisir !");
			return;
		}

		if (!Core.getPlugin().getStatsManager().getFaction(UPlayer.get(player).getFactionName()).getBankAcces().contains(target.getName())) {
			player.sendMessage("§cCe joueur n'est pas dans vos owners !");
			return;
		}

		Core.getPlugin().getStatsManager().getFaction(UPlayer.get(player).getFactionName()).getBankAcces().remove(target.getName());
		player.sendMessage("§eVous venez de supprimer §6" + target.getName() + "§e des owners !");
		return;
	}
}
