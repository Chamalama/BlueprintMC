package mike.dungeons.dungeon.component;

import lombok.Getter;
import mike.dungeons.dungeon.team.DungeonTeam;

import java.util.HashMap;
import java.util.Map;

@Getter
public class GenericEventComponent implements Cloneable {

    private EventComponent currentEvent;
    private final Map<Class<? extends EventComponent>, EventComponent> eventComponentMap;

    public GenericEventComponent(EventComponent currentEvent) {
        this.currentEvent = currentEvent;
        this.eventComponentMap = new HashMap<>();
    }

    public GenericEventComponent addEvent(EventComponent eventComponent) {
        eventComponentMap.put(eventComponent.getClass(), eventComponent);
        return this;
    }

    public EventComponent getEvent(Class<? extends EventComponent> componentClass) {
        return eventComponentMap.get(componentClass);
    }

    public void tick(DungeonTeam team) {
        if(this.currentEvent == null) return;
        this.currentEvent.tick(team, this);
    }

    public void startEvent(DungeonTeam team) {
        if(this.currentEvent == null) return;
        this.currentEvent.startEvent(team);
    }

    public void stopEvent(DungeonTeam team) {
        if(this.currentEvent == null) return;
        this.currentEvent.stopEvent(team);
    }

    public void transition(Class<? extends EventComponent> next, DungeonTeam team) {
        final EventComponent nextEvent = eventComponentMap.get(next);
        if(nextEvent == null) return;
        if(currentEvent != null) {
            currentEvent.stopEvent(team);
        }
        currentEvent = nextEvent;
        currentEvent.startEvent(team);
    }

    @Override
    public GenericEventComponent clone() {
        try {
            return (GenericEventComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
