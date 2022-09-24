package world.nations.utils.timer.event;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import world.nations.utils.timer.Timer;


public class TimerExpireEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Optional<Player> player;
    private final Optional<UUID> userUUID;
    private final Timer timer;

    public TimerExpireEvent(Timer timer) {
        this.userUUID = Optional.empty();
        this.timer = timer;
    }

    public TimerExpireEvent(UUID userUUID, Timer timer) {
        this.userUUID = Optional.ofNullable(userUUID);
        this.timer = timer;
    }

    public TimerExpireEvent(Player player, Timer timer) {
        Objects.requireNonNull(player);

        this.player = Optional.of(player);
        this.userUUID = Optional.of(player.getUniqueId());
        this.timer = timer;
    }

    public Optional<Player> getPlayer() {
        if (player == null) {
            player = userUUID.isPresent() ? Optional.of(Bukkit.getPlayer(userUUID.get())) : Optional.empty();
        }

        return player;
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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
