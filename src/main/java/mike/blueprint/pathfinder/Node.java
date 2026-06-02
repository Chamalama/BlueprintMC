package mike.blueprint.pathfinder;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Node {

    private int x, y, z;
    private double g, h;
    private Node parent;

    public Node(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.g = 0;
        this.h = 0;
        this.parent = null;
    }

    public double f() {
        return h + g;
    }

    public double getDistance(Location end) {
        double dx = x - end.getBlockX();
        double dy = y - end.getBlockY();
        double dz = z - end.getBlockZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Node other)) return false;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

}
