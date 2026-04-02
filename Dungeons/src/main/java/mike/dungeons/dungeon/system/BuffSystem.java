package mike.dungeons.dungeon.system;

import mike.blueprint.loader.Component;
import mike.blueprint.util.AbstractTask;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.DungeonBuff;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Component
public class BuffSystem extends AbstractTask {

    private static final Map<UUID, DungeonBuff> currentBuff = new HashMap<>();

    public BuffSystem() {
        super(Dungeons.getInst(), 10, 20, true);
    }

    public static void addBuff(Entity entity, DungeonBuff buff) {
        currentBuff.put(entity.getUniqueId(), buff);
        if(entity instanceof Player player) {
            player.showBossBar(buff.getDisplayBar());
        }
    }

    public static DungeonBuff getBuff(Entity entity) {
        return currentBuff.get(entity.getUniqueId());
    }

    public static boolean hasBuff(Entity entity) {
        return currentBuff.containsKey(entity.getUniqueId());
    }

    public static String getBuffID(Entity entity) {
        return getBuff(entity).getId();
    }

    @Override
    public void run() {
        final Iterator<Map.Entry<UUID, DungeonBuff>> buffIterator = currentBuff.entrySet().iterator();
        while(buffIterator.hasNext()) {
            final Map.Entry<UUID, DungeonBuff> buff = buffIterator.next();
            final UUID id = buff.getKey();
            final DungeonBuff dungeonBuff = buff.getValue();
            final Player player = Bukkit.getPlayer(id);
            if(player == null) {
                buffIterator.remove();
                continue;
            }
            dungeonBuff.decrementTime();
            dungeonBuff.updateBar();
            if(dungeonBuff.getBuffTimer() <= 0) {
                dungeonBuff.getDisplayBar().removeViewer(player);
                player.playSound(player, Sound.BLOCK_LAVA_EXTINGUISH, 1.0F, 0.8F);
                buffIterator.remove();
            }
        }
    }
}
