package mike.dungeons.dungeon.system;

import mike.blueprint.loader.Component;
import mike.blueprint.util.AbstractTask;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.component.SpawnComponent;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.service.DungeonTeamService;

@Component
public class SpawnSystem extends AbstractTask {

    private final DungeonTeamService dungeonTeamService;

    public SpawnSystem(DungeonTeamService dungeonTeamService) {
        super(Dungeons.getInst(), 2, 3, false);
        this.dungeonTeamService = dungeonTeamService;
    }

    @Override
    public void run() {
        for(DungeonTeam dungeonTeam : dungeonTeamService.getTeams()) {
            if(dungeonTeam.getDungeon() == null || dungeonTeam.getEncounterData().getCurrentEncounter() == null || !dungeonTeam.getEncounterData().isEncounterStarted()) continue;
            final SpawnComponent spawnComponent = dungeonTeam.getEncounterData().getCurrentEncounter().getComponent(SpawnComponent.class);
            if(spawnComponent == null) continue;
            spawnComponent.tick();
        }
    }
}
