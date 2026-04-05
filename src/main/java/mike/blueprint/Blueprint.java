package mike.blueprint;

import lombok.Getter;
import mike.blueprint.loader.Loader;
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
        // Plugin shutdown logic
    }
}
