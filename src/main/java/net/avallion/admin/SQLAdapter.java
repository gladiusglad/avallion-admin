package net.avallion.admin;

import me.gladgladius.gladlib.SQLData;
import me.gladgladius.gladlib.SQLLink;
import me.gladgladius.gladlib.Time;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLAdapter extends SQLLink {

    private final Time purgeTime;
    private PreparedStatement deathsQuery, addDeath, purgeDeaths;

    SQLAdapter(AvallionAdmin plugin, SQLData data, Time purgeTime, boolean useSSL) {
        super(data, useSSL);
        this.purgeTime = purgeTime;

        createTable("avallionadmin_deaths",
                "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, " +
                        "username VARCHAR(16), " +
                        "time BIGINT, " +
                        "tps REAL, " +
                        "ping INT, " +
                        "reason VARCHAR(256)");

        try {
            deathsQuery = connection.prepareStatement("SELECT time, tps, ping, reason FROM " +
                    "avallionadmin_deaths WHERE username=?;");
            addDeath = connection.prepareStatement("INSERT INTO avallionadmin_deaths (username, time, " +
                    "tps, ping, reason) VALUES (?, ?, ?, ?, ?);");
            purgeDeaths = connection.prepareStatement("DELETE FROM avallionadmin_deaths WHERE " +
                    "time < ? AND id > 0;");
            purgeDeaths();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                purgeDeaths();
            }
        }.runTaskTimerAsynchronously(plugin, 100, 864000);
    }

    void purgeDeaths() {
        try {
            purgeDeaths.setLong(1, System.currentTimeMillis() - purgeTime.toMinutes() * 60000);
            purgeDeaths.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void addDeath(DeathStats deathStats) {
        try {
            addDeath.setString(1, deathStats.username);
            addDeath.setLong(2, deathStats.time);
            addDeath.setFloat(3, deathStats.tps);
            addDeath.setInt(4, deathStats.ping);
            addDeath.setString(5, deathStats.reason);
            addDeath.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    List<DeathStats> getDeaths(String username) {
        try {
            List<DeathStats> deaths = new ArrayList<>();
            deathsQuery.setString(1, username);
            ResultSet result = deathsQuery.executeQuery();
            while (result.next()) {
                deaths.add(new DeathStats(
                        username,
                        result.getLong("time"),
                        result.getFloat("tps"),
                        result.getInt("ping"),
                        result.getString("reason")
                ));
            }
            deaths.sort(Comparator.comparing(d -> d.time, Collections.reverseOrder()));
            return deaths;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
