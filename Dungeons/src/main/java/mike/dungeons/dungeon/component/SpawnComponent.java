package mike.dungeons.dungeon.component;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.util.FastLocation;
import mike.dungeons.dungeon.DungeonKeys;
import mike.dungeons.dungeon.EntityLocation;
import mike.dungeons.dungeon.entity.DungeonEntity;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.dungeon.team.EncounterData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Supplier;

@Getter
@Setter
public class SpawnComponent {

    private static final Random random = new Random();

    private static final int MAX_SPAWNS_PER_TICK = 10;

    private final Supplier<DungeonEntity> entity;
    private final List<EntityLocation> spawnLocations;

    private final Queue<Runnable> spawnQueue = new LinkedList<>();

    public SpawnComponent(Supplier<DungeonEntity> entity, List<EntityLocation> spawnLocations) {
        this.entity = entity;
        this.spawnLocations = spawnLocations;
    }

    public void spawn(DungeonTeam dungeonTeam) {
        if(spawnLocations.isEmpty()) return;
        final EncounterData encounterData = dungeonTeam.getEncounterData();
        for(EntityLocation entityLocation : encounterData.getEncounterSpawns()) {
            if(!entityLocation.canSpawn()) continue;
            final int spawnCount = entityLocation.getSpawnCount();
            final FastLocation fastLocation = entityLocation.getSpawnLocation();
            final Location bukkitLocation = fastLocation.toBukkit();
            if(spawnCount > 1) {
                for(int i = 0; i < spawnCount; i++) {
                    final double randomX = random.nextDouble(-4, 4);
                    final double randomZ = random.nextDouble(-4, 4);
                    final Location location = bukkitLocation.clone().add(randomX, 0, randomZ);
                    spawnQueue.add(() -> spawnEntity(entityLocation, location.toCenterLocation(), dungeonTeam));
                }
            }else{
                spawnQueue.add(() -> spawnEntity(entityLocation, bukkitLocation.toCenterLocation(), dungeonTeam));
            }
        }
    }

    public void tick() {
        int spawnsThisTick = 0;
        while(!spawnQueue.isEmpty() && ++spawnsThisTick < MAX_SPAWNS_PER_TICK) {
            spawnQueue.poll().run();
        }
    }

    private void spawnEntity(EntityLocation location, Location spawnLocation, DungeonTeam team) {
        final EncounterData data = team.getEncounterData();
        if(data == null) return;
        entity.get().spawn(spawnLocation, team, dungeonEntity -> {
            dungeonEntity.getPersistentDataContainer().set(DungeonKeys.LOCATION_ID, PersistentDataType.INTEGER, location.getLocationID());
        });
        location.incrementActive();
        location.setLastSpawnTime(System.currentTimeMillis());
    }

    public void removeEntityFromLocation(LivingEntity le, DungeonTeam team) {
        if(!le.getPersistentDataContainer().has(DungeonKeys.LOCATION_ID)) return;
        final int entityID = le.getPersistentDataContainer().get(DungeonKeys.LOCATION_ID, PersistentDataType.INTEGER);
        final Optional<EntityLocation> location = team.getEncounterData().getEncounterSpawns().stream().filter(entityLocation -> entityLocation.getLocationID() == entityID).findFirst();
        location.ifPresent(EntityLocation::decrementActive);
    }

}
