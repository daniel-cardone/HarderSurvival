package us.bluescript.hardersurvival;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class EntityExplodeListener implements Listener {
    private final Random random = new Random();
    final int arrowRingSize = 128;
    final int arrowRingCount = 3;
    final int blazeCount = 8;

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntityType() == EntityType.ENDER_CRYSTAL) {
            EnderCrystal enderCrystal = (EnderCrystal)e.getEntity();
            World w = enderCrystal.getWorld();
            Location loc = enderCrystal.getLocation();
            if (w.getEnvironment() != World.Environment.THE_END) return;

            for (int i = 0; i < arrowRingSize; i++) {
                for (int j = 1; j <= arrowRingCount; j++) {
                    Location spawnLoc = loc.clone().add(0, 1.5, 0);
                    double theta = (2 * Math.PI / arrowRingSize) * i;
                    Arrow arrow = (Arrow) w.spawnEntity(spawnLoc, EntityType.ARROW);
                    arrow.setVelocity(new Vector(Math.cos(theta) * 0.66, 0.1 * Math.pow(j, 2), Math.sin(theta) * 0.66));
                }
            }

            for (int i = 0; i < blazeCount; i++) {
                Location offsetLoc = loc.clone().add(random.nextDouble() * 24 - 12, 0, random.nextDouble() * 24 - 12);
                Location spawnLoc = w.getHighestBlockAt(offsetLoc).getLocation().clone().add(0, 1, 0);

                w.spawnEntity(spawnLoc, EntityType.BLAZE);
            }
        }
    }
}
