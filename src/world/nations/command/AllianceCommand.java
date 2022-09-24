package world.nations.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FactionListComparator;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.UPlayer;

import world.nations.Core;
import world.nations.stats.data.FactionData;
import world.nations.utils.Utils;
import world.nations.utils.command.Command;
import world.nations.utils.command.CommandArgs;

public class AllianceCommand {
	
	private List<String> invitedURSS = new ArrayList<String>();
	private List<String> invitedAllied = new ArrayList<String>();
	private List<String> invitedAxe = new ArrayList<String>();

	private Core plugin;
	
	public AllianceCommand(Core plugin) {
		this.plugin = plugin;
	}
	
	@Command(name = "alliance", permission = "alliance.use", inGameOnly = false)
	public void allianceCommand(CommandArgs args) {
		args.getSender().sendMessage(Utils.LINE);
		if (args.getSender().hasPermission("alliance.staff")) {
			args.getSender().sendMessage("§e/" + args.getLabel() + " setleader <joueur> <urss/allies/axe>");
			args.getSender().sendMessage("§e/" + args.getLabel() + " removeleader <joueur> <urss/allies/axe>");
		}
		args.getSender().sendMessage("§e/" + args.getLabel() + " kick <pays>");
		args.getSender().sendMessage("§e/" + args.getLabel() + " invite <pays>");
		args.getSender().sendMessage("§e/" + args.getLabel() + " join <urss/allies/axe>");
		args.getSender().sendMessage("§e/" + args.getLabel() + " leave <urss/allies/axe>");
		args.getSender().sendMessage(Utils.LINE);
	}
	
	@Command(name = "alliance.infos", permission = "alliance.use", inGameOnly = false)
	public void infosCommand(CommandArgs args) {
		args.getSender().sendMessage("§e======= §6Alliance §9URSS §e=======");
		args.getSender().sendMessage("§eLeader§7: §6" + this.plugin.getConfig().getString("settings.urss"));
		args.getSender().sendMessage("§ePays dans l'alliance§7: §6" + (this.plugin.getConfig().getString("settings.urss").equalsIgnoreCase("personne") ? "0" : this.plugin.getConfig().getInt("settings.pays_urss")));
		args.getSender().sendMessage("");
		args.getSender().sendMessage("§e======= §6Alliance §9Alliés §e=======");
		args.getSender().sendMessage("§eLeader§7: §6" + this.plugin.getConfig().getString("settings.allied"));
		args.getSender().sendMessage("§ePays dans l'alliance§7: §6" + (this.plugin.getConfig().getString("settings.allied").equalsIgnoreCase("personne") ? "0" : this.plugin.getConfig().getInt("settings.pays_allied")));
		args.getSender().sendMessage("");
		args.getSender().sendMessage("§e======= §6Alliance §9Axe §e=======");
		args.getSender().sendMessage("§eLeader§7: §6" + this.plugin.getConfig().getString("settings.axe"));
		args.getSender().sendMessage("§ePays dans l'alliance§7: §6" + (this.plugin.getConfig().getString("settings.axe").equalsIgnoreCase("personne") ? "0" : this.plugin.getConfig().getInt("settings.pays_axe")));
		return;
	}
	
	@Command(name = "alliance.setleader", permission = "alliance.staff", inGameOnly = false)
	public void setLeaderCommand(CommandArgs args) {
		if (args.length() < 1) {
			args.getSender().sendMessage("§e/alliance setleader <joueur> <urss/allies/axe>");
			return;
		}
		
		Player target = Bukkit.getPlayer(args.getArgs(0));
		if (target == null) return;
		
		UPlayer fplayer = UPlayer.get(target);
		if (!fplayer.hasFaction()) {
			args.getPlayer().sendMessage("§cCe joueur n'a pas de faction !");
			return;
		}
		
		if (args.getArgs(1).equalsIgnoreCase("urss")) {
			this.plugin.getConfig().set("settings.urss", target.getName());
			Bukkit.broadcastMessage("§eLe nouveau §dleader §ede l'alliance §9URSS §eest §6" + target.getName() + " §e!");
		} else if (args.getArgs(1).equalsIgnoreCase("axe")) {
			this.plugin.getConfig().set("settings.axe", target.getName());
			Bukkit.broadcastMessage("§eLe nouveau §dleader §ede l'alliance §9Axe §eest §6" + target.getName() + " §e!");
		} else if (args.getArgs(1).equalsIgnoreCase("allies")) {
			this.plugin.getConfig().set("settings.allied", target.getName());
			Bukkit.broadcastMessage("§eLe nouveau §dleader §ede l'alliance §9Alliés §eest §6" + target.getName() + " §e!");
		}

		this.plugin.saveConfig();
	}
	
	@Command(name = "alliance.removeLeader", permission = "alliance.staff", inGameOnly = false)
	public void removeLeaderCommand(CommandArgs args) {
		if (args.length() < 1) {
			args.getSender().sendMessage("§e/alliance removeleader <joueur> <urss/allies/axe>");
			return;
		}
		
		Player target = Bukkit.getPlayer(args.getArgs(0));
		if (target == null) return;
		
		UPlayer fplayer = UPlayer.get(target);
		if (!fplayer.hasFaction()) return;
		
		switch (args.getArgs(1)) {
		case "urss":
			this.plugin.getConfig().set("settings.urss", "personne");
			Bukkit.broadcastMessage("§eLe poste de §dleader §ede l'alliance §9URSS §eest §6libre §e!");
			break;
		case "axe":
			this.plugin.getConfig().set("settings.axe", "personne");
			Bukkit.broadcastMessage("§eLe poste de §dleader §ede l'alliance §9Axe §eest §6libre §e!");
			break;
		case "allies":
			this.plugin.getConfig().set("settings.allied", "personne");
			Bukkit.broadcastMessage("§eLe poste de §dleader §ede l'alliance §9Alliés §eest §6libre §e!");
			break;
		}
	}
	
	@Command(name = "alliance.invite", permission = "alliance.use")
	public void inviteCommand(CommandArgs args) {
		if (!(this.plugin.getConfig().getString("settings.axe").equalsIgnoreCase(args.getPlayer().getName()) || this.plugin.getConfig().getString("settings.urss").equalsIgnoreCase(args.getPlayer().getName()) || this.plugin.getConfig().getString("settings.allied").equalsIgnoreCase(args.getPlayer().getName()))) {
			args.getSender().sendMessage("§cVous devez être le leader de l'alliance !");
			return;
		}
		
		if (args.length() < 0) {
			args.getSender().sendMessage("§eUsage: /alliance invite <pays>");
			return;
		}
		
		Faction faction = null;
		
		for (Faction consumer : FactionColls.get().get(args.getSender()).getAll(null, FactionListComparator.get())) {
			if (consumer.getName().equalsIgnoreCase(args.getArgs(0))) {
				faction = consumer;
				break;
			}
		}

		if (faction == null) {
			args.getSender().sendMessage("§cFaction introuveable !");
			return;
		}
		
		if (this.invitedURSS.contains(faction.getName()) || this.invitedAllied.contains(faction.getName()) || this.invitedAxe.contains(faction.getName())) {
			args.getSender().sendMessage("§cCe Pays à déjà été invité !");
			return;
		}
		
		if (this.plugin.getConfig().getString("settings.axe").equalsIgnoreCase(args.getPlayer().getName())) {
			invitedAxe.add(faction.getName());
			for (Player player : faction.getOnlinePlayers()) {
				player.sendMessage("§eVous avez reçu une §dinvitation §epour rejoindre l'alliance §9 Axe");
			}
		} else if (this.plugin.getConfig().getString("settings.urss").equalsIgnoreCase(args.getPlayer().getName())){
			invitedURSS.add(faction.getName());
			for (Player player : faction.getOnlinePlayers()) {
				player.sendMessage("§eVous avez reçu une §dinvitation §epour rejoindre l'alliance §9 URSS");
			}
		} else if (this.plugin.getConfig().getString("settings.allied").equalsIgnoreCase(args.getPlayer().getName())) {
			invitedAllied.add(faction.getName());
			for (Player player : faction.getOnlinePlayers()) {
				player.sendMessage("§eVous avez reçu une §dinvitation §epour rejoindre l'alliance §9 alliés");
			}
		}
		
		args.getSender().sendMessage("§eVous venez d'envoyer une §dinvitation §eau pays §9" + faction.getName() + "§e.");
		return;
	}

	@Command(name = "alliance.join", permission = "alliance.use")
	public void joinCommand(CommandArgs args) {
		Player player = args.getPlayer();
		
		if (args.length() < 1) {
			player.sendMessage("§e/" + args.getLabel() + " join <urss/allies/axe>");
			return;
		}
		
		UPlayer fplayer = UPlayer.get(player);
		
		if (fplayer.getRole() != Rel.LEADER) {
			player.sendMessage("§cVous devez être le leader !");
			return;
		}
		
		String alliance = args.getArgs(0);
		FactionData data = this.plugin.getStatsManager().getFaction(fplayer.getFactionName());
		
		if (alliance.equalsIgnoreCase("URSS") && this.invitedURSS.contains(fplayer.getFactionName()) && (data.isAlly() == false && data.isAxe() == false && data.isUrss() == false)) {
			this.plugin.getConfig().set("settings.pays_urss", this.plugin.getConfig().getInt("settings.pays_urss") + 1);
			Bukkit.broadcastMessage("§eLa faction §6" + data.getFactionName() + " §ea §arejoint §el'alliance §dURSS");
			data.setUrss(true);
			this.plugin.saveConfig();
			return;
		} else if (alliance.equalsIgnoreCase("AXE") && this.invitedAxe.contains(fplayer.getFactionName()) && (data.isAlly() == false && data.isAxe() == false && data.isUrss() == false)) {
			this.plugin.getConfig().set("settings.pays_axe", this.plugin.getConfig().getInt("settings.pays_axe") + 1);
			Bukkit.broadcastMessage("§eLa faction §6" + data.getFactionName() + " §ea §arejoint §el'alliance §8Axe");
			data.setAxe(true);
			this.plugin.saveConfig();
			return;
		} else if (alliance.equalsIgnoreCase("Allies") && this.invitedAllied.contains(fplayer.getFactionName()) && (data.isAlly() == false && data.isAxe() == false && data.isUrss() == false)) {
			this.plugin.getConfig().set("settings.pays_allied", this.plugin.getConfig().getInt("settings.pays_allied") + 1);
			Bukkit.broadcastMessage("§eLa faction §6" + data.getFactionName() + " §ea §arejoint §el'alliance §5Alliés");
			data.setAlly(true);
			this.plugin.saveConfig();
			return;
		} else {
			player.sendMessage("§eVous n'avez reçu aucune invitation !");
			return;
		}
	}
	
	@Command(name = "alliance.leave", permission = "alliance.use")
	public void leaveCommand(CommandArgs args) {
		Player player = args.getPlayer();
		
		if (args.length() < 1) {
			player.sendMessage("§e/" + args.getLabel() + " leave");
			return;
		}
		
		UPlayer fplayer = UPlayer.get(player);
		
		if (fplayer.getRole() != Rel.LEADER) {
			player.sendMessage("§cVous devez être le leader !");
			return;
		}

		FactionData data = this.plugin.getStatsManager().getFaction(fplayer.getFactionName());
		
		if (data.isAlly()) {
			Bukkit.broadcastMessage("§eLe Pays §9" + data.getFactionName() + "§e a quitté l'alliance §dAlliés§e !");
			this.plugin.getConfig().set("settings.pays_allied", this.plugin.getConfig().getInt("settings.pays_allied") - 1);
			data.setAlly(false);
			invitedAllied.remove(data.getFactionName());
			this.plugin.saveConfig();
			return;
		} else if (data.isAxe()) {
			Bukkit.broadcastMessage("§eLe Pays §9" + data.getFactionName() + "§e a quitté l'alliance §dAxe§e !");
			this.plugin.getConfig().set("settings.pays_axe", this.plugin.getConfig().getInt("settings.pays_axe") - 1);
			data.setAxe(false);
			invitedAxe.remove(data.getFactionName());
			this.plugin.saveConfig();
			return;
		} else if (data.isUrss()) {
			Bukkit.broadcastMessage("§eLe Pays §9" + data.getFactionName() + "§e a quitté l'alliance §dURSS§e !");
			this.plugin.getConfig().set("settings.pays_urss", this.plugin.getConfig().getInt("settings.pays_urss") - 1);
			data.setUrss(false);
			invitedURSS.remove(data.getFactionName());
			this.plugin.saveConfig();
			return;
		} else {
			player.sendMessage("§cVous ne faites partie d'aucune alliance !");
			return;
		}
	}
}
