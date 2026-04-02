package mike.dungeons.dungeon.entity.component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

@Getter
@Setter
@AllArgsConstructor
public class CombatComponent {

    private double damage;
    private double armor;
    private double critChance;
    private double critMultiplier;

    public boolean shouldCrit() {
        return Math.random() <= critChance;
    }

    public void apply(LivingEntity entity) {
        if(entity.getAttribute(Attribute.ATTACK_DAMAGE) == null) {
            entity.registerAttribute(Attribute.ATTACK_DAMAGE);
        }
        entity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(damage);
    }

}
