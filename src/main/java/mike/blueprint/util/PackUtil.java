package mike.blueprint.util;

import mike.blueprint.Blueprint;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;

public class PackUtil {

    public static void generatePack(Plugin plugin, String packName, String namespace) {
        Bukkit.getScheduler().runTaskAsynchronously(Blueprint.getInst(), () -> {
            final File dataFolder = plugin.getDataFolder();
            final File packRoot = new File(dataFolder, packName);
            if(!packRoot.exists()) {
                packRoot.mkdirs();
            }
            final File texturesRoot = new File(packRoot, "assets/" + namespace + "/textures");
            if(!texturesRoot.exists()) {
                texturesRoot.mkdirs();
            }
        });
    }

}
