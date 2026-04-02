package mike.dungeons.dungeon.entity.component;

import lombok.Getter;
import lombok.Setter;
import mike.dungeons.dungeon.entity.DungeonEntity;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

@Getter
@Setter
public class HealthComponent {

    private double maxHealth;
    private double health;
    private double prevHealth;

    public HealthComponent(double maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.prevHealth = maxHealth;
    }

    public void damage(double damage) {
        this.prevHealth = this.health;
        this.health = Math.max(0, health - damage);
    }

    public void heal(double heal) {
        this.prevHealth = health;
        this.health = Math.min(maxHealth, health + heal);
    }

    public double getDamage() {
        return this.health - prevHealth;
    }

    public boolean isDead() {
        return health <= 0;
    }

}
