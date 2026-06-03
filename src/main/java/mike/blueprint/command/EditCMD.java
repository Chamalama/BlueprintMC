package mike.blueprint.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import mike.blueprint.loader.Component;
import mike.blueprint.schem.Palette;
import mike.blueprint.schem.SchemService;
import mike.blueprint.schem.SchemStorage;
import mike.blueprint.util.Text;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

@CommandAlias("bpedit")
@CommandPermission("admin.bp.cmd")
@Component
public class EditCMD extends BaseCommand {

    private final SchemStorage schemStorage;
    private final SchemService schemService;
    private final PaperCommandManager paperCommandManager;

    public EditCMD(SchemStorage schemStorage, SchemService schemService, PaperCommandManager paperCommandManager) {
        this.schemStorage = schemStorage;
        this.schemService = schemService;
        this.paperCommandManager = paperCommandManager;
        this.paperCommandManager.getCommandCompletions().registerCompletion("schems", c -> schemStorage.getVALID_SCHEMS());
    }

    @Subcommand("pos")
    @CommandCompletion("1|2")
    public void onSetPos(Player sender, int pos) {
        schemService.addPoint(sender, sender.getLocation(), pos);
    }

    @Subcommand("schem")
    @CommandCompletion("name")
    public void onCopy(Player sender, String file) {
        final Palette palette = new Palette();
        final Set<Block> blocks = schemService.getBlocksInPoints(schemService.getEditPoints(sender));
        final Location centerPoint = sender.getLocation();
        for(Block block : blocks) {
            int id = SchemService.getIDFromType(block.getType());
            palette.getIdToOffsets().putIfAbsent(id, new ArrayList<>());
            short offsetX = (short) (block.getX() - centerPoint.getBlockX());
            short offsetY = (short) (block.getY() - centerPoint.getBlockY());
            short offsetZ = (short) (block.getZ() - centerPoint.getBlockZ());
            short[] offset = new short[]{offsetX, offsetY, offsetZ};
            palette.getOffsets(id).add(offset);
        }
        schemService.writeSchemFile(file, palette);
        schemStorage.addSchem(file);
        sender.sendMessage(Text.translate("<green><b>(!)</b> Created schematic file " + file + "..."));
    }

    @Subcommand("delete")
    @CommandCompletion("@schems")
    public void onDelete(Player sender, String schem) {
        schemStorage.removeSchem(schem);
        schemService.deleteSchemFile(schem);
        sender.sendMessage(Text.translate("<yellow><b>(!)</b> Deleted schematic file " + schem + "..."));
    }

    @Subcommand("paste")
    @CommandCompletion("@schems")
    public void onPaste(Player sender, String schem) {
        schemService.pasteSchemDelayed(sender.getUniqueId(), sender.getLocation(), schem, 10L, 10L);
    }

}
