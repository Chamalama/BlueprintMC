package mike.dungeons.dungeon.entity;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.Getter;
import mike.dungeons.dungeon.entity.type.GateGuard;
import mike.dungeons.dungeon.team.DungeonTeam;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.*;
import java.util.function.Supplier;

@Getter
public enum DungeonMobs {

    GATE_GUARD(GateGuard::new);

    private final Supplier<DungeonEntity> entitySupplier;

    @Getter
    public static final Map<UUID, DungeonEntity> activeEntities = new HashMap<>();
    public static final Map<Class<?>, Set<DungeonEntity>> componentEntities = new HashMap<>();
    public static final BiMap<DungeonEntity, DungeonTeam> entityToTeam = HashBiMap.create();

    DungeonMobs(Supplier<DungeonEntity> entitySupplier) {
        this.entitySupplier = entitySupplier;
    }

    public static void registerComponent(Class<?> component, DungeonEntity entity) {
        componentEntities.computeIfAbsent(component, k -> new HashSet<>()).add(entity);
    }

    public static void register(DungeonEntity entity) {
        activeEntities.put(entity.getId(), entity);
    }

    public static void registerToTeam(DungeonEntity entity, DungeonTeam team) {
        entityToTeam.put(entity, team);
    }

    public static void unregister(Entity le) {
        final UUID id = le.getUniqueId();
        final DungeonEntity entity = activeEntities.get(id);
        activeEntities.remove(id);
        entityToTeam.remove(entity);
        if (entity != null) {
            componentEntities.values().forEach(dungeonEntities -> dungeonEntities.remove(entity));
        }
    }

    public static DungeonTeam getEntitiesTeam(DungeonEntity entity) {
        return entityToTeam.get(entity);
    }

    public static Set<DungeonEntity> getComponentEntities(Class<?> component) {
        return componentEntities.getOrDefault(component, Collections.emptySet());
    }

    public static Collection<DungeonEntity> getEntities() {
        return activeEntities.values();
    }

    public static DungeonEntity getEntity(UUID uuid) {
        return activeEntities.get(uuid);
    }

    public DungeonEntity spawnEntity() {
        return this.getEntitySupplier().get();
    }

}
