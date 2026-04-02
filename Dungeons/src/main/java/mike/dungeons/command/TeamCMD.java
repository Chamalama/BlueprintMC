package mike.dungeons.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import mike.blueprint.loader.Component;
import mike.dungeons.dungeon.DungeonUtil;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.dungeon.team.RemovalReason;
import mike.dungeons.dungeon.gui.TeamGUI;
import mike.dungeons.service.DungeonTeamService;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("team")
@Component
public class TeamCMD extends BaseCommand {

    private final DungeonTeamService dungeonTeamService;

    public TeamCMD(DungeonTeamService dungeonTeamService) {
        this.dungeonTeamService = dungeonTeamService;
    }

    @Default
    public void onDefault(Player player) {
        final DungeonTeam dungeonTeam = dungeonTeamService.getPlayersTeam(player);
        if(dungeonTeam == null) return;
        player.openInventory(new TeamGUI(dungeonTeam).getInventory());
    }

    @Subcommand("create")
    public void onCreateTeam(Player player) {
        if(dungeonTeamService.getPlayersTeam(player) != null) {
            DungeonUtil.sendDungeonMessage(player, "<red>You are already in a dungeon team!");
            return;
        }
        dungeonTeamService.registerTeam(new DungeonTeam(player.getUniqueId(), player.getName()));
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    public void onInvite(Player player, OnlinePlayer invited) {
        if(invited == null) {
            DungeonUtil.sendDungeonMessage(player, "<red>Invalid player!");
            return;
        }
        final Player invitedPlayer = invited.getPlayer();
        final DungeonTeam dungeonTeam = dungeonTeamService.getPlayersTeam(player);
        if(dungeonTeam == null) {
            DungeonUtil.sendDungeonMessage(player, "<red>You are not in a dungeon team!");
            return;
        }
        if(!dungeonTeamService.isTeamLeader(player)) {
            DungeonUtil.sendDungeonMessage(player, "<red>Only the team leader can invite players!");
            return;
        }
        if(dungeonTeam.getTeamSize() >= DungeonTeam.MAX_TEAM_SIZE) {
            DungeonUtil.sendDungeonMessage(player, "<red>Your dungeon team is at capacity! <gray>(Max Players: 4)");
            return;
        }
        if(invitedPlayer == player) {
            DungeonUtil.sendDungeonMessage(player, "<red>You cannot invite yourself!");
            return;
        }
        if(dungeonTeam.getMembers().contains(invitedPlayer.getUniqueId())) {
            DungeonUtil.sendDungeonMessage(player, "<red>This player is already in your team!");
            return;
        }
        DungeonUtil.sendDungeonMessage(player, "You invited " + invitedPlayer.getName() + " to your team!");
        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.2F);
        dungeonTeam.invitePlayer(invitedPlayer);
    }

    @Subcommand("join")
    @CommandCompletion("@players")
    public void onJoin(Player player, OnlinePlayer team) {
        final Player teamPlayer = team.getPlayer();
        if(player == teamPlayer) {
            DungeonUtil.sendDungeonMessage(player, "<red>You cannot join yourself!");
            return;
        }
        final DungeonTeam playersTeam = dungeonTeamService.getPlayersTeam(player);
        if(playersTeam != null) {
            DungeonUtil.sendDungeonMessage(player, "<red>Leave your current team first!");
            return;
        }
        final DungeonTeam dungeonTeam = dungeonTeamService.getPlayersTeam(teamPlayer);
        if(dungeonTeam == null) {
            DungeonUtil.sendDungeonMessage(player, "<red>That player is not in a dungeon team!");
            return;
        }
        if(!dungeonTeam.getInvited().contains(player.getUniqueId())) {
            DungeonUtil.sendDungeonMessage(player, "<red>You were not invited to this team!");
            return;
        }
        if(dungeonTeam.getTeamSize() >= DungeonTeam.MAX_TEAM_SIZE) {
            DungeonUtil.sendDungeonMessage(player, "<red>This team is full!");
            return;
        }
        dungeonTeamService.registerNewMember(dungeonTeam, player);
    }

    @Subcommand("leave")
    public void onLeave(Player player) {
        final DungeonTeam dungeonTeam = dungeonTeamService.getPlayersTeam(player);
        if(dungeonTeam == null) {
            DungeonUtil.sendDungeonMessage(player, "<red>You are not in a dungeon team!");
            return;
        }
        if(dungeonTeamService.isTeamLeader(player)) {
            DungeonUtil.sendDungeonMessage(player, "<red>Please use /team disband!");
            return;
        }
        dungeonTeamService.unregisterMember(player, RemovalReason.LEFT);
    }

    @Subcommand("disband")
    public void onDisband(Player player) {
        final DungeonTeam dungeonTeam = dungeonTeamService.getPlayersTeam(player);
        if(dungeonTeam == null) {
            DungeonUtil.sendDungeonMessage(player, "<red>You are not in a dungeon team!");
            return;
        }
        final UUID uuid = player.getUniqueId();
        if(!dungeonTeam.getLeader().equals(uuid)) {
            DungeonUtil.sendDungeonMessage(player, "<red>Only the team leader can do this!");
            return;
        }
        dungeonTeamService.disbandTeam(dungeonTeam);
    }

}
