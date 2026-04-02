package mike.dungeons.dungeon.entity.component;

import lombok.Getter;
import lombok.Setter;
import mike.dungeons.dungeon.entity.DungeonEntity;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

@Getter
@Setter
public class ScaleComponent {

    private double scale;

    public ScaleComponent(double scale) {
        this.scale = scale;
    }

    public void scale(LivingEntity dungeonEntity) {
        dungeonEntity.getAttribute(Attribute.SCALE).setBaseValue(scale);
    }

}
