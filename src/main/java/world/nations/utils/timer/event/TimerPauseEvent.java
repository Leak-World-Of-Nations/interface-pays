package world.nations.utils.timer.event;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import world.nations.utils.timer.Timer;

public class TimerPauseEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final boolean paused;
    private final Optional<UUID> userUUID;
    private final Timer timer;

    public TimerPauseEvent(Timer timer, boolean paused) {
        this.userUUID = Optional.empty();
        this.timer = timer;
        this.paused = paused;
    }

    public TimerPauseEvent(UUID userUUID, Timer timer, boolean paused) {
        this.userUUID = Optional.ofNullable(userUUID);
        this.timer = timer;
        this.paused = paused;
    }
    
    public Optional<UUID> getUserUUID() {
        return userUUID;
    }
    
    public Timer getTimer() {
        return timer;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
