package mike.dungeons.dungeon.system;

import mike.blueprint.loader.Component;
import mike.blueprint.util.AbstractTask;
import mike.blueprint.util.ParticleUtil;
import mike.blueprint.util.Text;
import mike.blueprint.util.TitleUtil;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.dungeon.team.EncounterData;
import mike.dungeons.service.DungeonService;
import mike.dungeons.service.DungeonTeamService;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

@Component
public class TimerSystem extends AbstractTask {

    private final DungeonTeamService dungeonTeamService;
    private final DungeonService dungeonService;

    public TimerSystem(DungeonTeamService dungeonTeamService, DungeonService dungeonService) {
        super(Dungeons.getInst(), 0, 20, true);
        this.dungeonTeamService = dungeonTeamService;
        this.dungeonService = dungeonService;
    }

    @Override
    public void run() {
        for(DungeonTeam dungeonTeam : dungeonTeamService.getDungeonTeams().values()) {
            final EncounterData encounterData = dungeonTeam.getEncounterData();
            if(encounterData == null || !encounterData.isEncounterStarted()) continue;
            encounterData.setEncounterTime(encounterData.getEncounterTime() - 1);
            final List<Player> players = dungeonTeam.getPlayers(true);
            if(encounterData.getEncounterTime() <= 0) {
                for(Player player : players) {
                    TitleUtil.sendTitle(player, "<red><b>Failure", "", 1, 1, 1);
                    player.playSound(player, Sound.ENTITY_WITHER_DEATH, 0.7F, 0.7F);
                }
                Bukkit.getScheduler().runTask(Dungeons.getInst(), () ->  dungeonService.resetEncounter(dungeonTeam));

            }
            for(Player player : players) {
                //ParticleUtil.circle(player.getLocation().toCenterLocation(), Particle.FLAME, 2.0, 0.75,  10);
                player.sendActionBar(Text.translate("<yellow>" + format(encounterData.getEncounterTime())));
            }
        }
    }

    private String format(long time) {
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        if (minutes > 0) {
            return String.format("%dm, %ds", minutes, seconds);
        }else{
            return String.format("%ds", seconds);
        }
    }
}
