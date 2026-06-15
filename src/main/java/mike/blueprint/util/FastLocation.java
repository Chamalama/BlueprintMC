package mike.blueprint.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.function.Consumer;

@Setter
@Getter
public class FastLocation implements Cloneable {

    private String worldName;
    private double x, y, z;
    private float yaw, pitch;
    private long id;

    public FastLocation(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0f;
        this.pitch = 0f;
        this.id = ((long)x & 0x3FFFFFFL) | (((long)z & 0x3FFFFFFL) << 26) | (((long)y & 0xFFFL) << 52);
    }

    public FastLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.id = -1;
    }

    public FastLocation(Location location) {
        this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getYaw(), location.getPitch());
    }

    public String serialize() {
        return "world=" + worldName + ",x=" + x + ",y=" + y + ",z=" + z;
    }

    public FastLocation edit(Consumer<FastLocation> fastLocationConsumer) {
        fastLocationConsumer.accept(this);
        return this;
    }

    public Location toBukkit() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public double distanceFrom(Location location) {
        final double dx = location.getX() - x;
        final double dy = location.getY() - y;
        final double dz = location.getZ() - z;

        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public FastLocation clone() {
        try {
            FastLocation clone = (FastLocation) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
