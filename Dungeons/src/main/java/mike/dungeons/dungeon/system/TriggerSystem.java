package mike.dungeons.dungeon.system;

import mike.blueprint.loader.Component;
import mike.blueprint.util.AbstractTask;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.Dungeon;
import mike.dungeons.dungeon.DungeonRegistry;
import mike.dungeons.dungeon.DungeonRoom;
import mike.dungeons.dungeon.component.TriggerComponent;

@Component
public class TriggerSystem extends AbstractTask {

    public TriggerSystem() {
        super(Dungeons.getInst(), 0, 2, true);
    }

    @Override
    public void run() {
        for(DungeonRoom dungeonRoom : DungeonRegistry.getRooms().values()) {
            final Dungeon dungeon = dungeonRoom.getOwningDungeon();
            if(!dungeon.isActive()) continue;
        }
    }
}
