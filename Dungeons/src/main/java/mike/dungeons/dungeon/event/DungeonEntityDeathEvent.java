package mike.dungeons.dungeon.event;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class DungeonEntityDeathEvent extends Event implements Cancellable {

    public static HandlerList HANDLER_LIST = new HandlerList();

    private final LivingEntity entity;
    private final LivingEntity killer;

    public DungeonEntityDeathEvent(LivingEntity entity, LivingEntity killer) {
        this.entity = entity;
        this.killer = killer;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
