package mike.dungeons.dungeon.entity.component;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.util.MovementUtil;
import mike.dungeons.dungeon.entity.DungeonEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

@Getter
@Setter
public class MovementComponent {

    private double speed, stopDistance;

    public MovementComponent(double speed, double stopDistance) {
        this.speed = speed;
        this.stopDistance = stopDistance;
    }

    public void move(LivingEntity dungeonEntity, Location location) {
        if(dungeonEntity == null || dungeonEntity.isDead()) return;
        MovementUtil.moveTo(dungeonEntity, location, stopDistance, speed);
    }

}
