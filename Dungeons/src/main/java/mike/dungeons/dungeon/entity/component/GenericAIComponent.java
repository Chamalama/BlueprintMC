package mike.dungeons.dungeon.entity.component;

import lombok.Getter;
import mike.dungeons.dungeon.entity.DungeonEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class GenericAIComponent {

    private AIState currentState;
    private final Map<Class<? extends AIState>, AIState> states = new HashMap<>();

    public GenericAIComponent(AIState currentState) {
        this.currentState = currentState;
    }

    public void tick(DungeonEntity entity) {
        if(currentState == null) return;
        currentState.tick(entity, this);
    }

    public void transition(DungeonEntity entity, Class<? extends AIState> nextState) {
        final AIState next = states.get(nextState);
        if(next == null) return;
        currentState.stop(entity);
        currentState = next;
        currentState.start(entity);
    }

}
