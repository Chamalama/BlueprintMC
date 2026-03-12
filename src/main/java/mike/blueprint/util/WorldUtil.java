package mike.blueprint.util;

import mike.blueprint.world.EmptyWorld;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WorldUtil {

    public static Map<Integer, File> cachedWorld = new HashMap<>();

    public static void createEmptyWorld(String name) {
        final WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.generator(new EmptyWorld());
        Bukkit.createWorld(worldCreator);
    }

    public static void createCopy(String worldName) {
        final File file = Bukkit.getWorldContainer();
        final File[] worlds = file.listFiles();

    }

}
