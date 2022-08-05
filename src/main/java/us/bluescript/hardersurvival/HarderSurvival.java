package us.bluescript.hardersurvival;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HarderSurvival extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new CreatureSpawnListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityExplodeListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
