package mike.dungeons.dungeon.entity.system;

import mike.blueprint.loader.Component;
import mike.blueprint.util.AbstractTask;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.entity.DungeonEntity;
import mike.dungeons.dungeon.entity.DungeonMobs;
import mike.dungeons.dungeon.entity.component.GenericAIComponent;

@Component
public class GenericAISystem extends AbstractTask {

    public GenericAISystem() {
        super(Dungeons.getInst(), 0, 2, false);
    }

    @Override
    public void run() {
        for(DungeonEntity dungeonEntity : DungeonMobs.getComponentEntities(GenericAIComponent.class)) {
            final GenericAIComponent aiComponent = dungeonEntity.getComponent(GenericAIComponent.class);
            aiComponent.tick(dungeonEntity);
        }
    }
}
