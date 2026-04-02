package mike.dungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.config.Config;
import mike.blueprint.util.FastLocation;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.component.GenericEventComponent;
import mike.dungeons.dungeon.component.SpawnComponent;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.dungeon.team.EncounterData;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class DungeonRoom extends Config {

    private final transient Dungeon owningDungeon;
    private final transient String roomID;
    private final transient Map<Class<?>, Object> roomComponents;
    private DungeonRegion roomRegion;
    private FastLocation encounterSpawnPoint;
    private TriggerPoint startPoint;
    protected long encounterTime;
    protected boolean startOnRegionEnter;

    public DungeonRoom(Dungeon owningDungeon, String roomID) {
        super(Dungeons.getInst(), "dungeon_rooms", roomID);
        this.owningDungeon = owningDungeon;
        this.roomID = roomID;
        this.roomComponents = new HashMap<>();
        this.roomRegion = null;
        this.encounterSpawnPoint = null;
        this.startPoint = null;
        this.encounterTime = 60;
        this.startOnRegionEnter = false;
    }

    @Override
    public void init() {
        this.owningDungeon.addRoom(this);
        Dungeons.getInst().log("Adding room " + this.getRoomID() + " to dungeon " + this.owningDungeon.getDungeonName() + "...");
    }

    public void copyState(DungeonTeam team) {
        final EncounterData data = team.getEncounterData();
        data.setEncounterSpawnPoint(null);
        data.setEncounterPoint(null);
        data.getActivatedTriggers().clear();
        copyEncounterSpawns(team);
        final GenericEventComponent eventComponent = getComponent(GenericEventComponent.class);
        if(eventComponent != null) {
            data.setEncounterEvents(eventComponent.clone());
        }
        data.setEncounterSpawnPoint(encounterSpawnPoint.clone().edit(fastLocation -> fastLocation.setWorldName(team.getWorldName())));
        data.setEncounterPoint(startPoint.clone().edit(triggerPoint -> triggerPoint.getTriggerLocation().edit(fastLocation -> fastLocation.setWorldName(team.getWorldName()))));
        data.setEncounterTime(encounterTime);
    }

    public void copyEncounterSpawns(DungeonTeam team) {
        final EncounterData data = team.getEncounterData();
        data.getEncounterSpawns().clear();
        if (hasComponent(SpawnComponent.class)) {
            final SpawnComponent spawnComponent = getComponent(SpawnComponent.class);
            data.getEncounterSpawns().addAll(spawnComponent.getSpawnLocations().stream().map(
                    entityLocation -> entityLocation.clone().edit(
                            loc -> loc.getSpawnLocation().edit(
                                    fastLocation -> fastLocation.setWorldName(team.getWorldName())))).toList());
        }
    }

    public void handleEntityDeath(EntityDeathEvent event, DungeonTeam dungeonTeam) {

    }

    public void startEncounter(DungeonTeam dungeonTeam) {

    }

    public <T> T getComponent(Class<T> componentClass) {
        return componentClass.cast(roomComponents.get(componentClass));
    }

    public <T> DungeonRoom addComponent(T component) {
        roomComponents.put(component.getClass(), component);
        return this;
    }

    public boolean hasComponent(Class<?> component) {
        return roomComponents.containsKey(component);
    }

}
