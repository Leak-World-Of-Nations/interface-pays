package world.nations.stats.command;

import org.bukkit.entity.Player;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.cmd.MassiveCommand;
import com.massivecraft.massivecore.cmd.VisibilityMode;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;

import world.nations.Core;
import world.nations.stats.data.FactionData;
import world.nations.utils.Utils;

import java.text.DecimalFormat;

public class StatsCommand extends MassiveCommand {

    private final Core plugin;

    public StatsCommand(Core plugin) {
        this.plugin = plugin;

        this.addAliases("stats", "s");

        this.addRequirements(ReqIsPlayer.get());
        this.addRequiredArg("Pays");

        this.setVisibilityMode(VisibilityMode.VISIBLE);
    }

    @Override
    public void perform() {
        Player player = (Player) sender;

        Faction faction = this.arg(0, ARFaction.get(sender));
        if (faction == null) return;

        FactionData data = this.plugin.getStatsManager().getFaction(faction.getName());

        if (data == null) {
            player.sendMessage("§cPays introuvable");
            return;
        }

        player.sendMessage(Utils.color("&7======= &6Stats " + data.getFactionName() + " &7======="));
        player.sendMessage(Utils.color("&eNombre de kills&7: &c" + data.getKills()));
        player.sendMessage(Utils.color("&eNombre de morts&7: &c" + data.getDeaths()));
        player.sendMessage(Utils.color("&eRatio&7: &c" + data.getKDR()));

        if (this.plugin.getEconomyManager().getFactionsNames().contains(faction.getName()))
            player.sendMessage(Utils.color("&eBanque&7: &6" + this.plugin.getEconomyManager().getBalance(faction.getName())));
        else
            player.sendMessage(Utils.color("&eBanque&7: &cN/A"));

        player.sendMessage(Utils.color("&eAssauts gagnés&7: &c" + data.getWins()));
        player.sendMessage(Utils.color("&eAssauts perdus&7: &c" + data.getLoses()));
        player.sendMessage(Utils.color("&eAssauts ratio&7: &c" + data.getRatio()));
        player.sendMessage(Utils.color("&eScore Zone&7: &c" + new DecimalFormat("###.##").format(data.getScorezone())));
    }
}
