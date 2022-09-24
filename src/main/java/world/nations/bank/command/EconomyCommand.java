package world.nations.bank.command;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import org.apache.commons.lang.math.NumberUtils;
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

    private final DecimalFormat formatter = new DecimalFormat("0.00");

    @Command(name = "bank.help", permission = "wofn.use")
    public void help(final CommandArgs args) {
        args.getSender().sendMessage(Utils.LINE);
        if (args.getSender().hasPermission("wofn.staff")) {
            args.getSender().sendMessage("§e/bank give <faction> <amount>");
        }
        args.getSender().sendMessage("§e/bank [faction] §7- afficher le solde du compte");
        args.getSender().sendMessage("§e/bank create §7- créer une banque");
        args.getSender().sendMessage("§e/bank delete §7- supprimer votre banque");
        args.getSender().sendMessage("§e/bank deposit [faction] <amount> §7- deposer de l'argent");
        args.getSender().sendMessage("§e/bank withdraw <amount> §7- retirer de l'argent");
        args.getSender().sendMessage("§e/bank addowner <player> §7- ajouter un propriétaire");
        args.getSender().sendMessage("§e/bank removeowner <player> §7- retirer un propriétaire");
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

        String number = args.getArgs(1);

        if (!NumberUtils.isNumber(number)) {
            player.sendMessage("§cVeuillez saisir un chiffre ou un nombre");
            return;
        }

        int amount = NumberUtils.toInt(number);
        double newBalance = plugin.getEconomyManager().addBalance(faction, amount);

        FactionColl coll = PacketListener.getFactionColl("factions_faction@default");
        if (coll != null && coll.getByName(faction) != null) {
            addExchange(coll.getByName(faction).getId(), new FactionExchange(amount, player.getName(), LocalDateTime.now()));

            player.sendMessage(new String[]{
                    Utils.color("&8§ &eVous avez ajouté " + formatter.format(amount).replace(',', '.') + "$ a &6" + faction + "&e."),
                    Utils.color("&eLe solde de &6" + faction + " &eest maintenant de &6" + formatter.format(newBalance).replace(',', '.') + "$&e.")});
        }
    }

    @Command(name = "bank", permission = "wofn.use")
    public void bank(final CommandArgs args) {
        Player player = args.getPlayer();
        UPlayer fplayer = UPlayer.get(player);

        EconomyManager manager = this.plugin.getEconomyManager();

        if(args.length() == 1) {
            String faction = args.getArgs(0);
            if(!manager.getFactionsNames().contains(faction)) {
                player.sendMessage("§cCette faction n'a pas de banque ou n'existe pas !");
                return;
            }

            args.getSender().sendMessage("§cLa faction "+ faction +" a " + formatter.format(manager.getBalance(faction)) + "$ sur sa banque");
            return;
        } else if (args.length() > 1) {
            args.getSender().sendMessage("§cUsage: /bank [faction]");
            return;
        }

        if (!fplayer.hasFaction()) {
            player.sendMessage("§cVous devez avoir une faction !");
            return;
        }


        if (!manager.getFactionsNames().contains(fplayer.getFactionName())) {
            player.sendMessage("§cVotre faction n'a pas de compte en banque !");
            return;
        }

        player.sendMessage("§cVotre faction a " + formatter.format(manager.getBalance(fplayer.getFactionName())) + "$ sur sa banque");
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
        } else {
            player.sendMessage("§cVotre faction à déjà un compte banque !");
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
        } else {
            player.sendMessage("§cVous n'êtes pas le leader de la faction !");
        }
    }

    @Command(name = "bank.deposit", permission = "wofn.use")
    public void deposit(final CommandArgs args) {
        Player player = args.getPlayer();
        UPlayer fplayer = UPlayer.get(player);

        Pattern int_Pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        if (args.length() > 2 || args.length() < 1) {
            args.getSender().sendMessage("§cUsage: /bank deposit [faction] <amount>");
            return;
        }


        if(args.length() == 1 && !int_Pattern.matcher(args.getArgs(0)).matches()){
            args.getSender().sendMessage("§cUsage: /bank deposit [faction] <amount>");
            return;
        } else if(args.length() == 1){

            if (!fplayer.hasFaction()) {
                player.sendMessage("§cVous devez avoir une faction pour deposer de l'argent dans votre banque !");
                return;
            }

            if (!this.plugin.getEconomyManager().getFactionsNames().contains(fplayer.getFactionName())) {
                player.sendMessage("§cVous n'avez pas de compte en banque !");
                return;
            }

            double amount = Double.parseDouble(args.getArgs(0));
            if (amount <= 0) {
                player.sendMessage(Utils.color("&cVous devez deposer de l'argent en quantités positives."));
                return;
            }

            if (this.plugin.getEconomy().getBalance(player) >= amount) {
                this.plugin.getEconomy().withdrawPlayer(player, amount);
                this.plugin.getEconomyManager().addBalance(fplayer.getFactionName(), amount);

                addExchange(fplayer.getFactionId(), new FactionExchange((int) amount, player.getName(), LocalDateTime.now()));
                player.sendMessage("§eVous venez d'envoyer §6" + amount + "$ §eà votre banque !");
            } else {
                player.sendMessage("§cVous n'avez pas assez d'argent !");
            }
        }


        if(args.length() == 2 && !int_Pattern.matcher(args.getArgs(1)).matches()){
            args.getSender().sendMessage("§cUsage: /bank deposit [faction] <amount>");
        } else if(args.length() == 2){

            double amount = Double.parseDouble(args.getArgs(1));
            if (amount <= 0) {
                player.sendMessage(Utils.color("&cVous devez deposer de l'argent en quantités positives."));
                return;
            }

            FactionColl coll = PacketListener.getFactionColl("factions_faction@default");

            for(Faction factions : coll.getAll()) {
                if(factions.getName().equalsIgnoreCase(args.getArgs(0))) {

                    if (!this.plugin.getEconomyManager().getFactionsNames().contains(factions.getName())) {
                        player.sendMessage("§cCette faction n'a pas de compte en banque !");
                        return;
                    }

                    if (this.plugin.getEconomy().getBalance(player) >= amount) {
                        this.plugin.getEconomy().withdrawPlayer(player, amount);
                        this.plugin.getEconomyManager().addBalance(factions.getName(), amount);

                        addExchange(factions.getId(), new FactionExchange((int) amount, player.getName(), LocalDateTime.now()));
                        player.sendMessage("§eVous venez d'envoyer §6" + amount + "$ §eà la banque de la faction "+ factions.getName() + " !");
                    } else {
                        player.sendMessage("§cVous n'avez pas assez d'argent !");
                    }
                    return;
                }
            }

            player.sendMessage("§cCette faction n'existe pas !");

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

        for (BankData bank : this.plugin.getEconomyManager().getFactionsMap()) {
            if (bank.getFactionName().equalsIgnoreCase(fplayer.getFactionName())) {
                if (bank.getOwners().contains(player.getUniqueId()) || fplayer.getRole() == Rel.LEADER) {
                    this.plugin.getEconomyManager().substractBalance(fplayer.getFactionName(), amount);
                    this.plugin.getEconomy().depositPlayer(player, amount);

                    addExchange(fplayer.getFactionId(), new FactionExchange(amount.intValue() * -1, player.getName(), LocalDateTime.now()));
                    player.sendMessage(
                            "§6" + formatter.format(amount) + "$ §eviennent d'être §cretirés §ede votre banque !");
                } else {
                    player.sendMessage("§cVous devez être propriétaire pour retirer de l'argent !");
                }
                break;
            }
        }
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
            player.sendMessage("§cVous ne pouvez pas vous ajouter aux propriétaires");
            return;
        }

        if (fplayer.getRole() == Rel.LEADER) {
            this.plugin.getEconomyManager().addOwner(fplayer.getFactionName(), target.getUniqueId());
            player.sendMessage("§eVous venez d'ajouter §6" + target.getName() + " §eaux propriétaires !");
        } else {
            player.sendMessage("§cVous n'êtes pas le leader de la faction !");
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
            player.sendMessage("§cVous ne pouvez pas vous retirer des propriétaires");
            return;
        }

        if (fplayer.getRole() == Rel.LEADER) {
            this.plugin.getEconomyManager().removeOwner(fplayer.getFactionName(), target.getUniqueId());
            player.sendMessage("§eVous venez de retirer §6" + target.getName() + " §edes propriétaires !");
        } else {
            player.sendMessage("§cVous n'êtes pas le leader de la faction !");
        }

    }

    private void addExchange(String facId, FactionExchange exchange) {
        if (Core.factionExchanges.get(facId).size() == 0) {
            ArrayList<FactionExchange> exchanges = new ArrayList<FactionExchange>();
            exchanges.add(exchange);
            Core.factionExchanges.put(facId, exchanges);
        } else {
            ArrayList<FactionExchange> exchanges = Core.factionExchanges.get(facId);
            exchanges.add(exchange);
            Core.factionExchanges.replace(facId, exchanges);
        }
    }
}
