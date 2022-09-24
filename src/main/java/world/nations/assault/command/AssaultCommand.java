package world.nations.assault.command;

import java.util.ArrayList;
import java.util.Arrays;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Factions;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.cmd.MassiveCommand;
import com.massivecraft.massivecore.cmd.VisibilityMode;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;

import world.nations.Core;
import world.nations.assault.data.AssaultData;
import world.nations.mod.PacketListener;
import world.nations.mod.SendDatas;
import world.nations.stats.data.FactionData;
import world.nations.utils.timings.CooldownTimer;
import world.nations.utils.timings.CountdownTimer;

public class AssaultCommand extends MassiveCommand  {

	private Core plugin;

	public AssaultCommand(Core plugin) {
		this.plugin = plugin;

		this.addAliases("assault");

		this.addRequiredArg("factionEnemy");

		this.addOptionalArg("stop", "");
		this.addOptionalArg("join", "");
		this.addOptionalArg("accept", "");

		this.addOptionalArg("list", "");

		this.addRequirements(ReqIsPlayer.get());

		this.setVisibilityMode(VisibilityMode.VISIBLE);
	}

	@Override
	public void perform() {
		Player player = (Player) sender;
		UPlayer fplayer = UPlayer.get(player);

		if(this.arg(0).equalsIgnoreCase("list")) {
			if(this.plugin.getAssaultManager().getAssaultList().isEmpty()) {
				player.sendMessage("§cAucun assault en cours");
				return;
			}

			player.sendMessage("§6Liste des assauts en cours :");
			player.sendMessage("");

			for(AssaultData consumer : this.plugin.getAssaultManager().getAssaultList()) {

				String time = DurationFormatUtils.formatDuration(CooldownTimer.getCooldownForPlayerLong("assault", consumer.getFaction()),
						"mm:ss 'minutes'", true);

				player.sendMessage("§6" + ChatColor.translateAlternateColorCodes('&', String.format("- %s (&c%d&6) &f/ &6(&c%d&6) %s  &c%s", consumer.getFactionName(),
						consumer.getPoints(), consumer.getPointsTarget(),
						consumer.getFactionTarget(), time)));
			}
			return;
		}

		if(!fplayer.hasFaction()) {
			player.sendMessage("§eVous devez être dans une faction pour effectuer cette commande");
			return;
		}

		Faction otherFaction = this.arg(0, ARFaction.get(sender));
		if (otherFaction == null) return;

		if(fplayer.getRole() != Rel.OFFICER && fplayer.getRole() != Rel.LEADER && !player.isOp()) {
			player.sendMessage("§cVous n'avez pas les permissions pour effectuer cette commande");
			return;
		}

		if (otherFaction == fplayer.getFaction() && this.arg(1) == null) {
			player.sendMessage("§cVous ne pouvez pas vous attaquer !");
			return;
		}

		if (this.arg(1) == null && otherFaction.getRelationTo(fplayer.getFaction()) == Rel.ALLY) {
			player.sendMessage("§cVous ne pouvez pas attaquer vos alliés !");
			return;
		}

		if (this.arg(1) != null && this.arg(1).equalsIgnoreCase("join")) {
			if (fplayer.getRelationTo(otherFaction) == Rel.ALLY) {
				/*AssaultData data = this.plugin.getAssaultManager().getFaction(otherFaction);

				if (data == null) return;

				data.setFactionAllied(new ArrayList<String>(Arrays.asList(fplayer.getFactionName())));
				Bukkit.broadcastMessage("§eL'alliance §9" + fplayer.getFactionName() + " §evient de rejoindre la faction §9" + otherFaction.getName() + " §epour l'assaut.");*/
				AssaultData data = this.plugin.getAssaultManager().getFaction(otherFaction);

				if (data == null) {
					fplayer.sendMessage("§cVotre Pays ne peut pas rejoindre cet assaut !");
					return;
				}

				fplayer.sendMessage("§eUne demande a été envoyé au Pays pour rejoindre la bataille !");
				otherFaction.getOnlinePlayers().forEach(target -> target.sendMessage("§eLe Pays §9" + fplayer.getFactionName() + " §esouhaite apporter son §daide§e dans l'assaut !"));
				this.plugin.getAssaultManager().getAlliesMap().put(fplayer.getFactionName(), otherFaction.getName());
				return;
			} else {
				fplayer.sendMessage("§cVotre Pays ne peut pas rejoindre cet assaut !");
				return;
			}
		}

		if (this.arg(1) != null && this.arg(1).equalsIgnoreCase("accept")) {
			if (this.plugin.getAssaultManager().getAlliesMap().get(otherFaction.getName()) == fplayer.getFactionName() || this.plugin.getAssaultManager().getAlliesMap().get(fplayer.getFactionName()) == otherFaction.getName()) {
				AssaultData data = this.plugin.getAssaultManager().getFaction(fplayer.getFaction());
				if (data == null) return;

				//otherFaction = alliés

				data.setFactionAllied(new ArrayList<String>(Arrays.asList(otherFaction.getName())));

				/*otherFaction.setRelationWish(data.getFactionTarget(), Rel.ENEMY);*/

				this.plugin.getAssaultManager().getAlliesMap().remove(otherFaction.getName());
				this.plugin.getAssaultManager().getAlliesMap().remove(fplayer.getFactionName());
				

				SendDatas.sendAssaultDatas(fplayer.getFaction(), otherFaction, data.getPoints(), data.getPointsTarget(), data.getTimer().secondsLeft,data.getFactionAllied(), (Player) sender);

				Bukkit.broadcastMessage("§eLe pays §9" + otherFaction.getName() + " §evient de rejoindre la faction §9" + fplayer.getFactionName() + " §epour l'assaut.");
				
				return;
			} else {
				fplayer.sendMessage("§cVotre Pays n'a reçu aucune demande !");
				return;
			}
		}

		if (this.arg(0) != null && this.arg(1) != null && this.arg(1).equalsIgnoreCase("stop") && player.isOp()) {
			AssaultData data = this.plugin.getAssaultManager().getFaction(otherFaction);
			if (data == null) return;

			if (CooldownTimer.isOnCooldown("assault", data.getFaction()) && CooldownTimer.isOnCooldown("assault", data.getTarget())) {
				CooldownTimer.removeCooldown("assault", data.getFaction());
				CooldownTimer.removeCooldown("assault", data.getTarget());
				Bukkit.getScheduler().cancelTask(data.getTimer().getAssignedTaskId());

				SendDatas.sendAssaultDatas(fplayer.getFaction(), otherFaction, data.getPoints(), data.getPointsTarget(), 0,data.getFactionAllied(), (Player) sender);

				this.plugin.getAssaultManager().getAssaultList().remove(data);

				fplayer.sendMessage("§cVous venez d'arrêter l'assaut de la faction §4" + otherFaction.getName());
				return;
			} else {
				fplayer.sendMessage("§cCette faction n'est pas en assaut !");
				return;
			}
		}

		if(!player.isOp() && (fplayer.getFaction().getOnlinePlayers().size() < 2 || otherFaction.getOnlinePlayers().size() < 2)) {
			fplayer.sendMessage("§cVotre Pays ou celui de l'ennemi n'a pas assez de joueurs en ligne pour lancer un assaut !");
			return;
		}

		if (CooldownTimer.isOnCooldown("timeLeft", fplayer.getFaction())) {
			player.sendMessage("§cVous êtes encore en cooldown pendant " + DurationFormatUtils.formatDuration(CooldownTimer.getCooldownForPlayerLong("timeLeft", fplayer.getFaction()), "HH'heures' mm'mins' ss'sec'", false));
			return;
		}

		if (this.arg(1) == null && otherFaction.getRelationTo(fplayer.getFaction()) != Rel.ENEMY) {
			player.sendMessage("§cVous pouvez attaquer que vos enemies !");
			return;
		}//c bon

		if(CooldownTimer.isOnCooldown("assault", fplayer.getFaction())) {
			player.sendMessage("§cVous êtes déjà en assault !");
			return;
		}


		for(AssaultData consumer : this.plugin.getAssaultManager().getAssaultList()) {
			for(String allies : consumer.getFactionAllied()) {
				if(allies.equalsIgnoreCase(fplayer.getFactionName())) {
					player.sendMessage("§cVous êtes déjà en assaut avec la faction §9" + consumer.getFactionTarget() + "§c !");
					return;
				}
			}
			for(Faction factionAllies : consumer.getFactionAllies()) {
				if(factionAllies.getName().equalsIgnoreCase(fplayer.getFactionName())) {
					player.sendMessage("§cVous êtes déjà en assaut avec la faction §9" + consumer.getFactionTarget() + "§c !");
					return;
				}
			}
			for (Faction targetsAllies : consumer.getTargetAllies()) {
				if(targetsAllies.getName().equalsIgnoreCase(fplayer.getFactionName())) {
					player.sendMessage("§cVous êtes déjà en assaut avec la faction §9" + consumer.getFaction() + "§c !");
					return;
				}
			}
		}


		if (!player.isOp()) CooldownTimer.addCooldown("timeLeft", fplayer.getFaction(), 10800);

		CooldownTimer.addCooldown("assault", fplayer.getFaction(), 1800);
		CooldownTimer.addCooldown("assault", otherFaction, 1800);

		CountdownTimer timer = new CountdownTimer(plugin, 1800, () -> {
			AssaultData data = this.plugin.getAssaultManager().getFaction(fplayer.getFaction());

			Bukkit.broadcastMessage("§aLa faction §c" + fplayer.getFactionName() + " §avient de lancer un assaut contre la faction §c" + otherFaction.getName());
			SendDatas.sendAssaultDatas(fplayer.getFaction(), otherFaction, data.getPoints(), data.getPointsTarget(), data.getTimer().getSecondsLeft(),data.getFactionAllied(), (Player) sender);

		},
		() -> {
			AssaultData data = this.plugin.getAssaultManager().getFaction(fplayer.getFaction());

			if (data.getPoints() == data.getPointsTarget()) {
				Bukkit.broadcastMessage("§aLe Pays §c" + data.getFactionName() + " (" + data.getPoints() + ") §aest en §9égalité §acontre le Pays §c" + data.getFactionTarget() + " (" + data.getPointsTarget() + ").");
			} else {
				Bukkit.broadcastMessage("§aLe Pays §c" + (data.getPoints() > data.getPointsTarget() ? data.getFactionName() + " (" + data.getPoints() : data.getFactionTarget() + " (" + data.getPointsTarget()) + ") §avient de gagner l'assaut contre le Pays §c" + (data.getPoints() > data.getPointsTarget() ? data.getFactionTarget() + " (" + data.getPointsTarget() : data.getFactionName() + " (" + data.getPoints()) + ")");
			}

			/*data.setPoints(0);
			data.setPointsTarget(0);*/

			FactionData faction = this.plugin.getStatsManager().getFaction(data.getFactionName());
			FactionData target = this.plugin.getStatsManager().getFaction(data.getFactionTarget());

			SendDatas.sendAssaultDatas(fplayer.getFaction(), otherFaction, data.getPoints(), data.getPointsTarget(), 0,data.getFactionAllied(), (Player) sender);
			

			if (data.getPoints() > data.getPointsTarget()) {
				faction.addWin();
				target.addLose();
			}
			if (data.getPoints() < data.getPointsTarget()) {
				target.addWin();
				faction.addLose();
			}

			data.setPoints(0);
			data.setPointsTarget(0);

			plugin.getStatsManager().saveFactionsData();

			CooldownTimer.removeCooldown("assault", data.getFaction());
			CooldownTimer.removeCooldown("assault", data.getTarget());

			this.plugin.getAssaultManager().getAssaultList().remove(data);

			/*for(String ally : data.getFactionAllied()) {
				Faction allyFaction = Faction.get(ally);
				otherFaction.setRelationWish(allyFaction, Rel.NEUTRAL);
			}*/

		}, (t) -> {

			AssaultData data = this.plugin.getAssaultManager().getFaction(fplayer.getFaction());
			SendDatas.sendAssaultDatas(fplayer.getFaction(), otherFaction, data.getPoints(), data.getPointsTarget(), data.getTimer().getSecondsLeft(),data.getFactionAllied(), (Player) sender);
			FactionColl coll = PacketListener.getFactionColl("factions_faction@default");
			
			for(String facName : data.getFactionAllied()) {
				for(UPlayer uPlayer : coll.getByName(facName).getUPlayers()) {
					SendDatas.sendAssaultDatas(fplayer.getFaction(), otherFaction, data.getPoints(), data.getPointsTarget(), data.getTimer().getSecondsLeft(),data.getFactionAllied(),uPlayer.getPlayer());
				}
			}

		});

		timer.scheduleTimer();

		this.plugin.getAssaultManager().addFaction(fplayer.getFaction(), otherFaction, timer);
	}

}
