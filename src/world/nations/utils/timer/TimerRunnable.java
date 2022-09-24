package world.nations.utils.timer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import world.nations.Core;
import world.nations.utils.timer.event.TimerExpireEvent;

public class TimerRunnable {
    private final UUID player;
    private final Timer timer;
    private BukkitTask bukkitTask;
    private long expiryMillis;
    private long pauseMillis;

    public TimerRunnable(Timer timer, long duration) {
        this.player = null;
        this.timer = timer;
        this.setRemaining(duration);
    }

    public TimerRunnable(UUID player, Timer timer, long duration) {
        this.player = player;
        this.timer = timer;
        this.setRemaining(duration);
    }

    public long getRemaining() {
        return this.getRemaining(false);
    }

    public void setRemaining(long remaining) {
        this.setExpiryMillis(remaining);
    }

    public long getRemaining(boolean ignorePaused) {
        if (!ignorePaused && this.pauseMillis != 0) {
            return this.pauseMillis;
        }
        return this.expiryMillis - System.currentTimeMillis();
    }

    private void setExpiryMillis(long remainingMillis) {
		long expiryMillis = System.currentTimeMillis() + remainingMillis;
		if (expiryMillis == this.expiryMillis) {
			return;
		}
		this.expiryMillis = expiryMillis;
		if (remainingMillis > 0) {
			if (this.bukkitTask != null) {
				this.bukkitTask.cancel();
			}
			this.bukkitTask = new BukkitRunnable() {

				public void run() {
					TimerExpireEvent event = new TimerExpireEvent(TimerRunnable.this.player, TimerRunnable.this.timer);
					Bukkit.getPluginManager().callEvent((Event) event);
				}
			}.runTaskLater(Core.getPlugin(), remainingMillis / 50);
		}
	}

	public boolean isPaused() {
		if (this.pauseMillis != 0) {
			return true;
		}
		return false;
	}

	public void setPaused(boolean paused) {
		if (paused == this.isPaused()) {
			return;
		}
		if (paused) {
			this.pauseMillis = this.getRemaining(true);
			this.cancel();
		} else {
			this.setExpiryMillis(this.pauseMillis);
			this.pauseMillis = 0;
		}
	}

	public void cancel() {
		if (this.bukkitTask != null) {
			this.bukkitTask.cancel();
		}
	}

	public UUID getPlayer() {
		return this.player;
	}

	public Timer getTimer() {
		return this.timer;
	}

	public BukkitTask getBukkitTask() {
		return this.bukkitTask;
	}

	public long getExpiryMillis() {
		return this.expiryMillis;
	}

    public long getPauseMillis() {
        return this.pauseMillis;
    }

    public void setBukkitTask(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    public void setPauseMillis(long pauseMillis) {
        this.pauseMillis = pauseMillis;
    }
}