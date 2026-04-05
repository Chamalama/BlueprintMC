package mike.blueprint.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class MovementUtil {

    public static void moveTo(LivingEntity entity, Location endLocation, double distanceThreshold, double speed) {
        final Location current = entity.getLocation();
        final double distance = current.distanceSquared(endLocation);
        if(distance < distanceThreshold) return;
        final Vector currVector = current.toVector();
        final Vector endVector = endLocation.toVector();
        final Vector pointToTravel = endVector.subtract(currVector).normalize().multiply(speed).setY(0);
        final Location newLocation = entity.getLocation().add(pointToTravel);
        final Block block = newLocation.getBlock();
        if(block.isEmpty()) {
            if(block.getRelative(BlockFace.DOWN).isEmpty()) {
                entity.teleportAsync(newLocation.clone().add(0, getYDiff(newLocation, false, 16), 0));
            } else {
                entity.teleportAsync(newLocation);
            }
        }else{
            final Block above = block.getRelative(BlockFace.UP);
            if(above.isEmpty()) {
                entity.teleportAsync(newLocation.clone().add(0, getYDiff(newLocation, true, 16), 0));
            }
        }
    }

    private static int getYDiff(Location center, boolean above, int checkCount) {
        Block block = center.getBlock();
        for (int yDiff = 0; yDiff <= checkCount; yDiff++) {
            Block relative = block.getRelative(0, above ? yDiff : -yDiff, 0);
            if (above) {
                if (relative.isEmpty()) {
                    return yDiff;
                }
            } else {
                if (!relative.isEmpty()) {
                    return -(yDiff - 1);
                }
            }
        }
        return 0;
    }

}
