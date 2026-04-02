package mike.dungeons.listener;

import mike.blueprint.loader.Component;
import mike.dungeons.dungeon.DungeonRoom;
import mike.dungeons.dungeon.component.KillComponent;
import mike.dungeons.dungeon.component.SpawnComponent;
import mike.dungeons.dungeon.entity.DungeonEntity;
import mike.dungeons.dungeon.entity.DungeonMobs;
import mike.dungeons.dungeon.entity.component.CombatComponent;
import mike.dungeons.dungeon.entity.component.DeathComponent;
import mike.dungeons.dungeon.entity.component.HealthComponent;
import mike.dungeons.dungeon.system.DamageSystem;
import mike.dungeons.dungeon.team.DungeonTeam;
import mike.dungeons.service.DungeonTeamService;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

@Component
public class DamageSystemListener implements Listener {

    private final DamageSystem damageSystem;
    private final DungeonTeamService dungeonTeamService;

    public DamageSystemListener(DamageSystem damageSystem, DungeonTeamService dungeonTeamService) {
        this.damageSystem = damageSystem;
        this.dungeonTeamService = dungeonTeamService;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof LivingEntity damaged)) return;
        if(!(event.getDamager() instanceof LivingEntity damager)) return;
        final DungeonEntity damagerEntity = DungeonMobs.getEntity(damager.getUniqueId());
        if(damagerEntity != null) {
            final CombatComponent combatComponent = damagerEntity.getComponent(CombatComponent.class);
            if(combatComponent != null) {
                if(combatComponent.shouldCrit()) {
                    event.setDamage(event.getDamage() * combatComponent.getCritMultiplier());
                }
            }
            return;
        }
        final DungeonTeam team = dungeonTeamService.getTeam(damager.getUniqueId());
        if(team == null || team.getDungeon() == null || team.getEncounterData().getCurrentEncounter() == null) return;
        damageSystem.addDamageEntry(damager, damaged);
        final DungeonEntity dungeonEntity = DungeonMobs.getEntity(damaged.getUniqueId());
        if(dungeonEntity != null) {
            final HealthComponent healthComponent = dungeonEntity.getComponent(HealthComponent.class);
            if (healthComponent != null) {
                healthComponent.damage(event.getDamage());
                if (healthComponent.isDead()) {
                    damaged.setHealth(0);
                }
            }
            event.setDamage(0);
        }
    }


    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        final LivingEntity le = event.getEntity();
        final DungeonTeam team = damageSystem.getDamagerTeam(le);
        if(team != null) {
            final DungeonEntity dungeonEntity = DungeonMobs.getEntity(le.getUniqueId());
            if(dungeonEntity != null) {
                final Class<DeathComponent> deathComponent = DeathComponent.class;
                if(dungeonEntity.hasComponent(deathComponent)) {
                    dungeonEntity.getComponent(deathComponent).handle(dungeonEntity, Bukkit.getEntity(damageSystem.getDamager(le)));
                }
            }
            final DungeonRoom currentRoom = team.getEncounterData().getCurrentEncounter();
            final Class<KillComponent> killComponent = KillComponent.class;
            if(currentRoom.hasComponent(killComponent)) {
                currentRoom.getComponent(killComponent).handle(team, le);
            }
            if(currentRoom.hasComponent(SpawnComponent.class)) {
                currentRoom.getComponent(SpawnComponent.class).removeEntityFromLocation(le, team);
            }
        }
        damageSystem.clearEntry(le);
    }

}
