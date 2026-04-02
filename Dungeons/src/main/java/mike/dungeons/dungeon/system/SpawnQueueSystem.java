package mike.dungeons.dungeon.system;

import mike.blueprint.loader.Component;
import mike.blueprint.util.AbstractTask;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.DungeonRoom;
import mike.dungeons.dungeon.component.SpawnComponent;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.service.DungeonService;
import mike.dungeons.service.DungeonTeamService;

@Component
public class SpawnQueueSystem extends AbstractTask {

    private final DungeonService dungeonService;
    private final DungeonTeamService dungeonTeamService;

    public SpawnQueueSystem(DungeonService dungeonService, DungeonTeamService dungeonTeamService) {
        super(Dungeons.getInst(), 0, 30, false);
        this.dungeonService = dungeonService;
        this.dungeonTeamService = dungeonTeamService;
    }

    @Override
    public void run() {
        if(dungeonTeamService.getTeams().isEmpty()) return;
        for(DungeonTeam dungeonTeam : dungeonTeamService.getTeams()) {
            if(dungeonTeam.getDungeon() == null || dungeonTeam.getEncounterData().getCurrentEncounter() == null || !dungeonTeam.getEncounterData().isEncounterStarted()) continue;
            final DungeonRoom encounter = dungeonTeam.getEncounterData().getCurrentEncounter();
            if(!encounter.hasComponent(SpawnComponent.class)) continue;
            final SpawnComponent spawnComponent = encounter.getComponent(SpawnComponent.class);
            spawnComponent.spawn(dungeonTeam);
        }
    }
}
