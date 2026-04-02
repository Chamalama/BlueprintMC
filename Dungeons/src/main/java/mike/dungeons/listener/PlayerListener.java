package mike.dungeons.listener;

import mike.blueprint.loader.Component;
import mike.dungeons.dungeon.entity.DungeonEntity;
import mike.dungeons.dungeon.entity.DungeonMobs;
import mike.dungeons.dungeon.entity.component.DeathComponent;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.service.DungeonTeamService;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

@Component
public class PlayerListener implements Listener {

    private final DungeonTeamService dungeonTeamService;

    public PlayerListener(DungeonTeamService dungeonTeamService) {
        this.dungeonTeamService = dungeonTeamService;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getPlayer();
        final DungeonTeam dungeonTeam = dungeonTeamService.getPlayersTeam(player);
        if(dungeonTeam == null) return;
        event.setKeepLevel(true);
        event.setKeepInventory(true);
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0);
        event.getDrops().clear();
        DungeonMobs.unregister(event.getEntity());
    }

}
