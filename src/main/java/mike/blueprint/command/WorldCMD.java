package mike.blueprint.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import mike.blueprint.loader.Component;
import mike.blueprint.storage.WorldStorage;
import mike.blueprint.util.Text;
import mike.blueprint.util.WorldUtil;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Component
@CommandAlias("blueprintworld|bpworld")
@CommandPermission("admin.bp.cmd")
public class WorldCMD extends BaseCommand {

    private final WorldStorage worldStorage;
    private final PaperCommandManager paperCommandManager;

    //private final Registry<GameRule<?>> gameRuleRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.GAME_RULE);

    public WorldCMD(WorldStorage worldStorage, PaperCommandManager paperCommandManager) {
        this.worldStorage = worldStorage;
        this.paperCommandManager = paperCommandManager;
        paperCommandManager.getCommandCompletions().registerAsyncCompletion("worlds", c -> worldStorage.getWorldNames());
        paperCommandManager.getCommandCompletions().registerAsyncCompletion("env", c -> Arrays.stream(World.Environment.values()).map(Enum::name).toList());
    }

    @Default
    public void onDefault(Player player) {
        player.sendMessage(Text.translate("<gold><b>Worlds"));
        for(World world : Bukkit.getWorlds()) {
            player.sendMessage(" - " + world.getName());
        }
    }

    @Subcommand("create")
    @CommandCompletion("name @env")
    public void onCreate(CommandSender sender, String worldName, String environment) {
        WorldUtil.createEmptyWorld(worldName, environment, world -> {
            /*
            final GameRule<Boolean> rule = (GameRule<Boolean>) gameRuleRegistry.get(RegistryKey.GAME_RULE.typedKey(Key.key("minecraft:spawn_mobs")));
            if(rule != null) {
                world.setGameRule(rule, false);
            }
             */
            sender.sendMessage(world.getName() + " has generated successfully!");
            world.setSpawnLocation(0, 70, 0);
            worldStorage.getWorldNames().add(worldName);
            worldStorage.update();
        });
    }

    @Subcommand("copy")
    @CommandCompletion("@worlds name")
    public void onCopy(CommandSender sender, String worldName, String newWorldName) {
        WorldUtil.fastCopy(worldName, newWorldName, world -> {
            /*
            sender.sendMessage(newWorldName + " has been generated!");
            final GameRule<Boolean> rule = (GameRule<Boolean>) gameRuleRegistry.get(RegistryKey.GAME_RULE.typedKey(Key.key("minecraft:spawn_mobs")));
            if(rule != null) {
                world.setGameRule(rule, false);
            }
             */
            worldStorage.getWorldNames().add(newWorldName);
            worldStorage.update();
        });
    }

    @Subcommand("delete")
    @CommandCompletion("@worlds")
    public void onDelete(CommandSender sender, String worldName) {
        final World world = Bukkit.getWorld(worldName);
        if(world == null) {
            sender.sendMessage(Text.translate("<red>Invalid world..."));
            return;
        }
        worldStorage.getWorldNames().remove(worldName);
        worldStorage.update();
        WorldUtil.unloadWorld(world);
        sender.sendMessage(Text.translate("<red>Deleted " + worldName + " successfully!"));
    }

    @Subcommand("go")
    @CommandCompletion("@worlds")
    public void onGo(Player sender, String world) {
        final World toTeleport = Bukkit.getWorld(world);
        if(toTeleport == null) {
            sender.sendMessage(Text.translate("<red>Invalid world..."));
            return;
        }
        sender.teleportAsync(toTeleport.getSpawnLocation());
    }

}
