package world.nations.utils.timer.event;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import world.nations.utils.timer.Timer;

public class TimerExtendEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final Optional<Player> player;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private final long previousDuration;
    private long newDuration;

    public TimerExtendEvent(Timer timer, long previousDuration, long newDuration) {
        this.player = Optional.empty();
        this.userUUID = Optional.empty();
        this.timer = timer;
        this.previousDuration = previousDuration;
        this.newDuration = newDuration;
    }

    public TimerExtendEvent(@Nullable Player player, UUID uniqueId, Timer timer, long previousDuration, long newDuration) {
        this.player = Optional.ofNullable(player);
        this.userUUID = Optional.ofNullable(uniqueId);
        this.timer = timer;
        this.previousDuration = previousDuration;
        this.newDuration = newDuration;
    }

    public Optional<Player> getPlayer() {
        return player;
    }
    
    public Optional<UUID> getUserUUID() {
        return userUUID;
    }
    
    public Timer getTimer() {
        return timer;
    }

    public long getPreviousDuration() {
        return previousDuration;
    }

    public long getNewDuration() {
        return newDuration;
    }

    public void setNewDuration(long newDuration) {
        this.newDuration = newDuration;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
