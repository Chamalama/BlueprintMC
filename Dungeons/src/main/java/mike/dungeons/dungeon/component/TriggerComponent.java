package mike.dungeons.dungeon.component;

import lombok.Getter;
import lombok.Setter;
import mike.dungeons.dungeon.DungeonRoom;
import mike.dungeons.dungeon.TriggerPoint;
import mike.dungeons.dungeon.team.DungeonTeam;

import java.util.function.Consumer;

@Getter
public class TriggerComponent implements Cloneable {

    private final TriggerPoint triggerPoint;
    private final Consumer<DungeonTeam> teamConsumer;

    public TriggerComponent(TriggerPoint triggerPoint, Consumer<DungeonTeam> teamConsumer) {
        this.triggerPoint = triggerPoint;
        this.teamConsumer = teamConsumer;
    }

    public void handle(DungeonTeam team) {
        teamConsumer.accept(team);
    }

    @Override
    public TriggerComponent clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (TriggerComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
