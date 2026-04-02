package mike.dungeons.dungeon.entity.type;

import mike.dungeons.dungeon.entity.DungeonEntity;
import mike.dungeons.dungeon.DungeonKeys;
import mike.dungeons.dungeon.entity.component.*;
import mike.dungeons.dungeon.entity.type.component.RoamComponent;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import java.util.Set;

public class GateGuard extends DungeonEntity {

    public GateGuard() {
        super("Gate Guard", EntityType.COPPER_GOLEM);
        this.addComponent(new HealthComponent(100))
                .addComponent(new TargetComponent(19, 2000, Set.of(DungeonKeys.DUNGEON_MOB)))
                .addComponent(new TagComponent(DungeonKeys.DUNGEON_MOB))
                .addComponent(new MovementComponent(0.2, 5.5))
                .addComponent(new CombatComponent(3.0, 1.0, 0.05, 1.2))
                .addComponent(new ScaleComponent(1.25))
                .addComponent(new AttackComponent(1500, 5.5, Sound.BLOCK_CONDUIT_ATTACK_TARGET))
                .addComponent(new GenericAIComponent(new RoamComponent(0.3)));

    }

}
