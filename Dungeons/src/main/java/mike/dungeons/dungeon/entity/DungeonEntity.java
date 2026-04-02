package mike.dungeons.dungeon.entity;

import lombok.Getter;
import mike.dungeons.dungeon.entity.component.CombatComponent;
import mike.dungeons.dungeon.entity.component.ScaleComponent;
import mike.dungeons.dungeon.entity.component.TagComponent;
import mike.dungeons.dungeon.team.DungeonTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public abstract class DungeonEntity {

    private final String entityName;
    private final EntityType entityType;
    private final Map<Class<?>, Object> components = new HashMap<>();

    private UUID id;

    public DungeonEntity(String entityName, EntityType entityType) {
        this.entityName = entityName;
        this.entityType = entityType;
        this.id = null;
    }

    public <T> DungeonEntity addComponent(T component) {
        components.put(component.getClass(), component);
        return this;
    }

    public <T> T getComponent(Class<T> clazz) {
        return clazz.cast(components.get(clazz));
    }

    public <T> boolean hasComponent(Class<T> clazz) {
        return components.containsKey(clazz);
    }

    public void spawn(Location location, DungeonTeam dungeonTeam, Consumer<LivingEntity> callback) {
        final LivingEntity dungeonEntity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        id = dungeonEntity.getUniqueId();
        if(dungeonEntity instanceof Mob mob) {
            mob.setAware(false);
        }
        if(hasComponent(TagComponent.class)) {
            getComponent(TagComponent.class).applyTags(dungeonEntity);
        }
        if(hasComponent(ScaleComponent.class)) {
            getComponent(ScaleComponent.class).scale(dungeonEntity);
        }
        if(hasComponent(CombatComponent.class)) {
            getComponent(CombatComponent.class).apply(dungeonEntity);
        }
        for(Class<?> component : components.keySet()) {
            DungeonMobs.registerComponent(component, this);
        }
        dungeonTeam.getEncounterData().addEntity(id);
        DungeonMobs.registerToTeam(this, dungeonTeam);
        DungeonMobs.register(this);
        callback.accept(dungeonEntity);
    }

    public LivingEntity getEntity() {
        return (LivingEntity) Bukkit.getEntity(id);
    }

}
