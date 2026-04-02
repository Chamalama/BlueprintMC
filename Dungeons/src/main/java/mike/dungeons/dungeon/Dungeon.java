package mike.dungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.util.FastLocation;

import java.util.LinkedList;

@Getter
@Setter
public abstract class Dungeon {

    private final String dungeonName, worldName;
    private final Difficulty difficulty;
    private final LinkedList<DungeonRoom> dungeonRooms;
    protected FastLocation spawnLocation;
    private boolean active;

    public Dungeon(String dungeonName, String worldName, Difficulty difficulty) {
        this.dungeonName = dungeonName;
        this.worldName = worldName;
        this.difficulty = difficulty;
        this.dungeonRooms = new LinkedList<>();
        this.spawnLocation = null;
        this.active = false;
    }

    public void addRoom(DungeonRoom dungeonRoom) {
        this.dungeonRooms.add(dungeonRoom);
    }

}
