package us.bluescript.hardersurvival;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class EntityDamageByEntityListener implements Listener {
    Random random = new Random();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntityType() == EntityType.ENDER_DRAGON) {
            EnderDragon dragon = (EnderDragon)e.getEntity();
            World w = dragon.getWorld();
            Location loc = dragon.getLocation();

            if (w.getEnvironment() != World.Environment.THE_END) return;
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (random.nextDouble() < 0.25) {
                        Fireball fireball = (Fireball) w.spawnEntity(loc.clone().add(x, -3, z), EntityType.FIREBALL);
                        fireball.setVelocity(new Vector(0, -2, 0));
                    }
                }
            }

            if (random.nextDouble() < 0.5) {
                for (int i = 0; i < random.nextInt(4); i++) {
                    w.spawnEntity(loc, EntityType.ENDERMAN);
                }
            }
        }
    }
}
