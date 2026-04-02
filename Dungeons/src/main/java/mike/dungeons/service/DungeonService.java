package mike.dungeons.service;

import lombok.Getter;
import mike.blueprint.loader.Component;
import mike.blueprint.util.FastLocation;
import mike.blueprint.util.Region;
import mike.blueprint.util.WorldUtil;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.*;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.dungeon.team.EncounterData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Component
public class DungeonService {

    private final Map<UUID, DungeonRoom> editing = new HashMap<>();
    private final Map<UUID, Location[]> regionPositions = new HashMap<>();

    public void generateTeamDungeon(DungeonTeam dungeonTeam, Dungeon dungeon) {
        final String worldName = dungeon.getWorldName() + "." + dungeonTeam.getName();
        WorldUtil.fastCopy(dungeon.getWorldName(), worldName, world -> {
            final FastLocation spawn = dungeon.getSpawnLocation();
            final Location toTeleport = new Location(world, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch()).toCenterLocation();
            for(Player player : dungeonTeam.getPlayers(true)) {
                player.teleportAsync(toTeleport);
            }
        });
        dungeonTeam.setDungeon(dungeon);
        dungeonTeam.setWorldName(worldName);
        Dungeons.getInst().log("Generated new dungeon instance of " + dungeon.getDungeonName() + " for " + dungeonTeam.getName() + "...");
    }

    public void removeTeamDungeon(DungeonTeam dungeonTeam) {
        final EncounterData encounterData = dungeonTeam.getEncounterData();
        if(encounterData.getEncounterEvents() != null) {
            encounterData.getEncounterEvents().stopEvent(dungeonTeam);
        }
        encounterData.clear();
        encounterData.setCurrentEncounter(null);
        final Dungeon dungeon = dungeonTeam.getDungeon();
        if(dungeon != null) {
            final String worldName = dungeonTeam.getWorldName();
            final World world = Bukkit.getWorld(worldName);
            if (world != null) {
                WorldUtil.unloadWorld(world);
                Dungeons.getInst().log("Removing dungeon world... " + worldName);
            }
        }
        dungeonTeam.setWorldName(null);
    }

    public void resetEncounter(DungeonTeam dungeonTeam) {
        final EncounterData data = dungeonTeam.getEncounterData();
        if(data.getEncounterEvents() != null) {
            data.getEncounterEvents().stopEvent(dungeonTeam);
        }
        data.clear();
        for(Player player : dungeonTeam.getPlayers(true)) {
            player.teleportAsync(data.getEncounterSpawnPoint().toBukkit().toCenterLocation());
        }
    }

    public void addEditor(Player p, DungeonRoom dungeonRoom) {
        editing.put(p.getUniqueId(), dungeonRoom);
        regionPositions.put(p.getUniqueId(), new Location[2]);
        DungeonUtil.sendDungeonMessage(p, "You are now editing the " + dungeonRoom.getRoomID() + " dungeon room!");
    }

    public void removeEditor(Player player) {
        editing.remove(player.getUniqueId());
        DungeonUtil.sendDungeonMessage(player, "You are no longer editing the dungeon room!");
    }

    public void setRoomRegion(DungeonRoom dungeonRoom, Region region) {
        dungeonRoom.setRoomRegion(new DungeonRegion(dungeonRoom.getOwningDungeon().getWorldName(), region));
        dungeonRoom.update();
    }

    public DungeonRoom getEditing(Player player) {
        return editing.get(player.getUniqueId());
    }

}
