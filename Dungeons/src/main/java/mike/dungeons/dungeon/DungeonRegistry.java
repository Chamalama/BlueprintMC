package mike.dungeons.dungeon;

import lombok.Getter;
import mike.blueprint.loader.Component;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.dungeons.outpost.GateRoom;
import mike.dungeons.dungeon.dungeons.outpost.Outpost;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class DungeonRegistry {

    private static final Map<Class<? extends Dungeon>, Dungeon> dungeons = new HashMap<>();
    private static final Map<Class<? extends DungeonRoom>, DungeonRoom> dungeonRooms = new HashMap<>();
    private static final Map<Class<?>, Set<DungeonRoom>> componentRooms = new HashMap<>();

    @Getter
    private static final Map<String, DungeonRoom> rooms = new HashMap<>();

    public DungeonRegistry() {
        registerDungeon(Outpost.class, new Outpost());
        registerRoom(GateRoom.class, new GateRoom());
    }

    public static void registerDungeon(Class<? extends Dungeon> dungeonClass, Dungeon dungeon) {
        dungeons.put(dungeonClass, dungeon);
        Dungeons.getInst().log("Dungeon rooms for " + dungeon.getDungeonName() + " " + dungeon.getDungeonRooms().size());
    }

    public static void registerRoom(Class<? extends DungeonRoom> roomClass, DungeonRoom dungeonRoom) {
        dungeonRoom.load();
        dungeonRooms.put(roomClass, dungeonRoom);
        rooms.put(dungeonRoom.getRoomID(), dungeonRoom);
    }

    public static Dungeon getDungeon(Class<? extends Dungeon> d) {
        return dungeons.get(d);
    }

    public static DungeonRoom getRoom(Class<? extends DungeonRoom> room) {
        return dungeonRooms.get(room);
    }

    public static DungeonRoom getRoom(String room) {
        return rooms.get(room);
    }

}
