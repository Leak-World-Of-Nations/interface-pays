package world.nations.utils.timer.event;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import world.nations.utils.timer.Timer;

public class TimerStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Optional<Player> player;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private final long duration;

    public TimerStartEvent(Timer timer, final long duration) {
        this.player = Optional.empty();
        this.userUUID = Optional.empty();
        this.timer = timer;
        this.duration = duration;
    }

    public TimerStartEvent(@Nullable Player player, UUID uniqueId, Timer timer, long duration) {
        this.player = Optional.ofNullable(player);
        this.userUUID = Optional.ofNullable(uniqueId);
        this.timer = timer;
        this.duration = duration;
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

    public long getDuration() {
        return duration;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
