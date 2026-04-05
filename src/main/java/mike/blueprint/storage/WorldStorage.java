package mike.blueprint.storage;

import lombok.Getter;
import mike.blueprint.Blueprint;
import mike.blueprint.config.Config;
import mike.blueprint.loader.Component;
import mike.blueprint.util.WorldUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class WorldStorage extends Config {

    private final List<String> worldNames = new ArrayList<>();

    public WorldStorage() {
        super(Blueprint.getInst(), "worlds", "stored-worlds");
    }

    @Override
    public void init() {
        Blueprint.getInst().getLogger().info("Loading worlds...");
        for(String world : worldNames) {
            WorldUtil.loadWorld(world);
        }
    }
}
