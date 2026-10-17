package xd.firewoflik.cc.util;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class PlaceholderUtil {

    private static final boolean HAS_PAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

    private PlaceholderUtil() {}

    public static String replace(CommandSender sender, String text, String[] args) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        Player player = (sender instanceof Player) ? (Player) sender : null;

        // Player placeholders
        if (player != null) {
            text = text.replace("%player%", player.getName())
                       .replace("$player", player.getName())
                       .replace("%player_uuid%", player.getUniqueId().toString())
                       .replace("$player_uuid", player.getUniqueId().toString())
                       .replace("%world%", player.getWorld().getName());
        } else {
            text = text.replace("%player%", sender.getName())
                       .replace("$player", sender.getName())
                       .replace("%player_uuid%", "console")
                       .replace("$player_uuid", "console")
                       .replace("%world%", "world");
        }

        // Args placeholders
        if (args != null) {
            String allArgs = String.join(" ", args);
            text = text.replace("%args%", allArgs)
                       .replace("$multiargs", allArgs)
                       .replace("%multiargs%", allArgs)
                       .replace("%args_length%", String.valueOf(args.length))
                       .replace("$args_length", String.valueOf(args.length));

            for (int i = 0; i < 20; i++) {
                String val = (i < args.length) ? args[i] : "";
                text = text.replace("%arg" + (i + 1) + "%", val)
                           .replace("$arg" + (i + 1), val);
            }
        } else {
            text = text.replace("%args%", "")
                       .replace("$multiargs", "")
                       .replace("%multiargs%", "")
                       .replace("%args_length%", "0")
                       .replace("$args_length", "0");
        }

        // PlaceholderAPI support
        if (HAS_PAPI && player != null) {
            try {
                text = PlaceholderAPI.setPlaceholders(player, text);
            } catch (Throwable ignored) {
            }
        }

        return text;
    }
}
