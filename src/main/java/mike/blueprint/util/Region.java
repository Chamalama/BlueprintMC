package mike.blueprint.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Region {

    private final int minX, minY, minZ, maxX, maxY, maxZ;

    public Region(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public Region(Location one, Location two) {
        this(
                min(one.getBlockX(), two.getBlockX()),
                min(one.getBlockY(), two.getBlockY()),
                min(one.getBlockZ(), two.getBlockZ()),
                max(one.getBlockX(), two.getBlockX()),
                max(one.getBlockY(), two.getBlockY()),
                max(one.getBlockZ(), two.getBlockZ())
        );
    }

    public Set<Block> getRegionBlocks(World world) {
        final Set<Block> blocks = new HashSet<>();
        for(int x = minX; x < maxX; x++) {
            for(int y = minY; y < maxY; y++) {
                for(int z = minZ; z < maxZ; z++) {
                    final Block block = world.getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static int min(int one, int two) {
        return Math.min(one, two);
    }

    public static int max(int one, int two) {
        return Math.max(one, two);
    }

}
