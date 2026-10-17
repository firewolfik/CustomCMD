package xd.firewoflik.cc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import xd.firewoflik.cc.Main;
import xd.firewoflik.cc.manager.MessageManager;
import xd.firewoflik.cc.model.CustomCommandModel;
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
        MessageManager mm = plugin.getMessageManager();

        if (!sender.hasPermission("customcommand.admin")) {
            MessageUtil.sendMessage(sender, mm.get("no_permission"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            for (String line : mm.getList("help")) {
                MessageUtil.sendMessage(sender, line.replace("%label%", label));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
            long start = System.currentTimeMillis();
            plugin.reloadPlugin();
            long time = System.currentTimeMillis() - start;
            int count = plugin.getCommandManager().getLoadedCount();
            String msg = mm.get("reload_success")
                    .replace("%time%", String.valueOf(time))
                    .replace("%count%", String.valueOf(count))
                    .replace("%label%", label);
            MessageUtil.sendMessage(sender, msg);
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            List<DynamicCustomCommand> commands = plugin.getCommandManager().getRegisteredCommands();
            int count = commands.size();

            if (count == 0) {
                MessageUtil.sendMessage(sender, mm.get("list_empty"));
                return true;
            }

            String header = mm.get("list_header").replace("%count%", String.valueOf(count));
            MessageUtil.sendMessage(sender, header);

            String format = mm.get("list_format");
            for (DynamicCustomCommand cmd : commands) {
                CustomCommandModel model = cmd.getModel();
                String aliasesStr = model.getAliases().isEmpty() ? "-" : String.join(", ", model.getAliases());
                String line = format
                        .replace("%command%", model.getName())
                        .replace("%type%", model.getType().name())
                        .replace("%aliases%", aliasesStr)
                        .replace("%permission%", model.getPermission() == null ? "-" : model.getPermission());
                MessageUtil.sendMessage(sender, line);
            }

            String footer = mm.get("list_footer").replace("%count%", String.valueOf(count));
            if (!footer.isEmpty()) {
                MessageUtil.sendMessage(sender, footer);
            }
            return true;
        }

        MessageUtil.sendMessage(sender, mm.get("unknown_subcommand").replace("%label%", label));
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
