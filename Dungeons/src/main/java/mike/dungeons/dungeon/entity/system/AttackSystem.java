package mike.dungeons.dungeon.entity.system;

import mike.blueprint.loader.Component;
import mike.blueprint.util.AbstractTask;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.entity.DungeonEntity;
import mike.dungeons.dungeon.entity.DungeonMobs;
import mike.dungeons.dungeon.entity.component.AttackComponent;
import mike.dungeons.dungeon.entity.component.CombatComponent;
import mike.dungeons.dungeon.entity.component.HealthComponent;
import mike.dungeons.dungeon.entity.component.TargetComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

@Component
public class AttackSystem extends AbstractTask {

    public AttackSystem() {
        super(Dungeons.getInst(), 0, 5, false);
    }

    @Override
    public void run() {
        for(DungeonEntity dungeonEntity : DungeonMobs.getComponentEntities(AttackComponent.class)) {
            if(!dungeonEntity.hasComponent(TargetComponent.class) || dungeonEntity.getEntity() == null) continue;
            final TargetComponent targetComponent = dungeonEntity.getComponent(TargetComponent.class);
            if(targetComponent.getTarget() == null) continue;
            final AttackComponent attackComponent = dungeonEntity.getComponent(AttackComponent.class);
            if(!attackComponent.canAttack()) continue;
            final Entity target = targetComponent.getTarget();
            if(target instanceof LivingEntity le) {
                attackComponent.attack(dungeonEntity, le);
            }else{
                final DungeonEntity entity = DungeonMobs.getEntity(target.getUniqueId());
                if(entity != null) {
                    if(entity.hasComponent(HealthComponent.class)) {
                        final HealthComponent healthComponent = entity.getComponent(HealthComponent.class);
                        if(!dungeonEntity.hasComponent(CombatComponent.class)) continue;
                        final CombatComponent combatComponent = dungeonEntity.getComponent(CombatComponent.class);
                        healthComponent.damage(combatComponent.getDamage());
                    }
                }
            }
        }
    }
}
