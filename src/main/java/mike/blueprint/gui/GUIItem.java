package mike.blueprint.gui;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.Blueprint;
import mike.blueprint.util.Text;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GUIItem implements Serializable {

    public static transient NamespacedKey ITEM_KEY = new NamespacedKey(Blueprint.getInst(), "ITEM_KEY");

    private Material itemType = Material.PAPER;
    private String name = "", key = "KEY";
    private List<String> lore = new ArrayList<>();
    private int slot = 1;

    public ItemStack build() {
        final ItemStack stack = new ItemStack(itemType);
        final ItemMeta meta = stack.getItemMeta();
        meta.displayName(Text.translate(name));
        meta.lore(Text.translate(lore));
        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ITEM_KEY, PersistentDataType.STRING, key);
        stack.setItemMeta(meta);
        return stack;
    }

}
