package mike.dungeons.dungeon.entity.system;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import mike.blueprint.loader.Component;
import mike.blueprint.util.AbstractTask;
import mike.blueprint.util.FastLocation;
import mike.blueprint.util.TargetUtil;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.entity.DungeonEntity;
import mike.dungeons.dungeon.entity.DungeonMobs;
import mike.dungeons.dungeon.entity.component.TargetComponent;
import mike.dungeons.dungeon.entity.type.component.RoamComponent;
import mike.dungeons.dungeon.team.DungeonTeam;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;

import java.util.List;

@Component
public class TargetSystem extends AbstractTask {

    public TargetSystem() {
        super(Dungeons.getInst(), 0, 5, false);
    }

    @Override
    public void run() {
        for(DungeonEntity dungeonEntity : DungeonMobs.getComponentEntities(TargetComponent.class)) {
            final TargetComponent targetComponent = dungeonEntity.getComponent(TargetComponent.class);
            if(!targetComponent.shouldRetarget()) continue;
            final LivingEntity dungeonEnt = dungeonEntity.getEntity();
            final LivingEntity target = TargetUtil.findClosestEntity(dungeonEnt, targetComponent.getTargetRadius(), targetComponent.getExcludedTargets().toArray(new NamespacedKey[0]));
            if(dungeonEntity.hasComponent(RoamComponent.class) && target == null) {
                final DungeonTeam entityTeam = DungeonMobs.getEntitiesTeam(dungeonEntity);
                if(entityTeam != null) {
                    final List<FastLocation> possibleRoamPoints = entityTeam.getEncounterData().getRoamPoints();

                }
            }
            targetComponent.target(target);
        }
    }
}
