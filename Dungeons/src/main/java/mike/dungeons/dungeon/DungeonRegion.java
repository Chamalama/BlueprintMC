package mike.dungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.util.Region;
import org.bukkit.Location;

@Getter
@Setter
public class DungeonRegion {

    private final String dungeonWorldName;
    private final Region region;

    public DungeonRegion(String dungeonWorldName, Region region) {
        this.dungeonWorldName = dungeonWorldName;
        this.region = region;
    }

    public boolean contains(Location location) {
        try {
            int index = location.getWorld().getName().lastIndexOf(".");
            if(index == -1) return false;
            final String worldName = location.getWorld().getName().substring(0, index);
            final int locX = location.getBlockX();
            final int locY = location.getBlockY();
            final int locZ = location.getBlockZ();
            return worldName.equalsIgnoreCase(dungeonWorldName)
                    && locX > region.getMinX() && locX < region.getMaxX()
                    && locY > region.getMinY() && locY < region.getMaxY()
                    && locZ > region.getMinZ() && locZ < region.getMaxZ();
        }catch (StringIndexOutOfBoundsException ignored) {
            return false;
        }
    }

}
