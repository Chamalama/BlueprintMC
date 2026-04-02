package mike.dungeons.dungeon.component;

import mike.dungeons.dungeon.team.DungeonTeam;

public interface EventComponent {

    void tick(DungeonTeam team, GenericEventComponent genericEventComponent);
    void startEvent(DungeonTeam dungeonTeam);
    void stopEvent(DungeonTeam dungeonTeam);
    long tickTime();
    boolean startOnEnter();

}
