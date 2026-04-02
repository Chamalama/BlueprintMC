package mike.dungeons;

import lombok.Getter;
import mike.blueprint.loader.Loader;
import mike.blueprint.util.AbstractTask;
import mike.dungeons.service.DungeonService;
import mike.dungeons.service.DungeonTeamService;
import org.bukkit.plugin.java.JavaPlugin;

public final class Dungeons extends JavaPlugin {

    @Getter
    public static Dungeons inst;

    @Override
    public void onEnable() {
        inst = this;
        Loader.load(this);
    }

    @Override
    public void onDisable() {
        Loader.loaded.values().forEach(o -> {
            if(o instanceof AbstractTask task) {
                task.cancel();
            }
        });
        Loader.get(DungeonTeamService.class).getDungeonTeams().values().forEach(dungeonTeam -> {
            Loader.get(DungeonService.class).removeTeamDungeon(dungeonTeam);
        });
    }

    public void log(String message) {
        inst.getLogger().info(message);
    }

}
