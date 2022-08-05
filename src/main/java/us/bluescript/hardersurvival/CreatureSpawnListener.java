package us.bluescript.hardersurvival;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CreatureSpawnListener implements Listener {
    private final Random random = new Random();
    private final List<EntityType> armorMobs = Arrays.asList(
            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.SKELETON,
            EntityType.HUSK, EntityType.STRAY, EntityType.ZOMBIFIED_PIGLIN,
            EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.WITHER_SKELETON
    );

    private final Material[][] armorProgression = {
            { Material.AIR, Material.LEATHER_HELMET, Material.GOLDEN_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET },
            { Material.AIR, Material.LEATHER_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE },
            { Material.AIR, Material.LEATHER_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS },
            { Material.AIR, Material.LEATHER_BOOTS, Material.GOLDEN_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS }
    };

    private final Enchantment[] armorEnchantments = {
            Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FIRE,
            Enchantment.PROTECTION_PROJECTILE, Enchantment.DURABILITY, Enchantment.THORNS
    };

    private final List<EntityType> weaponMobs = Arrays.asList(
            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK,
            EntityType.ZOMBIFIED_PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.WITHER_SKELETON,
            EntityType.DROWNED, EntityType.VEX
    );

    private final Material[] weapons = {
            Material.WOODEN_SWORD, Material.GOLDEN_SWORD, Material.STONE_SWORD,
            Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.WOODEN_AXE,
            Material.GOLDEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
            Material.DIAMOND_AXE
    };

    private final Enchantment[] weaponEnchantments = {
            Enchantment.DAMAGE_ALL, Enchantment.DURABILITY, Enchantment.DAMAGE_UNDEAD,
            Enchantment.DAMAGE_ARTHROPODS
    };

    private final List<EntityType> resizableMobs = Arrays.asList(
            EntityType.SLIME, EntityType.MAGMA_CUBE, EntityType.PHANTOM
    );

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        handleArmor(e);
        handleWeapons(e);
        handleSizes(e);
        handleEnd(e);
    }

    private void handleArmor(CreatureSpawnEvent e) {
        if (armorMobs.contains(e.getEntityType())) {
            int[] indices = new int[armorProgression.length];
            Arrays.fill(indices, 0);

            for (int i = 0; i < armorProgression.length; i++) {
                Material[] armorType = armorProgression[i];
                for (int j = 0; j < armorType.length - 1; j++) {
                    if (random.nextDouble() < 0.33) {
                        indices[i]++;
                    } else {
                        break;
                    }
                }
            }

            LivingEntity mob = e.getEntity();
            EntityEquipment equipment = mob.getEquipment();
            if (equipment == null) return;

            ItemStack[] newArmor = {
                    equipment.getBoots(),
                    equipment.getLeggings(),
                    equipment.getChestplate(),
                    equipment.getHelmet()
            };

            for (int i = 0; i < newArmor.length; i++) {
                if (newArmor[3 - i].getType().isAir()) {
                    newArmor[3 - i] = new ItemStack(armorProgression[i][indices[i]], 1);
                }
            }

            for (ItemStack armorPiece : newArmor) {
                for (Enchantment ench : armorEnchantments) {
                    if (random.nextDouble() < 0.125) {
                        try {
                            armorPiece.addEnchantment(ench, random.nextInt(3) + 1);
                        } catch (IllegalArgumentException ignored) {}
                    }
                }
            }

            equipment.setArmorContents(newArmor);
        }
    }

    private void handleWeapons(CreatureSpawnEvent e) {
        if (weaponMobs.contains(e.getEntityType())) {
            if (random.nextDouble() < 0.25) {
                LivingEntity mob = e.getEntity();
                EntityEquipment equipment = mob.getEquipment();
                if (equipment == null) return;

                Material weaponMaterial = weapons[random.nextInt(weapons.length)];
                ItemStack weapon = new ItemStack(weaponMaterial, 1);

                for (Enchantment ench : weaponEnchantments) {
                    if (random.nextDouble() < 0.125) {
                        try {
                            weapon.addEnchantment(ench, random.nextInt(3) + 1);
                        } catch (IllegalArgumentException ignored) {}
                    }
                }

                equipment.setItemInMainHand(weapon);
            }
        }
    }

    private void handleSizes(CreatureSpawnEvent e) {
        if (resizableMobs.contains(e.getEntityType())) {
            LivingEntity mob = e.getEntity();
            if (mob instanceof Slime) {
                if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) return;
                Slime slime = (Slime)e.getEntity();
                slime.setSize(random.nextInt(8));
            } else {
                Phantom phantom = (Phantom)e.getEntity();
                phantom.setSize(random.nextInt(10));
            }
        }
    }

    private void handleEnd(CreatureSpawnEvent e) {
        LivingEntity mob = e.getEntity();
        World w = mob.getWorld();
        Location loc = mob.getLocation();
        if (w.getEnvironment() == World.Environment.THE_END) {
            if (mob.getType() == EntityType.ENDER_DRAGON) {
                mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(300);
                mob.setHealth(300);
                return;
            }

            if (mob.getType() != EntityType.ENDERMAN) return;
            if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;

            if (random.nextDouble() < 0.5) {
                w.spawnEntity(loc, EntityType.STRAY);
            }

            if (random.nextDouble() < 0.5) {
                e.setCancelled(true);
            }
        }
    }
}
