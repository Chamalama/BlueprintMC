package mike.blueprint.util;

import mike.blueprint.event.GiveItemEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.function.Consumer;

public class ItemUtil {

    public static ItemStack build(Material material, String name, List<String> lore) {
        final ItemStack stack = new ItemStack(material);
        final ItemMeta meta = stack.getItemMeta();
        meta.displayName(Text.translate(name));
        meta.lore(Text.translate(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    public static void giveItem(Player player, ItemStack stack) {
        Bukkit.getPluginManager().callEvent(new GiveItemEvent(player, stack));
    }

    public static class ItemBuilder {

        private final ItemStack stack;
        private ItemMeta meta;

        public ItemBuilder(Material material) {
            this.stack = new ItemStack(material);
            this.meta = stack.getItemMeta();
        }

        public ItemBuilder setName(String name) {
            this.meta.displayName(Text.translate(name));
            return this;
        }

        public ItemBuilder setLore(List<String> lore) {
            this.meta.lore(Text.translate(lore));
            return this;
        }

        public ItemBuilder setData(Consumer<PersistentDataContainer> consumer) {
            final PersistentDataContainer pdc = this.meta.getPersistentDataContainer();
            consumer.accept(pdc);
            return this;
        }

        public ItemStack get() {
            return stack;
        }

    }

}
