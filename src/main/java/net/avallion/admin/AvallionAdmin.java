package net.avallion.admin;

import me.gladgladius.gladlib.Message;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class AvallionAdmin extends JavaPlugin {

    Message msg;
    boolean deathStats;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        load();
        Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
        getCommand("deathstats").setExecutor(new Commands(msg));
    }

    @Override
    public void onDisable() {
        DeathStats.closeSQL();
    }

    public void load() {
        saveDefaultConfig();
        reloadConfig();
        FileConfiguration config = getConfig();

        msg = new Message("[AvallionAdmin] ", config.getString("prefix"));
        deathStats = config.getBoolean("death-stats");

        DeathStats.load(this, config);
    }
}
