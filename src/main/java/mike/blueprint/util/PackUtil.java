package mike.blueprint.util;

import mike.blueprint.Blueprint;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackInfoLike;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class PackUtil {

    public static void generatePack(Plugin plugin, String packName, String description, String namespace) {
        Bukkit.getScheduler().runTaskAsynchronously(Blueprint.getInst(), () -> {
            final File dataFolder = plugin.getDataFolder();
            final File packRoot = new File(dataFolder, packName);
            if(!packRoot.exists()) {
                packRoot.mkdirs();
            }
            final File mcmeta = new File(dataFolder, "pack.mcmeta");
            try{
                mcmeta.createNewFile();
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
            try(FileWriter writer = new FileWriter(mcmeta)) {
                writer.write(
                        "{\n" +
                        "\t\"pack\": {\n" +
                        "\t\t\"description\": " + description + ",\n" +
                        "\t\t\"pack_format\": 84,\n" +
                        "\t\t\"supported_formats\": 84,\n" +
                        "\t\t\"min_format\": 80,\n" +
                        "\t\t\"max_format\": 84\n" +
                        "\t}\n" +
                        "}");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            final File texturesRoot = new File(packRoot, "assets/" + namespace + "/textures");
            if(!texturesRoot.exists()) {
                texturesRoot.mkdirs();
            }
        });
    }

    public static void sendPack(Player player, String url) throws URISyntaxException, ExecutionException, InterruptedException {
        player.sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                .packs(ResourcePackInfo.resourcePackInfo()
                        .uri(new URI(url))
                        .computeHashAndBuild().get()));
    }

}
