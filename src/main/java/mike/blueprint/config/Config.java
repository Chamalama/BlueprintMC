package mike.blueprint.config;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class Config {

    private final transient Plugin plugin;
    private final transient String folderName, fileName;

    public Config(Plugin plugin, String folderName, String fileName) {
        this.plugin = plugin;
        this.folderName = folderName;
        this.fileName = fileName;
    }

    public <V> void load() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getLogger().info("Loading config... " + fileName);
            V loaded = (V) Json.get(plugin, folderName, fileName, this.getClass());
            if(loaded != null) {
                for(Field field : this.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        field.set(this, field.get(loaded));
                    }catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }else{
                plugin.getLogger().info("Creating new config for... " + fileName);
                Json.write(plugin, folderName, fileName, this, false);
            }
        });
    }

    public void update() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> Json.write(plugin, folderName, fileName, this, true));
    }

    public void init() {

    }



}
