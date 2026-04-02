package mike.dungeons.dungeon.entity.component;

import mike.dungeons.dungeon.entity.DungeonEntity;

public interface AIState {

    void tick(DungeonEntity entity, GenericAIComponent component);
    void start(DungeonEntity entity);
    void stop(DungeonEntity entity);
    void reset(DungeonEntity entity);

}
