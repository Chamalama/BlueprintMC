package mike.dungeons.dungeon.component;

import lombok.Getter;
import mike.dungeons.dungeon.team.DungeonTeam;
import org.bukkit.entity.Entity;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class KillComponent {

    private final BiConsumer<DungeonTeam, Entity> consumer;

    public KillComponent(BiConsumer<DungeonTeam, Entity> consumer) {
        this.consumer = consumer;
    }

    public void handle(DungeonTeam team, Entity entity) {
        consumer.accept(team, entity);
    }

}
