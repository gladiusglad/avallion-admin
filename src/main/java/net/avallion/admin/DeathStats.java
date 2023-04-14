package net.avallion.admin;

import me.gladgladius.gladlib.SQLData;
import me.gladgladius.gladlib.Time;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeathStats {

    private static SQLAdapter sql;
    public final long time;
    public final float tps;
    public final int ping;
    public final String username, reason;

    static void load(@NotNull AvallionAdmin plugin, @NotNull ConfigurationSection config) {
        if (sql != null) {
            closeSQL();
        }
        //noinspection ConstantConditions
        sql = new SQLAdapter(
                plugin,
                new SQLData(config.getString("mysql.host"),
                        config.getString("mysql.database"),
                        config.getString("mysql.username"),
                        config.getString("mysql.password"),
                        config.getInt("mysql.port")),
                new Time(config.getString("death-stats-purge")),
                config.getBoolean("mysql.use-ssl")
        );
    }

    static void closeSQL() {
        sql.close();
    }

    public DeathStats(String username, long time, float tps, int ping, String reason) {
        this.username = username;
        this.time = time;
        this.tps = tps;
        this.ping = ping;
        this.reason = reason;
    }

    public void save() {
        sql.addDeath(this);
    }

    public static List<DeathStats> getDeaths(String username) {
        return sql.getDeaths(username);
    }

    public static DeathsResult getDeaths(String username, int page) {
        int PAGE_SIZE = 5;
        List<DeathStats> deaths = getDeaths(username);

        if (deaths.size() == 0) return new DeathsResult(deaths, 0);
        int pageQty = (deaths.size() + PAGE_SIZE - 1) / PAGE_SIZE;
        if (page * PAGE_SIZE >= deaths.size()) return new DeathsResult(null, pageQty);

        return new DeathsResult(deaths.subList(page * PAGE_SIZE, Math.min((page + 1) * PAGE_SIZE, deaths.size())),
                pageQty);
    }
}
