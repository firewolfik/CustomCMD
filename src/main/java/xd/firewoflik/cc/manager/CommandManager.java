package xd.firewoflik.cc.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xd.firewoflik.cc.Main;
import xd.firewoflik.cc.command.DynamicCustomCommand;
import xd.firewoflik.cc.model.CommandType;
import xd.firewoflik.cc.model.CustomCommandModel;
import xd.firewoflik.cc.service.CommandExecutorService;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;

public class CommandManager {

    private final Main plugin;
    private final CommandExecutorService service;
    private final List<DynamicCustomCommand> registered = new ArrayList<>();

    public CommandManager(Main plugin, CommandExecutorService service) {
        this.plugin = plugin;
        this.service = service;
    }

    public void loadCommands() {
        unregisterAll();

        File folder = new File(plugin.getDataFolder(), "commands");
        if (!folder.exists()) {
            folder.mkdirs();
            saveDefaultReadme(folder);
        } else {
            File readme = new File(folder, "README.md");
            if (!readme.exists()) {
                saveDefaultReadme(folder);
            }
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));
        if (files == null || files.length == 0) {
            return;
        }

        CommandMap commandMap = Bukkit.getCommandMap();
        int loaded = 0;

        for (File file : files) {
            try {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                for (String key : yaml.getKeys(false)) {
                    ConfigurationSection section = yaml.getConfigurationSection(key);
                    if (section == null) continue;

                    CustomCommandModel model = parseCommand(key, section);
                    if (model == null) continue;

                    DynamicCustomCommand cmd = new DynamicCustomCommand(model, service);
                    commandMap.register(plugin.getName().toLowerCase(), cmd);
                    registered.add(cmd);
                    loaded++;
                }
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to load command config: " + file.getName(), e);
            }
        }

        syncCommands();
        plugin.getLogger().info("Loaded " + loaded + " custom commands.");
    }

    public void unregisterAll() {
        if (registered.isEmpty()) return;

        CommandMap commandMap = Bukkit.getCommandMap();
        Map<String, Command> known = commandMap.getKnownCommands();
        String prefix = plugin.getName().toLowerCase();

        for (DynamicCustomCommand cmd : registered) {
            cmd.unregister(commandMap);
            known.remove(cmd.getName().toLowerCase());
            known.remove(prefix + ":" + cmd.getName().toLowerCase());

            for (String alias : cmd.getAliases()) {
                known.remove(alias.toLowerCase());
                known.remove(prefix + ":" + alias.toLowerCase());
            }
        }

        registered.clear();
        syncCommands();
    }

    public void syncCommands() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                player.updateCommands();
            } catch (Throwable ignored) {}
        }
    }

    public int getLoadedCount() {
        return registered.size();
    }

    private CustomCommandModel parseCommand(String id, ConfigurationSection sec) {
        String raw = sec.getString("command", id);
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        String name = raw.trim();
        if (name.startsWith("/")) {
            name = name.substring(1).trim();
        }

        CustomCommandModel.Builder builder = new CustomCommandModel.Builder(id, name);

        List<String> aliases = new ArrayList<>();
        if (sec.isList("aliases")) {
            for (String a : sec.getStringList("aliases")) {
                String clean = a.trim();
                if (clean.startsWith("/")) clean = clean.substring(1);
                if (!clean.isEmpty()) aliases.add(clean);
            }
        } else if (sec.isString("alias")) {
            String a = sec.getString("alias", "").trim();
            if (a.startsWith("/")) a = a.substring(1);
            if (!a.isEmpty()) aliases.add(a);
        }
        builder.aliases(aliases);

        String typeStr = sec.getString("type", "TEXT").toUpperCase();
        CommandType type;
        try {
            if (typeStr.equals("BROADCAST_TEXT") || typeStr.equals("BROADCAST_RAW_TEXT")) {
                type = CommandType.BROADCAST;
            } else if (typeStr.equals("RAW_TEXT")) {
                type = CommandType.TEXT;
            } else {
                type = CommandType.valueOf(typeStr);
            }
        } catch (IllegalArgumentException e) {
            type = CommandType.TEXT;
        }
        builder.type(type);

        List<String> lines = new ArrayList<>();
        if (sec.isList("lines")) {
            lines.addAll(sec.getStringList("lines"));
        } else if (sec.isList("text")) {
            lines.addAll(sec.getStringList("text"));
        } else if (sec.isList("runcmd")) {
            lines.addAll(sec.getStringList("runcmd"));
        } else if (sec.isString("lines")) {
            lines.add(sec.getString("lines"));
        } else if (sec.isString("text")) {
            lines.add(sec.getString("text"));
        } else if (sec.isString("runcmd")) {
            lines.add(sec.getString("runcmd"));
        }
        builder.lines(lines);

        String aliasTarget = sec.getString("target", sec.getString("alias", ""));
        builder.aliasTarget(aliasTarget);

        builder.title(sec.getString("title", ""));
        builder.subtitle(sec.getString("subtitle", ""));
        builder.fadeIn(sec.getInt("fade_in", 10));
        builder.stay(sec.getInt("stay", 40));
        builder.fadeOut(sec.getInt("fade_out", 10));
        builder.actionBar(sec.getString("actionbar", sec.getString("action_bar", "")));

        String perm = sec.getString("permission", null);
        if (perm != null && !perm.trim().isEmpty()) {
            builder.permission(perm.trim());
        }

        builder.permissionMessage(sec.getString("permission_message", "&cУ вас нет прав на эту команду!"));
        builder.cooldownSeconds(sec.getInt("cooldown", 0));
        builder.cooldownMessage(sec.getString("cooldown_message", "&cПодождите %time% сек. перед повторным вводом!"));
        builder.minArgs(sec.getInt("min_args", 0));
        builder.minArgsMessage(sec.getString("min_args_message", "&cНедостаточно аргументов!"));
        builder.cost(sec.getDouble("cost", 0.0));
        builder.costMessage(sec.getString("cost_message", "&cНедостаточно средств! Нужно: %cost%$"));

        builder.sound(sec.getString("sound", null));
        builder.soundVolume((float) sec.getDouble("sound_volume", 1.0));
        builder.soundPitch((float) sec.getDouble("sound_pitch", 1.0));
        builder.onlyPlayer(sec.getBoolean("only_player", false));

        if (sec.isList("tab_completer")) {
            builder.tabCompletions(sec.getStringList("tab_completer"));
        } else if (sec.isList("tab")) {
            builder.tabCompletions(sec.getStringList("tab"));
        }

        return builder.build();
    }

    private void saveDefaultReadme(File folder) {
        File file = new File(folder, "README.md");
        try (InputStream in = plugin.getResource("commands/README.md")) {
            if (in != null) {
                Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not save README.md", e);
        }
    }
}
