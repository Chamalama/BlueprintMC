package mike.dungeons.dungeon.gui;

import lombok.Getter;
import mike.blueprint.gui.BaseGUI;
import mike.blueprint.gui.GUI;
import mike.blueprint.loader.Component;
import mike.dungeons.Dungeons;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Getter
@Component
public class DungeonGUI extends BaseGUI {

    private final GUI DUNGEON_GUI = new GUI();

    public DungeonGUI() {
        super(Dungeons.getInst(), "dungeon_gui");
    }

    @Override
    public void init() {
        create(DUNGEON_GUI);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        final ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null) return;
        if(event.getInventory().getHolder(false) instanceof DungeonGUI) {
            event.setCancelled(true);
        }
    }

}
