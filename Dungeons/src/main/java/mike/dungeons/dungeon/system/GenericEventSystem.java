package mike.dungeons.dungeon.system;

import mike.blueprint.loader.Component;
import mike.blueprint.util.AbstractTask;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.service.DungeonTeamService;

@Component
public class GenericEventSystem extends AbstractTask {

    private final DungeonTeamService dungeonTeamService;

    public GenericEventSystem(DungeonTeamService dungeonTeamService) {
        super(Dungeons.getInst(), 5, 3, false);
        this.dungeonTeamService = dungeonTeamService;
    }

    @Override
    public void run() {
        if(dungeonTeamService.getTeams().isEmpty()) return;
        for(DungeonTeam team : dungeonTeamService.getTeams()) {
            if(team.getDungeon() == null || team.getEncounterData().getCurrentEncounter() == null || !team.getEncounterData().isEncounterStarted()) continue;
            if(team.getEncounterData().getEncounterEvents() == null) continue;
            team.getEncounterData().getEncounterEvents().tick(team);
        }
    }
}
