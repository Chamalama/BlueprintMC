package mike.dungeons.dungeon.entity.component;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Set;

@Getter
public class TargetComponent {

    private final double targetRadius;
    private final long retargetTime;
    private final Set<NamespacedKey> excludedTargets;
    private Entity target;
    private Location locationTarget;
    private long lastTargetTime;

    public TargetComponent(double targetRadius, long retargetTime, Set<NamespacedKey> excludedTargets) {
        this.targetRadius = targetRadius;
        this.retargetTime = retargetTime;
        this.excludedTargets = excludedTargets;
        this.target = null;
        this.locationTarget = null;
        this.lastTargetTime = 0L;
    }

    public boolean shouldRetarget() {
        if(target == null) return true;
        return System.currentTimeMillis() - lastTargetTime >= retargetTime;
    }

    public void target(LivingEntity entity) {
        this.target = entity;
        this.lastTargetTime = System.currentTimeMillis();
        this.locationTarget = null;
    }

    public void target(Location location) {
        if(target != null) return;
        this.locationTarget = location;
    }

}
