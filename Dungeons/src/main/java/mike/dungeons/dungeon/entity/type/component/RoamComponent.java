package mike.dungeons.dungeon.entity.type.component;

import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.math.Position;
import lombok.Getter;
import lombok.Setter;
import mike.blueprint.util.MovementUtil;
import mike.dungeons.dungeon.entity.DungeonEntity;
import mike.dungeons.dungeon.entity.component.AIState;
import mike.dungeons.dungeon.entity.component.GenericAIComponent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

@Getter
@Setter
public class RoamComponent implements AIState {

    private final double roamSpeed;
    private Location pathLocation;
    private boolean pathing, canRoam;

    public RoamComponent(double roamSpeed) {
        this.roamSpeed = roamSpeed;
        this.pathLocation = null;
        this.pathing = false;
        this.canRoam = false;
    }

    @Override
    public void tick(DungeonEntity entity, GenericAIComponent component) {
        if(pathLocation != null) {
            final LivingEntity le = entity.getEntity();
            MovementUtil.moveTo(le, pathLocation, 6, 0.4);
            le.lookAt(Position.fine(pathLocation), LookAnchor.EYES);
            pathing = true;
        }
    }

    @Override
    public void start(DungeonEntity entity) {

    }

    @Override
    public void stop(DungeonEntity entity) {
        this.pathing = false;
        this.canRoam = false;
    }

    @Override
    public void reset(DungeonEntity entity) {
        this.pathing = false;
        this.pathLocation = null;
    }

}
