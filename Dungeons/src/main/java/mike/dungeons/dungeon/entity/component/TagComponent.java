package mike.dungeons.dungeon.entity.component;

import lombok.Getter;
import mike.dungeons.dungeon.entity.DungeonEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public class TagComponent {

    private final Set<NamespacedKey> keys = new HashSet<>();

    public TagComponent(NamespacedKey... key) {
        keys.addAll(Arrays.asList(key));
    }

    public void applyTags(LivingEntity entity) {
        keys.forEach(key -> entity.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1));
    }

    public void addTag(NamespacedKey key) {
        this.keys.add(key);
    }

    public boolean hasTag(NamespacedKey key) {
        return keys.contains(key);
    }

}
