package world.nations.utils.timer;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import world.nations.Core;
import world.nations.utils.timer.event.TimerExpireEvent;

public class TimerCooldown {

    private BukkitTask eventNotificationTask;

    @Getter
    private final Timer timer;

    private final UUID owner;

    @Getter
    private long expiryMillis;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private long pauseMillis;

    protected TimerCooldown(Timer timer, long duration) {
        this.owner = null;
        this.timer = timer;
        this.setRemaining(duration);
    }

    protected TimerCooldown(Timer timer, UUID playerUUID, long duration) {
        this.timer = timer;
        this.owner = playerUUID;
        this.setRemaining(duration);
    }

    public long getRemaining() {
        return getRemaining(false);
    }

    protected long getRemaining(boolean ignorePaused) {
        if (!ignorePaused && pauseMillis != 0L) {
            return pauseMillis; // If isn't paused, return that.
        } else {
            return expiryMillis - System.currentTimeMillis();
        }
    }

    protected void setRemaining(long milliseconds) throws IllegalStateException {
        if (milliseconds <= 0L) {
            cancel();
            return;
        }

        long expiryMillis = System.currentTimeMillis() + milliseconds;
        if (expiryMillis != this.expiryMillis) {
            this.expiryMillis = expiryMillis;

            // Recreate the task manually as Bukkit doesn't allow
            // you to just reschedule for some reason :(.
            if (eventNotificationTask != null) {
                eventNotificationTask.cancel();
            }

            long ticks = milliseconds / 50L;
            eventNotificationTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (timer instanceof PlayerTimer && owner != null) {
                        ((PlayerTimer) timer).handleExpiry(Bukkit.getPlayer(owner), owner);
                    }

                    Bukkit.getPluginManager().callEvent(new TimerExpireEvent(owner, timer));
                }
            }.runTaskLater(Core.getPlugin(), ticks);
        }
    }

    protected boolean isPaused() {
        return pauseMillis != 0L;
    }

    public void setPaused(boolean paused) {
        if (paused != isPaused()) {
            if (paused) {
                pauseMillis = getRemaining(true);
                cancel();
            } else {
                setRemaining(pauseMillis);
                pauseMillis = 0L;
            }
        }
    }

    /**
     * Cancels this runnable for event notification.
     *
     * @throws IllegalStateException if was not running
     */
    protected void cancel() throws IllegalStateException {
        if (eventNotificationTask != null) {
            eventNotificationTask.cancel();
            eventNotificationTask = null;
        }
    }
}
