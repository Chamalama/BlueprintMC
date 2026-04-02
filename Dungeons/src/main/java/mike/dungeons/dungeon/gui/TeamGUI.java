package mike.dungeons.dungeon.gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import mike.blueprint.gui.BaseGUI;
import mike.blueprint.util.Text;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.team.DungeonTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class TeamGUI extends BaseGUI {

    private final DungeonTeam dungeonTeam;

    private static ItemStack FILLER;

    private static final List<Integer> PLAYER_SLOTS = List.of(4, 12, 13, 14);

    public TeamGUI(DungeonTeam dungeonTeam) {
        super(Dungeons.getInst(), 27, "Team");
        this.dungeonTeam = dungeonTeam;
        create();
    }

    private void create() {
        final List<Player> players = dungeonTeam.getPlayers(true);
        for(int i = 0; i < players.size(); i++) {
            final Player player = players.get(i);
            this.inventory.setItem(PLAYER_SLOTS.get(i), buildHead(player));
        }
        for(int i = 0; i < this.inventory.getSize(); i++) {
            final ItemStack stack = this.inventory.getItem(i);
            if(stack == null) {
                this.inventory.setItem(i, fillerItem());
            }
        }
    }

    private ItemStack buildHead(Player player) {
        final ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();
        skullMeta.setOwningPlayer(player);
        headItem.setItemMeta(skullMeta);
        headItem.setData(DataComponentTypes.CUSTOM_NAME, Text.translate("<yellow>" + player.getName()));
        headItem.setData(DataComponentTypes.LORE, ItemLore.lore(Text.translate(List.of("<gray>(Click to view inventory)"))));
        return headItem;
    }

    private ItemStack fillerItem() {
        if(FILLER == null) {
            FILLER = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            FILLER.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
        }
        return FILLER;
    }

}
