package mike.blueprint.util;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import mike.blueprint.event.GiveItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.function.Consumer;

public class ItemUtil {

    public static ItemStack build(Material material, String name, List<String> lore) {
        final ItemStack stack = new ItemStack(material);
        stack.setData(DataComponentTypes.CUSTOM_NAME, Text.translate(name));
        stack.setData(DataComponentTypes.LORE, ItemLore.lore(Text.translate(lore)));
        return stack;
    }

    public static void giveItem(Player player, ItemStack stack) {
        Bukkit.getPluginManager().callEvent(new GiveItemEvent(player, stack));
    }

    public static class ItemBuilder {

        private final ItemStack stack;

        public ItemBuilder(Material material) {
            this.stack = new ItemStack(material);
        }

        public ItemBuilder setName(String name) {
            this.stack.setData(DataComponentTypes.CUSTOM_NAME, Text.translate(name));
            return this;
        }

        public ItemBuilder setLore(List<String> lore) {
            this.stack.setData(DataComponentTypes.LORE, ItemLore.lore(Text.translate(lore)));
            return this;
        }

        public ItemBuilder setData(Consumer<PersistentDataContainer> consumer) {
            this.stack.editPersistentDataContainer(consumer);
            return this;
        }

        public <T> ItemBuilder setItemData(DataComponentType.Valued<T> dataComponentTypes, T data) {
            this.stack.setData(dataComponentTypes, data);
            return this;
        }

        public ItemStack get() {
            return stack;
        }

    }

}
