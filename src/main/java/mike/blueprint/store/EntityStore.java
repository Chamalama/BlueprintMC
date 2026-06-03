package mike.blueprint.store;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class EntityStore {

    private static final Object2ObjectOpenHashMap<UUID, StoreEntity> storedEntities = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<UUID, Map<Class<?>, Object>> entityComponents = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectOpenHashMap<Class<?>, Set<UUID>> componentEntities = new Object2ObjectOpenHashMap<>();

    public static void register(StoreEntity entity) {
        final LivingEntity storeEntity = entity.getBukkitEntity();
        entityComponents.put(storeEntity.getUniqueId(), new HashMap<>());
        for(Object o : entity.getComponents().values()) {
            addComponent(storeEntity.getUniqueId(), o);
        }
        storedEntities.put(storeEntity.getUniqueId(), entity);
    }

    public static <Component> void addComponent(UUID id, Component component) {
        final Map<Class<?>, Object> components = getComponents(id);
        components.put(component.getClass(), component);
        componentEntities.computeIfAbsent(component.getClass(), k -> new HashSet<>()).add(id);
    }

    public static void removeComponent(UUID id, Class<?> componentClass) {
        final StoreEntity legend = getStoredEntity(id);
        final Map<Class<?>, Object> components = getComponents(id);
        components.remove(componentClass);
        legend.getComponents().remove(componentClass);
        final Set<UUID> entities = componentEntities.get(componentClass);
        if(entities.isEmpty()) return;
        entities.remove(id);
    }

    public static void unregister(LivingEntity entity) {
        final UUID entityID = entity.getUniqueId();
        entity.getPassengers().forEach(Entity::remove);
        if(!entityComponents.containsKey(entityID)) return;
        final Set<Class<?>> componentClasses = entityComponents.get(entityID).keySet();
        for(Class<?> componentClass : componentClasses) {
            componentEntities.get(componentClass).remove(entityID);
        }
        entityComponents.remove(entityID);
        storedEntities.remove(entityID);
    }

    public static void cleanup() {
        for(StoreEntity storeEntity : storedEntities.values()) {
            unregister(storeEntity.getBukkitEntity());
            storeEntity.getBukkitEntity().remove();
        }
    }

    public static boolean hasComponent(UUID entityID, Class<?> componentClazz) {
        return componentEntities.get(componentClazz).contains(entityID);
    }

    public static Map<Class<?>, Object> getComponents(UUID uuid) {
        return entityComponents.get(uuid);
    }

    public static <Component> Component getComponent(UUID entity, Class<Component> componentClass) {
        final Map<Class<?>, Object> components = getComponents(entity);
        return componentClass.cast(components.get(componentClass));
    }

    public static <Component> Set<UUID> getComponentEntities(Class<Component> component) {
        return componentEntities.getOrDefault(component, new HashSet<>());
    }

    public static StoreEntity getStoredEntity(UUID id) {
        return storedEntities.get(id);
    }

}
