package mike.dungeons.dungeon.dungeons.outpost;

import mike.blueprint.util.FastLocation;
import mike.dungeons.dungeon.Difficulty;
import mike.dungeons.dungeon.Dungeon;

public class Outpost extends Dungeon {

    public Outpost() {
        super("Outpost", "dungeon_outpost", Difficulty.EASY);
        this.spawnLocation = new FastLocation("dungeon_outpost", -536, 211, 81, 91, 0);
    }

}
