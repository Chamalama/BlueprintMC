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
        this.id = ((long)x & 0x3FFFFFFL) | (((long)z & 0x3FFFFFFL) << 26) | (((long)y & 0xFFFL) << 52);
    }

    public BLocation(Location location) {
        this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

}
