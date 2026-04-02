package mike.dungeons.dungeon.entity.component;

import lombok.Getter;
import lombok.Setter;
import mike.dungeons.dungeon.entity.DungeonEntity;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

@Getter
@Setter
public class AttackComponent {

    private long attackSpeed, lastAttackTime;
    private double attackDistance;
    private Sound attackSound;

    public AttackComponent(long attackSpeed, double attackDistance, @Nullable Sound attackSound) {
        this.attackSpeed = attackSpeed;
        this.attackDistance = attackDistance;
        this.attackSound = attackSound;
        this.lastAttackTime = 0L;
    }

    public boolean canAttack() {
        return System.currentTimeMillis() - lastAttackTime >= attackSpeed;
    }

    public void attack(DungeonEntity entity, LivingEntity target) {
        final LivingEntity le = entity.getEntity();
        final double distanceToTarget = le.getLocation().distanceSquared(target.getLocation());
        if(distanceToTarget > attackDistance) return;
        le.swingMainHand();
        le.attack(target);
        target.getWorld().playSound(target, attackSound, 0.8F, 1.0F);
        this.lastAttackTime = System.currentTimeMillis();
    }

}
