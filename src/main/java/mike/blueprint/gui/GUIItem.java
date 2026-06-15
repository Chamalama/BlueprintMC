package mike.blueprint.gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import lombok.Getter;
import lombok.Setter;
import mike.blueprint.Blueprint;
import mike.blueprint.util.Text;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
@Setter
public class GUIItem implements Serializable {

    public static transient NamespacedKey ITEM_KEY = new NamespacedKey(Blueprint.getInst(), "ITEM_KEY");

    private Material itemType = Material.PAPER;
    private String name = "", key = "KEY";
    private List<String> lore = new ArrayList<>();
    private int slot = 1;
    private transient Consumer<Player> clickAction;

    public ItemStack build() {
        final ItemStack stack = new ItemStack(itemType);
        stack.setData(DataComponentTypes.CUSTOM_NAME, Text.translate(name));
        stack.setData(DataComponentTypes.LORE, ItemLore.lore(Text.translate(lore)));
        final ItemMeta meta = stack.getItemMeta();
        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ITEM_KEY, PersistentDataType.STRING, key);
        stack.setItemMeta(meta);
        return stack;
    }

    public static class Builder {

        private final GUIItem guiItem = new GUIItem();

        public Builder setType(Material type) {
            this.guiItem.setItemType(type);
            return this;
        }

        public Builder setName(String name) {
            this.guiItem.setName(name);
            return this;
        }

        public Builder setKey(String key) {
            this.guiItem.setKey(key);
            return this;
        }

        public Builder setLore(List<String> lore) {
            this.guiItem.setLore(lore);
            return this;
        }

        public Builder setSlot(int slot) {
            this.guiItem.setSlot(slot);
            return this;
        }

        public Builder setClickAction(Consumer<Player> action) {
            this.guiItem.setClickAction(action);
            return this;
        }

        public GUIItem build() {
            return guiItem;
        }

    }

}
