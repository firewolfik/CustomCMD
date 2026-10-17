package xd.firewoflik.cc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import xd.firewoflik.cc.Main;
import xd.firewoflik.cc.util.MessageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public AdminCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("customcommand.admin")) {
            MessageUtil.sendMessage(sender, "&c☠ &fУ вас нет прав");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            MessageUtil.sendMessage(sender, "&8&m----------------------------------------");
            MessageUtil.sendMessage(sender, "&6&lCustomCommand &7v" + plugin.getDescription().getVersion());
            MessageUtil.sendMessage(sender, "&e/" + label + " reload &8- &7Перезагрузить плагин");
            MessageUtil.sendMessage(sender, "&e/" + label + " list &8- &7Список команд");
            MessageUtil.sendMessage(sender, "&8&m----------------------------------------");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
            long start = System.currentTimeMillis();
            plugin.reloadPlugin();
            long time = System.currentTimeMillis() - start;
            int count = plugin.getCommandManager().getLoadedCount();
            MessageUtil.sendMessage(sender, "&aПлагин успешно перезагружен &7(" + time + "ms)&a! Загружено команд: &e" + count);
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            int count = plugin.getCommandManager().getLoadedCount();
            MessageUtil.sendMessage(sender, "&7Загружено команд: &e" + count);
            return true;
        }

        MessageUtil.sendMessage(sender, "&cНеизвестная подкоманда! Используйте &e/" + label + " help");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("customcommand.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> sub = Arrays.asList("reload", "list", "help");
            List<String> matched = new ArrayList<>();
            StringUtil.copyPartialMatches(args[0], sub, matched);
            Collections.sort(matched);
            return matched;
        }

        return Collections.emptyList();
    }
}
