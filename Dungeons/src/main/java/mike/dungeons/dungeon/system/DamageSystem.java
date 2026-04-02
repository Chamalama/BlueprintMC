package mike.dungeons.dungeon.system;

import com.google.common.collect.HashBiMap;
import lombok.Getter;
import mike.blueprint.loader.Component;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.service.DungeonTeamService;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

@Component
@Getter
public class DamageSystem {

    private final HashBiMap<UUID, UUID> damageEntries = HashBiMap.create();

    private final DungeonTeamService dungeonTeamService;

    public DamageSystem(DungeonTeamService dungeonTeamService) {
        this.dungeonTeamService = dungeonTeamService;
    }

    public void addDamageEntry(LivingEntity damager, LivingEntity damaged) {
        damageEntries.put(damager.getUniqueId(), damaged.getUniqueId());
    }

    public UUID getDamager(LivingEntity damaged) {
        return damageEntries.inverse().get(damaged.getUniqueId());
    }

    public DungeonTeam getDamagerTeam(LivingEntity damaged) {
        if(getDamager(damaged) == null) return null;
        return dungeonTeamService.getTeam(getDamager(damaged));
    }

    public void clearEntry(LivingEntity damaged) {
        final DungeonTeam team = getDamagerTeam(damaged);
        if(team != null) {
            team.getEncounterData().getActiveMobs().remove(damaged.getUniqueId());
        }
        final UUID killer = getDamager(damaged);
        damageEntries.remove(killer);
    }

}
