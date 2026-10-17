package xd.firewoflik.cc.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import xd.firewoflik.cc.model.CustomCommandModel;
import xd.firewoflik.cc.service.CommandExecutorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DynamicCustomCommand extends Command {

    private final CustomCommandModel model;
    private final CommandExecutorService service;

    public DynamicCustomCommand(CustomCommandModel model, CommandExecutorService service) {
        super(model.getName());
        this.model = model;
        this.service = service;

        if (model.getAliases() != null && !model.getAliases().isEmpty()) {
            setAliases(model.getAliases());
        }
        if (model.getPermission() != null) {
            setPermission(model.getPermission());
        }
        setDescription("Custom command /" + model.getName());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return service.execute(sender, model, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (model.getTabCompletions() == null || model.getTabCompletions().isEmpty()) {
            return Collections.emptyList();
        }

        String currentArg = (args.length > 0) ? args[args.length - 1] : "";
        List<String> matched = new ArrayList<>();
        StringUtil.copyPartialMatches(currentArg, model.getTabCompletions(), matched);
        Collections.sort(matched);
        return matched;
    }

    public CustomCommandModel getModel() {
        return model;
    }
}
