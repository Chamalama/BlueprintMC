package mike.dungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.util.FastLocation;
import mike.blueprint.util.Region;

import java.util.function.Consumer;

@Getter
@Setter
public class TriggerPoint implements Cloneable {

    private static int TRIGGER_ID = 0;

    private FastLocation triggerLocation;
    private double triggerRadius;
    private int triggerID;
    private Region triggerRegion;

    public TriggerPoint(FastLocation triggerLocation, double triggerRadius) {
        this.triggerLocation = triggerLocation;
        this.triggerRadius = triggerRadius;
        this.triggerRegion = null;
        this.triggerID = TRIGGER_ID++;
    }

    public TriggerPoint(Region triggerRegion) {
        this.triggerRegion = triggerRegion;
        this.triggerRadius = 0;
        this.triggerLocation = null;
        this.triggerID = TRIGGER_ID++;
    }

    public TriggerPoint edit(Consumer<TriggerPoint> pointConsumer) {
        pointConsumer.accept(this);
        return this;
    }

    public <V> void trigger(V v, Consumer<V> consumer) {
        consumer.accept(v);
    }

    public void trigger(Runnable runnable) {
        runnable.run();
    }


    @Override
    public TriggerPoint clone() {
        try {
            return (TriggerPoint) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
