package mike.dungeons.dungeon.entity.system;

import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.math.Position;
import mike.blueprint.loader.Component;
import mike.blueprint.util.AbstractTask;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.entity.DungeonEntity;
import mike.dungeons.dungeon.entity.DungeonMobs;
import mike.dungeons.dungeon.entity.component.MovementComponent;
import mike.dungeons.dungeon.entity.component.TargetComponent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

@Component
public class MovementSystem extends AbstractTask {

    public MovementSystem() {
        super(Dungeons.getInst(), 0, 1, false);
    }

    @Override
    public void run() {
        for(DungeonEntity dungeonEntity : DungeonMobs.getComponentEntities(MovementComponent.class)) {
            if(dungeonEntity.getEntity() == null) continue;
            if(!dungeonEntity.hasComponent(TargetComponent.class)) continue;
            final MovementComponent movementComponent = dungeonEntity.getComponent(MovementComponent.class);
            final TargetComponent targetComponent = dungeonEntity.getComponent(TargetComponent.class);
            final Entity targetEntity = targetComponent.getTarget();
            final Location targetLocation = targetComponent.getLocationTarget();
            final LivingEntity le = dungeonEntity.getEntity();
            if(targetEntity == null && targetLocation != null) {
                movementComponent.move(le, targetLocation);
                le.lookAt(Position.fine(targetLocation), LookAnchor.EYES);
            }else {
                if (targetEntity != null) {
                    movementComponent.move(le, targetEntity.getLocation());
                    le.lookAt(Position.fine(targetEntity.getLocation().clone().add(0, 1.5, 0)), LookAnchor.EYES);
                }
            }
        }
    }
}
