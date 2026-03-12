package mike.blueprint.util;

import lombok.Getter;
import org.bukkit.Location;

@Getter
public class BLocation {

    private final String worldName;
    private final int x, y, z;
    private final long id;

    public BLocation(String worldName, int x, int y, int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = ((long) x << 32) ^ ((long)z << 16) ^ y;
    }

    public BLocation(Location location) {
        this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

}
