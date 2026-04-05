package mike.blueprint.util;

import org.bukkit.Location;
import org.bukkit.Particle;

public class ParticleUtil {

    public static void circle(Location center, Particle particle, double radius, double angle, int count) {
        final double radAdd = (double) 360 / count;
        for(int i = 0; i < count; i++) {
            double radians = Math.toRadians(angle);
            final double xAdd = radius * (Math.sin(radians));
            final double zAdd = radius * (Math.cos(radians));
            center.getWorld().spawnParticle(particle, center.clone().add(xAdd, 0, zAdd), 1, 0.0, 0.0, 0.0, 0.0);
            angle += radAdd;
        }
    }

}
