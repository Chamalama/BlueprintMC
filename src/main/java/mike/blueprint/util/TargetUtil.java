package mike.blueprint.util;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Collection;
import java.util.function.Predicate;

public class TargetUtil {

    private static final Predicate<Entity> ENTITY_PREDICATE = entity -> entity instanceof LivingEntity;

    public static LivingEntity findClosestEntity(LivingEntity targeter, double distance, NamespacedKey... keys) {
        if(targeter == null || targeter.isDead()) return null;
        double currDistance = distance * distance;
        LivingEntity target = null;
        final int id = targeter.getEntityId();

        final Collection<Entity> nearby = targeter.getWorld().getNearbyEntities(
                targeter.getLocation(),
                distance, distance, distance,
                ENTITY_PREDICATE
        );

        for(Entity entity : nearby) {
            final LivingEntity le = (LivingEntity) entity;
            if(le == null || !le.isValid() || le.isDead() || le.getEntityId() == id) continue;
            final Location entityLocation = le.getLocation();
            final double checkDistance = entityLocation.distanceSquared(targeter.getLocation());
            if(checkDistance > currDistance) continue;
            final PersistentDataContainer pdc = le.getPersistentDataContainer();
            boolean excluded = false;
            for(NamespacedKey key : keys) {
                if(pdc.has(key)) {
                    excluded = true;
                    break;
                }
            }
            if(excluded) continue;
            currDistance = checkDistance;
            target = le;
        }
        return target;
    }

}
