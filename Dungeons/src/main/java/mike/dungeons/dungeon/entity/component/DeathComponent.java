package mike.dungeons.dungeon.entity.component;

import lombok.Getter;
import mike.dungeons.dungeon.entity.DungeonEntity;
import org.bukkit.entity.Entity;

import java.util.function.BiConsumer;

@Getter
public class DeathComponent {

    private final BiConsumer<DungeonEntity, Entity> onDeath;

    public DeathComponent(BiConsumer<DungeonEntity, Entity> onDeath) {
        this.onDeath = onDeath;
    }

    public void handle(DungeonEntity entity, Entity killer) {
        onDeath.accept(entity, killer);
    }

}
