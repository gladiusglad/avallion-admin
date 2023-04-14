package net.avallion.admin;

import me.gladgladius.gladlib.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

    private final Message msg;

    Commands(Message msg) {
        this.msg = msg;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equals("deathstats") && sender.hasPermission("avallionadmin.deathstats")) {
            switch (args.length) {
                case 1:
                    return sendDeaths(sender, args[0], 0);
                case 2:
                    try {
                        return sendDeaths(sender, args[0], Integer.parseInt(args[1]) - 1);
                    } catch (NumberFormatException e) {
                        msg.error(sender, "Please enter a number for the <page> argument.");
                    }
                    return false;
                default:
                    msg.error(sender, "Usage: /deathstats <player> [page]");
                    return false;
            }
        }
        return false;
    }
    
    private boolean sendDeaths(CommandSender sender, String name, int page) {
        if (page < 0) {
            msg.error(sender, "Invalid page number. Pages start from one (1).");
            return false;
        }
        msg.send(sender, "Searching " + name + "'s deaths. Please wait...");
        DeathsResult deaths = DeathStats.getDeaths(name, page);
        if (page > 0 && deaths.getDeaths() == null) {
            msg.error(sender, "No results found for that page. The last page is page " + deaths.getPageQty() + ".");
            return false;
        }
        if (deaths.getDeaths() == null || deaths.getDeaths().size() == 0) {
            msg.error(sender, "Player " + name + " not found!");
            return false;
        }
        SimpleDateFormat df = new SimpleDateFormat("dd/MM HH:mm");
        StringBuilder sb = new StringBuilder("&a" + name + "'s deaths");
        sb.append("\n&8&m------------------------------");
        deaths.getDeaths().forEach(d -> {
            sb.append("\n&6[");
            sb.append(df.format(new Date(d.time)));
            sb.append("&6] &c");
            sb.append(d.reason);
            sb.append("\n  &7└ &fTPS: ");
            sb.append((d.tps > 18) ? "&a" : ((d.tps > 14) ? "&e" : "&c"));
            sb.append(String.format("%.2f", d.tps));
            sb.append("     &fPing: ");
            sb.append((d.ping < 150) ? "&a" : ((d.ping < 400) ? "&e" : "&c"));
            sb.append(d.ping);
        });
        sb.append("\n&8&m------------------------------");
        sb.append("\n&fPage &a");
        sb.append(page + 1);
        sb.append("&2/");
        sb.append(deaths.getPageQty());
        sb.append(" &7| &fTo view a page, type &b/deaths ");
        sb.append(name);
        sb.append(" <page>&f.");
        msg.send(sender, sb.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String[] args) {
        if (command.getName().equals("deathstats") && sender.hasPermission("avallionadmin.deathstats")) {
            if (args.length < 2) {
                return null;
            }
            return Collections.emptyList();
        }
        return null;
    }
}
