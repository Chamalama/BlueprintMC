package mike.dungeons.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import mike.blueprint.loader.Component;
import mike.blueprint.util.Text;
import mike.dungeons.dungeon.DungeonRegistry;
import mike.dungeons.dungeon.DungeonRoom;
import mike.dungeons.dungeon.dungeons.outpost.Outpost;
import mike.dungeons.dungeon.gui.DungeonGUI;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.service.DungeonService;
import mike.dungeons.service.DungeonTeamService;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Component
@CommandAlias("dungeon")
public class DungeonCMD extends BaseCommand {

    private final DungeonGUI dungeonGUI;
    private final DungeonService dungeonService;
    private final DungeonTeamService dungeonTeamService;
    private final PaperCommandManager paperCommandManager;

    public DungeonCMD(DungeonGUI dungeonGUI, DungeonService dungeonService, DungeonTeamService dungeonTeamService, PaperCommandManager paperCommandManager) {
        this.dungeonGUI = dungeonGUI;
        this.dungeonService = dungeonService;
        this.dungeonTeamService = dungeonTeamService;
        this.paperCommandManager = paperCommandManager;
        this.paperCommandManager.getCommandCompletions().registerCompletion("dungeons", c -> DungeonRegistry.getRooms().keySet());
    }

    @Default
    public void openGUI(Player player) {
        player.openInventory(dungeonGUI.getInventory());
        player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.8F, 0.8F);
    }

    @Subcommand("reload")
    @CommandPermission("admin.dungeon.cmd")
    public void reload(CommandSender sender) {
        dungeonGUI.load();
        for(DungeonRoom dungeonRoom : DungeonRegistry.getRooms().values()) {
            dungeonRoom.load();
        }
        sender.sendMessage(Text.translate("<yellow><b>(!)</b> Reloading dungeon configs..."));
    }

    @Subcommand("edit")
    @CommandPermission("admin.edit.cmd")
    @CommandCompletion("@dungeons")
    public void edit(Player sender, String dungeon) {
        final DungeonRoom dungeonRoom = DungeonRegistry.getRoom(dungeon);
        dungeonService.addEditor(sender, dungeonRoom);
    }

    @Subcommand("stop-edit")
    @CommandPermission("admin.edit.cmd")
    public void stopEdit(Player sender) {
        if(dungeonService.getEditing(sender) == null) return;
        dungeonService.removeEditor(sender);
    }

    @Subcommand("test")
    @CommandPermission("admin.test.cmd")
    public void test(Player player) {
        final DungeonTeam dungeonTeam = dungeonTeamService.getPlayersTeam(player);
        if(dungeonTeam == null) return;
        dungeonService.generateTeamDungeon(dungeonTeam, DungeonRegistry.getDungeon(Outpost.class));
    }

    @Subcommand("leave")
    public void leaveDungeon(Player player) {
        final DungeonTeam dungeonTeam = dungeonTeamService.getPlayersTeam(player);
        if(dungeonTeam == null) return;
        dungeonService.removeTeamDungeon(dungeonTeam);
        dungeonTeam.setDungeon(null);
    }

}
