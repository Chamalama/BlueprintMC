package mike.dungeons.dungeon.team;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.util.FastLocation;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.DungeonRoom;
import mike.dungeons.dungeon.EntityLocation;
import mike.dungeons.dungeon.component.GenericEventComponent;
import mike.dungeons.dungeon.component.TriggerComponent;
import mike.dungeons.dungeon.TriggerPoint;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.*;

@Getter
@Setter
public class EncounterData {

    private DungeonRoom currentEncounter = null;
    private FastLocation encounterSpawnPoint = null;
    private TriggerPoint encounterPoint = null;
    private GenericEventComponent encounterEvents = null;
    private final Map<Class<?>, Object> encounterState = new HashMap<>();
    private final List<TriggerComponent> encounterComponents = new ArrayList<>();
    private final List<EntityLocation> encounterSpawns = new ArrayList<>();
    private final List<FastLocation> roamPoints = new ArrayList<>();
    private final List<Integer> activatedTriggers = new ArrayList<>();
    private final List<UUID> activeMobs = new ArrayList<>();
    private boolean encounterStarted = false;
    private long encounterTime = 0L;
    private int encountersCleared = 0;

    public List<Entity> getBukkitMobs() {
        return activeMobs.stream().map(Bukkit::getEntity).toList();
    }

    public void addEntity(LivingEntity entity) {
        this.activeMobs.add(entity.getUniqueId());
    }

    public void addEntity(UUID uuid) {
        this.activeMobs.add(uuid);
    }

    public <T> void setState(Class<T> type, T state) {
        encounterState.put(type, state);
    }

    public <T> T getState(Class<T> type) {
        return type.cast(encounterState.get(type));
    }

    public <T> boolean hasState(Class<T> type) {
        return encounterState.containsKey(type);
    }

    public void clearState() {
        this.encounterState.clear();
    }

    public void clear() {
        this.encounterStarted = false;
        for (Entity entity : getBukkitMobs()) {
            if (entity != null) {
                entity.getPassengers().forEach(Entity::remove);
                entity.remove();
            }
        }
        activeMobs.clear();
        activatedTriggers.clear();
        encounterSpawns.forEach(entityLocation -> entityLocation.setActiveEntities(0));
        if(currentEncounter != null) {
            this.encounterTime = currentEncounter.getEncounterTime();
        }
    }

}
