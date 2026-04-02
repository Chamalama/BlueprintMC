package mike.dungeons.dungeon.dungeons.outpost;

import mike.blueprint.util.FastLocation;
import mike.blueprint.util.TitleUtil;
import mike.dungeons.dungeon.DungeonRegistry;
import mike.dungeons.dungeon.DungeonRoom;
import mike.dungeons.dungeon.EntityLocation;
import mike.dungeons.dungeon.component.GenericEventComponent;
import mike.dungeons.dungeon.component.KillComponent;
import mike.dungeons.dungeon.component.SpawnComponent;
import mike.dungeons.dungeon.dungeons.outpost.component.GateCaptureComponent;
import mike.dungeons.dungeon.entity.DungeonMobs;
import mike.dungeons.dungeon.team.DungeonTeam;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class GateRoom extends DungeonRoom {

    public GateRoom() {
        super(DungeonRegistry.getDungeon(Outpost.class), "GATE_ROOM");
        this.encounterTime = 180L;
        this.addComponent(new SpawnComponent(DungeonMobs.GATE_GUARD::spawnEntity, List.of(
                EntityLocation.of(new FastLocation(DungeonRegistry.getDungeon(Outpost.class).getWorldName(), -607, 214, 121), 5, 30000)
        )));
        this.addComponent(new KillComponent((team, entity) -> {

        }));
        this.addComponent(new GenericEventComponent(new GateCaptureComponent()));
    }

    @Override
    public void startEncounter(DungeonTeam dungeonTeam) {
        dungeonTeam.getEncounterData().setEncounterStarted(true);
        for(Player player : dungeonTeam.getPlayers(true)) {
            TitleUtil.sendTitle(player, "<gold><b>Encounter Started", "<gray>Open the gate", 1, 2, 1);
            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_7, 0.8F, 0.8F);
        }
    }
}
