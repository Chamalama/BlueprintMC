package mike.blueprint.gui;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.config.Config;
import mike.blueprint.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

@Getter
@Setter
public abstract class BaseGUI extends Config implements InventoryHolder, Listener {

    private transient final Plugin plugin;
    protected transient Inventory inventory;
    protected transient boolean paged;

    public BaseGUI(Plugin plugin, String title) {
        super(plugin, "gui", title);
        this.plugin = plugin;
        this.inventory = null;
    }

    public BaseGUI(Plugin plugin, int size, String title) {
        super(plugin, "gui", title);
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, size, Text.translate(title));
        this.paged = false;
    }

    public void create(GUI gui) {
        this.inventory = Bukkit.createInventory(this, gui.getPageSize(), gui.getTitle());
        populate(gui, 0);
    }

    public void populate(GUI gui, int page) {
        if(!paged) {
            page = 0;
        }
        if(gui != null) {
            final List<GUIItem> items = gui.getPageItems().get(page);
            if (items == null || items.isEmpty()) return;
            for (GUIItem item : items) {
                this.inventory.setItem(item.getSlot(), item.build());
            }
        }
    }

    public ItemStack[] getPageContents(int page, int slots, ItemStack[] items) {
        if(page <= 0) {
            return new ItemStack[slots];
        }else{
            int itemStartIndex = slots * page;
            int endIndex = Math.min(itemStartIndex + slots, items.length);
            final ItemStack[] contents = new ItemStack[slots];
            for(int i = 0; i < slots && (itemStartIndex + i) < endIndex; i++) {
                contents[i] = items[itemStartIndex + i];
            }
            return contents;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
