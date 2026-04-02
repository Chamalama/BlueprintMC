package mike.dungeons.dungeon.gui;

import mike.blueprint.gui.BaseGUI;
import mike.dungeons.Dungeons;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ViewGUI extends BaseGUI {

    private final Player player;

    private static final ItemStack EMPTY_SLOT = new ItemStack(Material.AIR);

    public ViewGUI(Player player) {
        super(Dungeons.getInst(), 54, player.getName() + "'s Inventory");
        this.player = player;
        create();
    }

    private void create() {
        for(int i = 0; i < player.getInventory().getSize(); i++) {
            final ItemStack slotItem = player.getInventory().getItem(i);
            if(slotItem == null) {
                this.inventory.setItem(i, EMPTY_SLOT);
                continue;
            }
            this.inventory.setItem(i, slotItem.clone());
        }
    }

}
