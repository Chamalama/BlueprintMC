package mike.dungeons.service;

import lombok.Getter;
import mike.blueprint.loader.Component;
import mike.dungeons.dungeon.DungeonUtil;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.dungeon.team.RemovalReason;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

@Component
public class DungeonTeamService {

    @Getter
    private final Map<UUID, DungeonTeam> dungeonTeams = new HashMap<>();

    public void registerTeam(DungeonTeam team) {
        dungeonTeams.put(team.getLeader(), team);
        final Player player = team.getLeaderPlayer();
        DungeonUtil.sendDungeonMessage(player, "You created a new dungeon team!");
        player.playSound(player, Sound.ENTITY_BAT_TAKEOFF, 0.6F, 1.0F);
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.6F, 0.8F);
    }

    public void registerNewMember(DungeonTeam team, Player player) {
        dungeonTeams.put(player.getUniqueId(), team);
        team.addMember(player);
    }

    public void unregisterMember(Player player, RemovalReason removalReason) {
        final UUID uuid = player.getUniqueId();
        if(!dungeonTeams.containsKey(uuid)) return;
        final DungeonTeam team = dungeonTeams.get(uuid);
        team.removeMember(player, removalReason);
        dungeonTeams.remove(uuid);
    }

    public void disbandTeam(DungeonTeam team) {
        team.getInvited().clear();
        for(UUID uuid : team.getMembers()) {
            dungeonTeams.remove(uuid);
        }
        for(Player player : team.getPlayers(false)) {
            team.removeMember(player, RemovalReason.DISBANDED);
        }
        final Player leader = team.getLeaderPlayer();
        DungeonUtil.sendDungeonMessage(leader, "You disbanded your dungeon team!");
        leader.playSound(leader, Sound.ENTITY_VILLAGER_NO, 1.0F, 0.8F);
        dungeonTeams.remove(team.getLeader());
    }

    public boolean isTeamLeader(Player player) {
        final UUID uuid = player.getUniqueId();
        final DungeonTeam team = getPlayersTeam(player);
        if(team == null) return false;
        return team.getLeader().equals(uuid);
    }

    public DungeonTeam getPlayersTeam(Player player) {
        return dungeonTeams.get(player.getUniqueId());
    }

    public DungeonTeam getTeam(UUID uuid) {
        return dungeonTeams.get(uuid);
    }

    public Collection<DungeonTeam> getTeams() {
        return dungeonTeams.values();
    }

}
