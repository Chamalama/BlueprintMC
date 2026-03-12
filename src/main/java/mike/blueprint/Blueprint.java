package mike.blueprint;

import org.bukkit.plugin.java.JavaPlugin;

public final class Blueprint extends JavaPlugin {

    public static Blueprint inst;

    @Override
    public void onEnable() {
        inst = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
