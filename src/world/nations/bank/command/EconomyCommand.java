package world.nations.bank.command;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.UPlayer;

import world.nations.Core;
import world.nations.bank.EconomyManager;
import world.nations.bank.data.BankData;
import world.nations.mod.FactionExchange;
import world.nations.mod.PacketListener;
import world.nations.utils.BukkitUtils;
import world.nations.utils.Utils;
import world.nations.utils.command.Command;
import world.nations.utils.command.CommandArgs;

public class EconomyCommand {

	private final Core plugin;

	public EconomyCommand(Core plugin) {
		this.plugin = plugin;
	}

	private DecimalFormat formatter = new DecimalFormat("0.00");

	@Command(name = "bank.help", permission = "wofn.use")
	public void help(final CommandArgs args) {
		args.getSender().sendMessage(Utils.LINE);
		if (args.getSender().hasPermission("wofn.staff")) {
			args.getSender().sendMessage("§e/bank give <faction> <amount>");
		}
		args.getSender().sendMessage("§e/bank §7- afficher le solde du compte");
		args.getSender().sendMessage("§e/bank create §7- créer une banque");
		args.getSender().sendMessage("§e/bank delete §7- supprimer votre banque");
		args.getSender().sendMessage("§e/bank deposit <amount> §7- deposer de l'argent");
		args.getSender().sendMessage("§e/bank withdraw <amount> §7- retirer de l'argent");
		args.getSender().sendMessage("§e/bank addowner <player> §7- ajouter un owner");
		args.getSender().sendMessage("§e/bank removeowner <player> §7- retirer un owner");
		args.getSender().sendMessage(Utils.LINE);
	}

	@Command(name = "bank.give", permission = "wofn.staff")
	public void banks(final CommandArgs args) {
		Player player = args.getPlayer();

		if (args.length() < 1) {
			args.getSender().sendMessage("§e/bank give <faction> <amount>");
			return;
		}
		String faction = args.getArgs(0);

		if (!this.plugin.getEconomyManager().getFactionsNames().contains(faction)) {
			player.sendMessage("§cCette faction n'existe pas !");
			return;
		}

		int amount = Utils.tryParseInt(args.getArgs(1));

		double newBalance = plugin.getEconomyManager().addBalance(faction, amount);

		FactionColl coll = PacketListener.getFactionColl("factions_faction@default");
		addExchange(coll.getByName(faction).getId(), new FactionExchange(amount,player.getName(),LocalDateTime.now()));

		player.sendMessage(new String[] { Utils.color("&8§ &eVous avez ajouté " + formatter.format(amount).replace(',', '.') + "$ a &6" + faction + "&e."),
				Utils.color("&eLe solde de &6" + faction + " &eest maintenant de &6" + formatter.format(newBalance).replace(',', '.') + "$&e.")});
		return;
	}

	@Command(name = "bank", permission = "wofn.use")
	public void bank(final CommandArgs args) {
		Player player = args.getPlayer();
		UPlayer fplayer = UPlayer.get(player);

		if (!fplayer.hasFaction()) {
			player.sendMessage("§cVous devez avoir une faction !");
			return;
		}

		EconomyManager manager = this.plugin.getEconomyManager();

		if (!manager.getFactionsNames().contains(fplayer.getFactionName())) {
			player.sendMessage("§cVous n'avez pas de compte bancaire !");
			return;
		}

		player.sendMessage("§cVotre faction a " + formatter.format(manager.getBalance(fplayer.getFactionName())) + "$");
		return;
	}

	@Command(name = "bank.create", permission = "wofn.use")
	public void create(final CommandArgs args) {
		Player player = args.getPlayer();

		UPlayer fplayer = UPlayer.get(player);

		if (!fplayer.hasFaction()) {
			player.sendMessage("§cVous devez avoir une faction !");
			return;
		}

		if (!this.plugin.getEconomyManager().getFactionsNames().contains(fplayer.getFactionName())) {
			EconomyManager eco = this.plugin.getEconomyManager();
			BankData data = new BankData(fplayer.getFactionName());
			data.getOwners().add(player.getUniqueId());
			eco.getFactionsMap().add(data);
			eco.getFactionsNames().add(fplayer.getFactionName());
			player.sendMessage("§eCompte crée avec succès !");
			return;
		} else {
			player.sendMessage("§cVotre faction à déjà un compte banque !");
			return;
		}
	}

	@Command(name = "bank.delete", permission = "wofn.use")
	public void remove(final CommandArgs args) {
		Player player = args.getPlayer();

		UPlayer fplayer = UPlayer.get(player);

		if (!fplayer.hasFaction()) {
			player.sendMessage("§cVous devez avoir une faction !");
			return;
		}

		if (!this.plugin.getEconomyManager().getFactionsNames().contains(fplayer.getFactionName())) {
			player.sendMessage("§cVous n'avez pas de compte banque !");
			return;
		}

		if (fplayer.getRole() == Rel.LEADER) {
			this.plugin.getEconomyManager().getFactionsMap().removeIf(obj -> (obj.getFactionName().equalsIgnoreCase(fplayer.getFactionName())));
			this.plugin.getEconomyManager().getFactionsNames().remove(fplayer.getFactionName());
			player.sendMessage("§aCompte supprimé avec succès !");
			return;
		} else {
			player.sendMessage("§cVous n'êtes pas le leader de la faction !");
			return;
		}
	}

	@Command(name = "bank.deposit", permission = "wofn.use")
	public void deposit(final CommandArgs args) {
		Player player = args.getPlayer();

		UPlayer fplayer = UPlayer.get(player);

		if (!fplayer.hasFaction()) {
			player.sendMessage("§cVous devez avoir une faction !");
			return;
		}

		if (!this.plugin.getEconomyManager().getFactionsNames().contains(fplayer.getFactionName())) {
			player.sendMessage("§cVous n'avez pas de compte banque !");
			return;
		}

		if (args.length() != 1) {
			args.getSender().sendMessage("§cUsage: /bank deposit <amount>");
			return;
		}

		Double amount = Double.parseDouble(args.getArgs(0));

		if (amount <= 0) {
			player.sendMessage(Utils.color("&cVous devez deposer de l'argent en quantités positives."));
			return;
		}

		if (this.plugin.getEconomy().getBalance(player) >= amount) {
			this.plugin.getEconomy().withdrawPlayer(player, amount);
			this.plugin.getEconomyManager().addBalance(fplayer.getFactionName(), amount);

			addExchange(fplayer.getFactionId(), new FactionExchange(amount.intValue(),player.getName(),LocalDateTime.now()));
			player.sendMessage("§eVous venez d'envoyer §6" + amount + "$ §eau compte faction !");
			return;
		} else {
			player.sendMessage("§cVous n'avez pas assez d'argent !");
			return;
		}

	}

	@Command(name = "bank.withdraw", permission = "wofn.use")
	public void withdraw(final CommandArgs args) {
		Player player = args.getPlayer();

		UPlayer fplayer = UPlayer.get(player);

		if (!fplayer.hasFaction()) {
			player.sendMessage("§cVous devez avoir une faction !");
			return;
		}

		if (!this.plugin.getEconomyManager().getFactionsNames().contains(fplayer.getFactionName())) {
			player.sendMessage("§cVous n'avez pas de compte banque !");
			return;
		}

		if (args.length() != 1) {
			args.getSender().sendMessage("§cUsage: /bank withdraw <amount>");
			return;
		}

		Double amount = Double.parseDouble(args.getArgs(0));

		if (amount <= 0) {
			player.sendMessage(Utils.color("&cVous devez retirer de l'argent en quantités positives."));
			return;
		} else if (amount > this.plugin.getEconomyManager().getBalance(fplayer.getFactionName())) {
			player.sendMessage("§cOpération impossible, le montant est supérieur à votre solde !");
			return;
		}

		this.plugin.getEconomyManager().getFactionsMap().forEach(consumer -> {
			if (consumer.getFactionName().equalsIgnoreCase(fplayer.getFactionName())) {
				if (consumer.getOwners().contains(player.getUniqueId()) || fplayer.getRole() == Rel.LEADER) {
					this.plugin.getEconomyManager().substractBalance(fplayer.getFactionName(), amount);
					this.plugin.getEconomy().depositPlayer(player, amount);

					addExchange(fplayer.getFactionId(), new FactionExchange(amount.intValue() * -1,player.getName(),LocalDateTime.now()));
					player.sendMessage(
							"§6" + formatter.format(amount) + "$ §eviennent d'être §cretirés §ede votre banque !");
					return;
				} else {
					player.sendMessage("§cVous devez être owner pour retirer de l'argent !");
					return;
				}
			}
		});

		return;
	}

	@Command(name = "bank.addowner", permission = "wofn.use")
	public void addOwner(final CommandArgs args) {
		Player player = args.getPlayer();

		UPlayer fplayer = UPlayer.get(player);

		if (!fplayer.hasFaction()) {
			player.sendMessage("§cVous devez avoir une faction !");
			return;
		}

		if (!this.plugin.getEconomyManager().getFactionsNames().contains(fplayer.getFactionName())) {
			player.sendMessage("§cVous n'avez pas de compte banque !");
			return;
		}

		if (args.length() != 1) {
			args.getSender().sendMessage("§cUsage: /bank addowner <player>");
			return;
		}

		Player target = Bukkit.getPlayer(args.getArgs(0));

		if (target == null || !target.isOnline()) {
			player.sendMessage("§cJoueur introuveable");
			return;
		}

		if (target.equals(player)) {
			player.sendMessage("§cVous ne pouvez pas vous ajouter aux owners");
			return;
		}

		if (fplayer.getRole() == Rel.LEADER) {
			this.plugin.getEconomyManager().addOwner(fplayer.getFactionName(), target.getUniqueId());
			player.sendMessage("§eVous venez d'ajouter §6" + target.getName() + " §eaux owners !");
			return;
		} else {
			player.sendMessage("§cVous n'êtes pas le leader de la faction !");
			return;
		}
	}

	@Command(name = "bank.removeowner", permission = "wofn.use")
	public void removeOwner(final CommandArgs args) {
		Player player = args.getPlayer();

		UPlayer fplayer = UPlayer.get(player);

		if (!fplayer.hasFaction()) {
			player.sendMessage("§cVous devez avoir une faction !");
			return;
		}

		if (!this.plugin.getEconomyManager().getFactionsNames().contains(fplayer.getFactionName())) {
			player.sendMessage("§cVous n'avez pas de compte banque !");
			return;
		}

		if (args.length() != 1) {
			args.getSender().sendMessage("§cUsage: /bank addowner <player>");
			return;
		}

		OfflinePlayer target = BukkitUtils.offlinePlayerWithNameOrUUID(args.getArgs(0));

		if (target == null) {
			player.sendMessage("§cJoueur introuveable");
			return;
		}

		if (target.equals(player)) {
			player.sendMessage("§cVous ne pouvez pas vous retirer des owners");
			return;
		}

		if (fplayer.getRole() == Rel.LEADER) {
			this.plugin.getEconomyManager().removeOwner(fplayer.getFactionName(), target.getUniqueId());
			player.sendMessage("§eVous venez de retirer §6" + target.getName() + " §edes owners !");
			return;
		} else {
			player.sendMessage("§cVous n'êtes pas le leader de la faction !");
			return;
		}

	}

	private void addExchange(String facId,FactionExchange exchange) {
		if(Core.plugin.factionExchanges.get(facId).size() == 0) {
			ArrayList<FactionExchange> exchanges = new ArrayList<FactionExchange>();
			exchanges.add(exchange);
			Core.plugin.factionExchanges.put(facId, exchanges);
		}
		else {
			ArrayList<FactionExchange> exchanges = Core.plugin.factionExchanges.get(facId);
			exchanges.add(exchange);
			Core.plugin.factionExchanges.replace(facId, exchanges);
		}
	}
}
