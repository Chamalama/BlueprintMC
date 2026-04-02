package mike.dungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.util.FastLocation;

import java.util.function.Consumer;

@Getter
@Setter
public class EntityLocation implements Cloneable {

    private static int ID_TRACKER = 0;

    private FastLocation spawnLocation;
    private int locationID, spawnCount, activeEntities;
    private long spawnTime, lastSpawnTime;

    public EntityLocation(FastLocation spawnLocation, int spawnCount, long spawnTime) {
        this.spawnLocation = spawnLocation;
        this.spawnCount = spawnCount;
        this.spawnTime = spawnTime;
        this.locationID = ID_TRACKER++;
        this.activeEntities = 0;
        this.lastSpawnTime = 0L;
    }

    public boolean canSpawn() {
        return activeEntities <= 0 && System.currentTimeMillis() - lastSpawnTime >= spawnTime;
    }

    public void incrementActive() {
        ++this.activeEntities;
    }

    public void decrementActive() {
        --this.activeEntities;
    }

    public static EntityLocation of(FastLocation spawnLocation, int spawnCount, long spawnTime) {
        return new EntityLocation(spawnLocation, spawnCount, spawnTime);
    }

    public EntityLocation edit(Consumer<EntityLocation> consumer) {
        consumer.accept(this);
        return this;
    }

    @Override
    public EntityLocation clone() {
        try {
            return (EntityLocation) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
