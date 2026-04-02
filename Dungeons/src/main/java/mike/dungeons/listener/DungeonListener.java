package mike.dungeons.listener;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import mike.blueprint.loader.Component;
import mike.blueprint.util.FastLocation;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.Dungeon;
import mike.dungeons.dungeon.DungeonRegion;
import mike.dungeons.dungeon.DungeonRoom;
import mike.dungeons.dungeon.TriggerPoint;
import mike.dungeons.dungeon.component.GenericEventComponent;
import mike.dungeons.dungeon.entity.DungeonEntity;
import mike.dungeons.dungeon.entity.DungeonMobs;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.dungeon.team.EncounterData;
import mike.dungeons.service.DungeonService;
import mike.dungeons.service.DungeonTeamService;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

@Component
public class DungeonListener implements Listener {

    private final DungeonTeamService dungeonTeamService;
    private final DungeonService dungeonService;

    public DungeonListener(DungeonTeamService dungeonTeamService, DungeonService dungeonService) {
        this.dungeonTeamService = dungeonTeamService;
        this.dungeonService = dungeonService;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final DungeonTeam team = dungeonTeamService.getPlayersTeam(player);
        if(team == null) return;
        final Dungeon currentDungeon = team.getDungeon();
        if(currentDungeon == null) return;
        final EncounterData encounterData = team.getEncounterData();
        final DungeonRoom currentRoom = encounterData.getCurrentEncounter();
        final int clearedRooms = encounterData.getEncountersCleared();
        if(currentRoom == null) {
            final DungeonRoom dungeonRoom = currentDungeon.getDungeonRooms().get(clearedRooms);
            final DungeonRegion dungeonRegion = dungeonRoom.getRoomRegion();
            if (dungeonRegion == null || dungeonRoom.getStartPoint() == null) return;
            if (dungeonRegion.contains(player.getLocation())) {
                encounterData.setCurrentEncounter(dungeonRoom);
                dungeonRoom.copyState(team);
                Dungeons.getInst().log("Setting current encounter for team " + team.getName() + " to " + dungeonRoom.getRoomID());
            }
        }else{
            final TriggerPoint triggerPoint = encounterData.getEncounterPoint();
            final Location playerLocation = player.getLocation();
            final Location triggerLocation = triggerPoint.getTriggerLocation().toBukkit();
            final int triggerID = triggerPoint.getTriggerID();
            final boolean isInRange = playerLocation.distanceSquared(triggerLocation) <= triggerPoint.getTriggerRadius();
            final boolean isActivated = encounterData.getActivatedTriggers().contains(triggerID);
            if(!isActivated && isInRange) {
                encounterData.getCurrentEncounter().startEncounter(team);
                encounterData.getActivatedTriggers().add(triggerID);
                final GenericEventComponent genericEventComponent = encounterData.getEncounterEvents();
                if(genericEventComponent.getCurrentEvent().startOnEnter()) {
                    genericEventComponent.startEvent(team);
                }
            }
        }
    }

    @EventHandler
    public void onRemove(EntityRemoveFromWorldEvent event) {
        final Entity entity = event.getEntity();
        final UUID id = entity.getUniqueId();
        if(DungeonMobs.getEntity(id) != null) {
            DungeonMobs.unregister(entity);
        }
    }

}
