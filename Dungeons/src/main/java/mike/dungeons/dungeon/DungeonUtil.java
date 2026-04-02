package mike.dungeons.dungeon;

import mike.blueprint.util.Text;
import org.bukkit.entity.Player;

public class DungeonUtil {

    public static final String MESSAGE_PREFIX = "<gold><b>[DUNGEONS]</b> ";

    public static void sendDungeonMessage(Player player, String message) {
        player.sendMessage(Text.translate(MESSAGE_PREFIX + "<yellow>" + message));
    }

}
