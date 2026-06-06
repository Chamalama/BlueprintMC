package mike.blueprint.gui;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIHelper {

    private static final Map<UUID, BaseGUI> storedGUI = new HashMap<>();
    private static final Map<UUID, Integer> playerPages = new HashMap<>();

    public static void initGUI(Player player, BaseGUI baseGUI) {
        storedGUI.put(player.getUniqueId(), baseGUI);
        playerPages.putIfAbsent(player.getUniqueId(), 0);
    }

    public static void updatePage(Player player, int page) {
        playerPages.put(player.getUniqueId(), page);
    }

    public static void clearPlayer(Player player, boolean clearPage) {
        if(clearPage) {
            playerPages.remove(player.getUniqueId());
        }
        storedGUI.remove(player.getUniqueId());
    }

    public static int getCurrentPage(Player player) {
        return playerPages.getOrDefault(player.getUniqueId(), 0);
    }

    public static BaseGUI getStoredGUI(Player player) {
        return storedGUI.get(player.getUniqueId());
    }

}
