package mike.blueprint.gui;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
public abstract class AbstractGUI implements InventoryHolder {

    private final Inventory inventory;
    private int currentPage;
    protected boolean paged;

    public AbstractGUI(int size, String title) {
        this.inventory = Bukkit.createInventory(this, size, Text.translate(title));
        this.currentPage = 0;
        this.paged = false;
    }

    public void addItems(List<ItemStack> items) {
        for(int i = 0; i < items.size(); i++) {
            this.inventory.setItem(i, items.get(i));
        }
    }

    public void addPagedItems(List<ItemStack> items, int page, int startPageIndex, int pageSize) {
        if(!paged) page = 0;
        final int startIndex = page * pageSize;
        final List<ItemStack> pageItems = items.subList(startIndex, startIndex + pageSize);
        for(ItemStack stack : pageItems) {
            this.inventory.setItem(startPageIndex, stack.clone());
            startPageIndex++;
        }
    }

    public AbstractGUI populateFiller(Material filler) {
        final ItemStack fillerItem = new ItemStack(filler);
        for(int i = 0; i < inventory.getSize(); i++) {
            final ItemStack stack = inventory.getItem(i);
            if(stack != null) continue;
            inventory.setItem(i, fillerItem);
        }
        return this;
    }

}
