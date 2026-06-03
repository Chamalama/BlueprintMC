package mike.blueprint.config;

import mike.blueprint.Blueprint;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.security.auth.callback.Callback;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
            final V loaded = (V) Json.get(plugin, folderName, fileName, this.getClass());
            if (loaded != null) {
                plugin.getLogger().info("Loading config... " + fileName);
                Class<?> clazz = this.getClass();
                while(clazz != null && clazz != Object.class) {
                    for (Field field : clazz.getDeclaredFields()) {
                        if (Modifier.isTransient(field.getModifiers())) continue;
                        field.setAccessible(true);
                        try {
                            final Object val = field.get(loaded);
                            field.set(this, val);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Json.write(plugin, folderName, fileName, this, true);
                    clazz = clazz.getSuperclass();
                }
            } else {
                plugin.getLogger().info("Creating new config for... " + fileName);
                Json.write(plugin, folderName, fileName, this, false);
            }
            Bukkit.getScheduler().runTask(Blueprint.getInst(), this::init);
        });
    }

    public void update() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> Json.write(plugin, folderName, fileName, this, true));
    }

    public void init() {

    }



}
