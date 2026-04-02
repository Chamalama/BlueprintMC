package mike.dungeons.listener;

import mike.blueprint.loader.Component;
import mike.dungeons.dungeon.gui.TeamGUI;
import mike.dungeons.dungeon.gui.ViewGUI;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

@Component
public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getInventory().getHolder(false) instanceof ViewGUI) {
            event.setCancelled(true);
            return;
        }
        if(event.getInventory().getHolder(false) instanceof TeamGUI) {
            final Player clicking = (Player) event.getWhoClicked();
            event.setCancelled(true);
            final ItemStack clicked = event.getCurrentItem();
            if(clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;
            final SkullMeta skullMeta = (SkullMeta) clicked.getItemMeta();
            final OfflinePlayer player = skullMeta.getOwningPlayer();
            if(player != null && player.isOnline()) {
                final Player online = player.getPlayer();
                if(online == null) return;
                clicking.openInventory(new ViewGUI(online).getInventory());
                clicking.playSound(clicking, Sound.ENTITY_BAT_TAKEOFF, 0.8F, 0.8F);
            }
        }
    }

}
