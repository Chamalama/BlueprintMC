package mike.dungeons.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import mike.blueprint.loader.Component;
import mike.blueprint.util.FastLocation;
import mike.blueprint.util.Region;
import mike.dungeons.dungeon.DungeonRegion;
import mike.dungeons.dungeon.DungeonRoom;
import mike.dungeons.dungeon.DungeonUtil;
import mike.dungeons.dungeon.TriggerPoint;
import mike.dungeons.service.DungeonService;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Component
public class DungeonEditListener implements Listener {

    private final DungeonService dungeonService;

    public DungeonEditListener(DungeonService dungeonService) {
        this.dungeonService = dungeonService;
    }

    @EventHandler
    public void onSetPosition(AsyncChatEvent event) {
        final Player player = event.getPlayer();
        final String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        final DungeonRoom editing = dungeonService.getEditing(player);
        if(editing == null) return;
        final Location[] regionPositions = dungeonService.getRegionPositions().get(player.getUniqueId());
        final Location location = player.getLocation();
        if(message.equalsIgnoreCase("spawn")) {
            editing.setEncounterSpawnPoint(new FastLocation(player.getLocation()));
            DungeonUtil.sendDungeonMessage(player, "<green>Set encounter spawn location at " + player.getLocation() + "!");
            event.setCancelled(true);
            editing.update();
            return;
        }
        if(message.startsWith("trigger")) {
            double radius = Double.parseDouble(message.split(" ")[1]);
            editing.setStartPoint(new TriggerPoint(new FastLocation(player.getLocation()), radius));
            DungeonUtil.sendDungeonMessage(player, "<green>Set trigger point at " + player.getLocation() + " with radius of " + radius);
            event.setCancelled(true);
            editing.update();
            return;
        }
        if(message.equalsIgnoreCase("region1")) {
            regionPositions[0] = location;
            DungeonUtil.sendDungeonMessage(player, "<green>Set region position one!");
            event.setCancelled(true);
        }
        if(message.equalsIgnoreCase("region2")) {
            regionPositions[1] = location;
            DungeonUtil.sendDungeonMessage(player, "<green>Set region position two!");
            event.setCancelled(true);
        }
        if(regionPositions[0] != null && regionPositions[1] != null) {
            editing.setRoomRegion(new DungeonRegion(editing.getOwningDungeon().getWorldName(), new Region(regionPositions[0], regionPositions[1])));
            DungeonUtil.sendDungeonMessage(player, "Set new region for " + editing.getRoomID() + "!");
            editing.update();
        }
    }

}
