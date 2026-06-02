package mike.blueprint.util;

import mike.blueprint.pathfinder.Node;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
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
        final Vector pointToTravel = endVector.subtract(currVector).normalize().multiply(1.0).setY(0);
        final Location newLocation = entity.getLocation().clone().add(pointToTravel).add(0, 0.5, 0);
        final World world = newLocation.getWorld();
        world.spawnParticle(Particle.FLAME, newLocation, 1, 0.0, 0.0, 0.0, 0.0);
        Block block = newLocation.getBlock();
        if(!block.isSolid()) {
            entity.setVelocity(pointToTravel.multiply(speed));
        } else {
            if(block.getY() > current.getBlockY()) {
                int diff = block.getY() - current.getBlockY();
                double jumpHeight = diff * 0.5;
                pointToTravel.setY(jumpHeight);
            }else{
                int diff = current.getBlockY() - block.getY();
                double fallHeight = diff * 0.5;
                pointToTravel.setY(fallHeight);
            }
            entity.setVelocity(pointToTravel.multiply(speed));
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
