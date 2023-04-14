package net.avallion.admin;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Listeners implements Listener {

    private final AvallionAdmin plugin;

    Listeners(AvallionAdmin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (plugin.deathStats) {
            DeathStats deathStats = new DeathStats(
                    e.getEntity().getName(),
                    System.currentTimeMillis(),
                    (float) Math.min(20, Bukkit.getServer().getTPS()[0]),
                    ((CraftPlayer) e.getEntity()).getHandle().ping,
                    e.getDeathMessage()
            );
            new BukkitRunnable() {
                @Override
                public void run() {
                    deathStats.save();
                }
            }.runTaskAsynchronously(plugin);
        }
    }
}
