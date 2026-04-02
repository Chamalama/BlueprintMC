package mike.dungeons.dungeon.team;

import lombok.Getter;
import lombok.Setter;
import mike.dungeons.Dungeons;
import mike.dungeons.dungeon.Dungeon;
import mike.dungeons.dungeon.DungeonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DungeonTeam {

    private final UUID leader;
    private final String name;
    private final List<UUID> members, invited;
    private EncounterData encounterData;
    private Dungeon dungeon;
    private String worldName;

    public static final int MAX_TEAM_SIZE = 4;

    public DungeonTeam(UUID leader, String name) {
        this.leader = leader;
        this.name = name;
        this.members = new ArrayList<>();
        this.invited = new ArrayList<>();
        this.encounterData = new EncounterData();
        this.dungeon = null;
        this.worldName = "";
    }

    public void invitePlayer(Player player) {
        final UUID id = player.getUniqueId();
        DungeonUtil.sendDungeonMessage(player, "You were invited to " + name + "'s dungeon team!");
        player.playSound(player, Sound.ENTITY_VILLAGER_TRADE, 1.0F, 1.0F);
        invited.add(id);
        Bukkit.getScheduler().runTaskLater(Dungeons.getInst(), () -> invited.remove(id), 15 * 20);
    }

    public void addMember(Player player) {
        DungeonUtil.sendDungeonMessage(player, "You joined " + name + "'s dungeon team!");
        player.playSound(player, Sound.ENTITY_VILLAGER_YES, 1.0F, 0.9F);
        members.add(player.getUniqueId());
    }

    public void removeMember(Player player, RemovalReason removalReason) {
        switch (removalReason) {
            case KICKED -> DungeonUtil.sendDungeonMessage(player, "<red>You were removed from " + name + "'s dungeon team!");
            case LEFT -> DungeonUtil.sendDungeonMessage(player, "<red>You left " + name + "'s dungeon team!");
            case DISBANDED -> DungeonUtil.sendDungeonMessage(player, "<red>Your dungeon team was disbanded!");
        }
        player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0F, 0.8F);
        members.remove(player.getUniqueId());
    }

    public int getTeamSize() {
        return members.size();
    }

    public List<Player> getPlayers(boolean includeLeader) {
        final List<Player> players = new ArrayList<>(members.stream().map(Bukkit::getPlayer).toList());
        if(includeLeader) {
            players.add(Bukkit.getPlayer(leader));
        }
        return players;
    }

    public Player getLeaderPlayer() {
        return Bukkit.getPlayer(leader);
    }

}
