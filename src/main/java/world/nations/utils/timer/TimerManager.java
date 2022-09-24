package world.nations.utils.timer;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Data;
import lombok.Getter;
import world.nations.utils.ConfigCreator;

@Data
public class TimerManager implements Listener {
	
    @Getter private final Set<Timer> timers = new LinkedHashSet<>();

    private final JavaPlugin plugin;
    private ConfigCreator config;

    public TimerManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
        reloadTimerData();
    }

    public void registerTimer(Timer timer) {
        timers.add(timer);
        if (timer instanceof Listener) {
            plugin.getServer().getPluginManager().registerEvents((Listener) timer, plugin);
        }
    }

    public void unregisterTimer(Timer timer) {
        timers.remove(timer);
    }
    
    public void reloadTimerData() {
        config = new ConfigCreator(plugin, "timers");
        for (Timer timer : timers) {
            timer.load(config);
        }
    }
    
    public void saveTimerData() {
        for (Timer timer : timers) {
            timer.onDisable(config);
        }

        config.save();
    }
}