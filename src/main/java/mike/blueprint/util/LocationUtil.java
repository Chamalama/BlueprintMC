package mike.blueprint.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationUtil {

    private static final Random random = new Random();

    public static List<Block> getNearbyBlocks(final Location center, int radius, boolean excludeAir) {
        final List<Block> blocks = new ArrayList<>();
        final World world = center.getWorld();
        final int centerX = center.getBlockX();
        final int centerY = center.getBlockY();
        final int centerZ = center.getBlockZ();
        for(int xOff = centerX - radius; xOff <= centerX + radius; xOff++) {
            for(int yOff = centerY - radius; yOff <= centerY + radius; yOff++) {
                for(int zOff = centerZ - radius; zOff <= centerZ + radius; zOff++) {
                    final Block block = world.getBlockAt(xOff, yOff, zOff);
                    if(excludeAir && block.getType().isAir()) continue;
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static List<Block> getNearbyBlocks(final Location center, int radius, boolean excludeAir, Material targetType) {
        final List<Block> blocks = new ArrayList<>();
        final int centerX = center.getBlockX();
        final int centerY = center.getBlockY();
        final int centerZ = center.getBlockZ();
        for(int xOff = centerX - radius; xOff <= centerX + radius; xOff++) {
            for(int yOff = centerY - radius; yOff <= centerY + radius; yOff++) {
                for(int zOff = centerZ - radius; zOff <= centerZ + radius; zOff++) {
                    final int chunkX = xOff >> 4;
                    final int chunkZ = zOff >> 4;
                    final Chunk chunk = center.getWorld().getChunkAt(chunkX, chunkZ);
                    final Block block = chunk.getBlock(xOff & 15, yOff, zOff & 15);
                    if(excludeAir && block.isEmpty()) continue;
                    if(targetType != null && block.getType() != targetType) continue;
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static List<Block> getNearbyTopBlocks(final Location center, int radius) {
        final List<Block> blocks = new ArrayList<>();
        final World world = center.getWorld();
        final int centerX = center.getBlockX();
        final int centerZ = center.getBlockZ();
        for(int xOff = centerX - radius; xOff <= centerX + radius; xOff++) {
            for (int zOff = centerZ - radius; zOff <= centerZ + radius; zOff++) {
                final Block block = world.getBlockAt(xOff, world.getHighestBlockYAt(xOff, zOff), zOff);
                blocks.add(block);
            }
        }
        return blocks;
    }

    public static List<Block> getNearbyBlocks(final Location center, int radius, int yRadius, boolean excludeAir) {
        final List<Block> blocks = new ArrayList<>();
        final World world = center.getWorld();
        final int centerX = center.getBlockX();
        final int centerY = center.getBlockY();
        final int centerZ = center.getBlockZ();
        for(int xOff = centerX - radius; xOff <= centerX + radius; xOff++) {
            for(int yOff = centerY - yRadius; yOff <= centerY + yRadius; yOff++) {
                for(int zOff = centerZ - radius; zOff <= centerZ + radius; zOff++) {
                    final Block block = world.getBlockAt(xOff, yOff, zOff);
                    if(excludeAir && block.getType().isAir()) continue;
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public static List<Block> getOutlineBlocks(final Location center, int radius) {
        final List<Block> blocks = new ArrayList<>();
        final World world = center.getWorld();
        final int centerX = center.getBlockX();
        final int centerY = center.getBlockY();
        final int centerZ = center.getBlockZ();
        final int minX = centerX - radius;
        final int minY = centerY - radius;
        final int minZ = centerZ - radius;
        final int maxX = centerX + radius;
        final int maxY = centerY + radius;
        final int maxZ = centerZ + radius;
        for(int xOff = minX; xOff <= maxX; xOff++) {
            for(int yOff = minY; yOff <= maxY; yOff++) {
                for(int zOff = minZ; zOff <= maxZ; zOff++) {
                    if(xOff == minX || xOff == maxX || yOff == minY || yOff == maxY || zOff == minZ || zOff == maxZ) {
                        blocks.add(world.getBlockAt(xOff, yOff, zOff));
                    }
                }
            }
        }
        return blocks;
    }

    public static Location getRandomLocation(Region region, World world) {
        int randomX = random.nextInt(region.getMinX(), region.getMaxX());
        int randomZ = random.nextInt(region.getMinZ(), region.getMaxZ());
        return new Location(world, randomX, world.getHighestBlockYAt(randomX, randomZ), randomZ);
    }

}
