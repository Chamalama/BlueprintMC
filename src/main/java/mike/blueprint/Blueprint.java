package mike.blueprint;

import lombok.Getter;
import mike.blueprint.loader.Loader;
import mike.blueprint.util.AbstractTask;
import org.bukkit.plugin.java.JavaPlugin;

public final class Blueprint extends JavaPlugin {

    @Getter
    public static Blueprint inst;

    @Override
    public void onEnable() {
        inst = this;
        Loader.load(this);
    }

    @Override
    public void onDisable() {
        Loader.getLoaded().values().forEach(o -> {
            if (o instanceof AbstractTask task) {
                task.cancel();
            }
        });
    }
}
