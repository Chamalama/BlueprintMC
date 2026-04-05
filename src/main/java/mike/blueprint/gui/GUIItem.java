package mike.blueprint.gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
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

    public static transient NamespacedKey ITEM_KEY;

    private Material itemType = Material.PAPER;
    private String name = "";
    private List<String> lore = new ArrayList<>();
    private String TAG = "TAG";
    private int slot = 1;

    public ItemStack build() {
        final ItemStack stack = new ItemStack(itemType);
        stack.setData(DataComponentTypes.CUSTOM_NAME, Text.translate(name));
        stack.setData(DataComponentTypes.LORE, ItemLore.lore(Text.translate(lore)));
        final ItemMeta meta = stack.getItemMeta();
        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ITEM_KEY, PersistentDataType.STRING, TAG);
        stack.setItemMeta(meta);
        return stack;
    }

    static {
        ITEM_KEY = new NamespacedKey(Blueprint.getInst(), "ITEM_KEY");
    }

}
