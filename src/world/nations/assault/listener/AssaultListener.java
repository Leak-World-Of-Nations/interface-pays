package world.nations.assault.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.massivecraft.factions.entity.UPlayer;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import world.nations.Core;
import world.nations.assault.data.AssaultData;
import world.nations.utils.DurationFormatter;
import world.nations.utils.timings.CooldownTimer;
import world.nations.utils.timings.CountdownTimer;

public class AssaultListener implements Listener {

	private Core plugin;

	private Map<Player, CountdownTimer> timer = new HashMap<>();
	
	public AssaultListener(Core plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		UPlayer fplayer = UPlayer.get(event.getEntity());
		UPlayer kplayer = UPlayer.get(event.getEntity().getKiller());
		if (fplayer.hasFaction()) {
			if (CooldownTimer.isOnCooldown("assault", fplayer.getFaction()) || CooldownTimer.isOnCooldown("assault", kplayer.getFaction())) {

				for (AssaultData consumer : this.plugin.getAssaultManager().getAssaultList()) {

					if (consumer.getFactionName() == fplayer.getFactionName() && consumer.getFactionTarget() == kplayer.getFactionName()) {
						consumer.addTargetPoints(1);

						//plugin.getStatsManager().getFaction(consumer.getFactionTarget()).addKill();
						//plugin.getStatsManager().getFaction(consumer.getFactionName()).addDeath();

						Bukkit.broadcastMessage("§6" + fplayer.getPlayer().getName() + " §eest mort au combat, §c" + kplayer.getFactionName() + " §egagne un point (" + consumer.getPointsTarget() + ")");
						Bukkit.broadcastMessage("§6Temps restant : " + DurationFormatter.getRemaining((consumer.getTimer().getSecondsLeft() * 1000), false));
						break;
					} else if (consumer.getFactionTarget() == fplayer.getFactionName() && consumer.getFactionName() == kplayer.getFactionName()) {
						consumer.addPoints(1);

						//plugin.getStatsManager().getFaction(consumer.getFactionName()).addKill();
						//plugin.getStatsManager().getFaction(consumer.getFactionTarget()).addDeath();

						Bukkit.broadcastMessage("§6" + fplayer.getPlayer().getName() + " §eest mort au combat, §c" + kplayer.getFactionName() + " §egagne un point (" + consumer.getPoints() + ")");
						Bukkit.broadcastMessage("§6Temps restant : " + DurationFormatter.getRemaining((consumer.getTimer().getSecondsLeft() * 1000), false));
						break;
					}

					for(String ally : consumer.getFactionAllied()) {

						if(ally == fplayer.getFactionName() && consumer.getFactionTarget() == kplayer.getFactionName()) {
							//quand ally se fait tué
							consumer.addTargetPoints(1);

							Bukkit.broadcastMessage("§6" + fplayer.getPlayer().getName() + " §eest mort au combat, §c" + kplayer.getFactionName() + " §egagne un point (" + consumer.getPointsTarget() + ")");
							Bukkit.broadcastMessage("§6Temps restant : " + DurationFormatter.getRemaining((consumer.getTimer().getSecondsLeft() * 1000), false));
							break;
						} else if(ally == kplayer.getFactionName() && consumer.getFactionTarget() == fplayer.getFactionName()) {
							//quand ally fait un kill
							consumer.addPoints(1);

							Bukkit.broadcastMessage("§6" + fplayer.getPlayer().getName() + " §eest mort au combat, §c" + kplayer.getFactionName() + " §egagne un point (" + consumer.getPoints() + ")");
							Bukkit.broadcastMessage("§6Temps restant : " + DurationFormatter.getRemaining((consumer.getTimer().getSecondsLeft() * 1000), false));
							break;
						}

					}

				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UPlayer fplayer = UPlayer.get(player);

		if(fplayer.hasFaction() && CooldownTimer.isOnCooldown(player.getName(), fplayer.getFaction()) && timer.containsKey(player)) {
			CooldownTimer.removeCooldown(player.getName(), fplayer.getFaction());
			Bukkit.getScheduler().cancelTask(timer.get(player).getAssignedTaskId());
		}

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UPlayer fplayer = UPlayer.get(player);

		if(!fplayer.hasFaction()) return;

		for(AssaultData consumer : this.plugin.getAssaultManager().getAssaultList()) {
			if(consumer.getFactionName() == fplayer.getFactionName() || consumer.getFactionTarget() == fplayer.getFactionName()) {
				CountdownTimer countdownTimer = new CountdownTimer(plugin, 180, () -> {
					CooldownTimer.addCooldown(player.getName(), fplayer.getFaction(), 180);
					Bukkit.broadcastMessage("§6" + player.getName() + " §ec'est déconnecté pendant l'assault de son pays, il a 3min pour se reconnecter");
				}, () -> {
					for(AssaultData assault : this.plugin.getAssaultManager().getAssaultList()) {

						if(CooldownTimer.isOnCooldown(player.getName(), fplayer.getFaction())) {

							if (assault.getFactionName() == fplayer.getFactionName()) {
								assault.addTargetPoints(1);
								Bukkit.broadcastMessage("§eLa pays §6" + assault.getFactionTarget() + " gagne 1 point grace à la deconnexion de §6" + fplayer.getName());
							}
							if (assault.getFactionTarget() == fplayer.getFactionName()) {
								assault.addPoints(1);
								Bukkit.broadcastMessage("§eLa pays §6" + assault.getFactionName() + " gagne 1 point grace à la deconnexion de §6" + fplayer.getName());
							}
						}
					}

					if(timer.containsKey(player)) {
						CooldownTimer.removeCooldown(player.getName(), fplayer.getFaction());
					}

				}, (t) -> {});

				countdownTimer.scheduleTimer();
				timer.put(player, countdownTimer);

			}
		}


	}

}
