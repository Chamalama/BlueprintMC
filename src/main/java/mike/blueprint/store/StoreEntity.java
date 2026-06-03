package mike.blueprint.store;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
public abstract class StoreEntity {

    private final String name;
    private final EntityType entityType;
    private LivingEntity bukkitEntity;

    private Consumer<LivingEntity> entityConsumer;

    private final Map<Class<?>, Object> components = new HashMap<>();

    public StoreEntity(String name, EntityType entityType) {
        this.name = name;
        this.entityType = entityType;
        this.bukkitEntity = null;
    }

    public void edit(Consumer<LivingEntity> entityConsumer) {
        this.entityConsumer = entityConsumer;
    }

    public <Component> StoreEntity addComponent(Component component) {
        this.components.put(component.getClass(), component);
        return this;
    }

    public void spawn(Location location) {
        final World world = location.getWorld();
        this.bukkitEntity = (LivingEntity) world.spawnEntity(location, entityType);
        this.entityConsumer.accept(bukkitEntity);
    }

}
