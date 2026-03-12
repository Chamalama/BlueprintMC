package mike.blueprint.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class Json {

    private static final Gson JSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();

    public static <V> void write(Plugin plugin, String folder, String file, V object, boolean update) {
        final File writing = new File(plugin.getDataFolder(), folder + "/" + file + ".json");
        if(writing.exists() && !update) return;
        if(!writing.exists()) {
            try {
                if(!writing.getParentFile().exists()) {
                    writing.getParentFile().mkdirs();
                }
                writing.createNewFile();
            }catch (IOException e) {
                throw new RuntimeException("Could not create new file!");
            }
        }
        try(FileWriter writer = new FileWriter(writing)) {
            writer.write(JSON.toJson(object));
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <V> V get(Plugin plugin, String folder, String file, Class<V> clazz) {
        final File reading = new File(plugin.getDataFolder(), folder + "/" + file + ".json");
        if(!reading.exists()) return null;
        try(FileReader reader = new FileReader(reading)) {
            return JSON.fromJson(reader, clazz);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
