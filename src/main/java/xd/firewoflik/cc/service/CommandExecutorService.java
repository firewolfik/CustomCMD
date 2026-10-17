package xd.firewoflik.cc.service;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xd.firewoflik.cc.Main;
import xd.firewoflik.cc.hook.VaultHook;
import xd.firewoflik.cc.manager.CooldownManager;
import xd.firewoflik.cc.model.CustomCommandModel;
import xd.firewoflik.cc.util.MessageUtil;
import xd.firewoflik.cc.util.PlaceholderUtil;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandExecutorService {

    private static final Pattern DELAY_PATTERN = Pattern.compile("(?i)^\\$(?:delay[:<](\\d+)[>$]?|delay\\$)\\s*");
    private static final Pattern SOUND_PATTERN = Pattern.compile("(?i)^(?<!\\\\)(?:\\$sound:([^$]+)\\$|<sound:([^>]+)>)\\s*");

    private final Main plugin;
    private final CooldownManager cooldownManager;
    private final VaultHook vaultHook;

    public CommandExecutorService(Main plugin, CooldownManager cooldownManager, VaultHook vaultHook) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.vaultHook = vaultHook;
    }

    public boolean execute(CommandSender sender, CustomCommandModel cmd, String[] args) {
        Player player = (sender instanceof Player p) ? p : null;

        if (cmd.isOnlyPlayer() && player == null) {
            MessageUtil.sendMessage(sender, "&cКоманда доступна только игрокам!");
            return true;
        }

        if (cmd.getPermission() != null && !sender.hasPermission(cmd.getPermission())) {
            MessageUtil.sendMessage(sender, cmd.getPermissionMessage());
            return true;
        }

        if (args.length < cmd.getMinArgs()) {
            String msg = PlaceholderUtil.replace(sender, cmd.getMinArgsMessage(), args);
            MessageUtil.sendMessage(sender, msg);
            return true;
        }

        if (player != null) {
            if (cmd.getCooldownSeconds() > 0 && !player.hasPermission("customcommand.cooldown.bypass")) {
                if (cooldownManager.hasCooldown(cmd.getId(), player)) {
                    long remaining = cooldownManager.getRemainingSeconds(cmd.getId(), player);
                    String msg = cmd.getCooldownMessage().replace("%time%", String.valueOf(remaining));
                    MessageUtil.sendMessage(player, msg);
                    return true;
                }
            }

            if (cmd.getCost() > 0.0) {
                if (!vaultHook.hasMoney(player, cmd.getCost())) {
                    String msg = cmd.getCostMessage().replace("%cost%", String.valueOf(cmd.getCost()));
                    MessageUtil.sendMessage(player, msg);
                    return true;
                }
                if (!vaultHook.withdraw(player, cmd.getCost())) {
                    MessageUtil.sendMessage(player, "&cОшибка списания средств!");
                    return true;
                }
            }

            if (cmd.getSound() != null) {
                MessageUtil.playSound(player, cmd.getSound(), cmd.getSoundVolume(), cmd.getSoundPitch());
            }
        }

        dispatchByType(sender, player, cmd, args);

        if (player != null && cmd.getCooldownSeconds() > 0 && !player.hasPermission("customcommand.cooldown.bypass")) {
            cooldownManager.setCooldown(cmd.getId(), player, cmd.getCooldownSeconds());
        }

        return true;
    }

    private void dispatchByType(CommandSender sender, Player player, CustomCommandModel cmd, String[] args) {
        switch (cmd.getType()) {
            case TEXT -> executeTextLines(sender, cmd.getLines(), args, false);
            case BROADCAST -> executeTextLines(sender, cmd.getLines(), args, true);
            case ACTION_BAR -> {
                if (player != null) {
                    String parsed = PlaceholderUtil.replace(player, cmd.getActionBar(), args);
                    MessageUtil.sendActionBar(player, parsed);
                }
            }
            case TITLE -> {
                if (player != null) {
                    Component title = MessageUtil.parse(PlaceholderUtil.replace(player, cmd.getTitle(), args));
                    Component sub = MessageUtil.parse(PlaceholderUtil.replace(player, cmd.getSubtitle(), args));
                    Title.Times times = Title.Times.times(
                            Duration.ofMillis(cmd.getFadeIn() * 50L),
                            Duration.ofMillis(cmd.getStay() * 50L),
                            Duration.ofMillis(cmd.getFadeOut() * 50L)
                    );
                    player.showTitle(Title.title(title, sub, times));
                }
            }
            case ALIAS -> {
                String target = cmd.getAliasTarget();
                if (!target.isEmpty()) {
                    if (target.startsWith("/")) target = target.substring(1);
                    if (args.length > 0) target += " " + String.join(" ", args);
                    target = PlaceholderUtil.replace(sender, target, args);
                    runCommand(sender, target, false);
                }
            }
            case RUN_COMMAND -> executeCommandLines(sender, cmd.getLines(), args, false);
            case RUN_CONSOLE -> executeCommandLines(sender, cmd.getLines(), args, true);
        }
    }

    private void executeTextLines(CommandSender sender, List<String> lines, String[] args, boolean broadcast) {
        long accumulatedTicks = 0;
        Player player = (sender instanceof Player p) ? p : null;

        for (String raw : lines) {
            LineData line = parseLine(raw);
            accumulatedTicks += (line.delaySec * 20L);

            String text = PlaceholderUtil.replace(sender, line.text, args);

            Runnable task = () -> {
                if (line.sound != null && player != null) {
                    MessageUtil.playSound(player, line.sound, 1.0f, 1.0f);
                }
                if (!text.isEmpty()) {
                    if (broadcast) {
                        Bukkit.broadcast(MessageUtil.parse(text));
                    } else {
                        MessageUtil.sendMessage(sender, text);
                    }
                }
            };

            if (accumulatedTicks == 0) {
                task.run();
            } else {
                Bukkit.getScheduler().runTaskLater(plugin, task, accumulatedTicks);
            }
        }
    }

    private void executeCommandLines(CommandSender sender, List<String> lines, String[] args, boolean asConsole) {
        long accumulatedTicks = 0;
        Player player = (sender instanceof Player p) ? p : null;

        for (String raw : lines) {
            LineData line = parseLine(raw);
            accumulatedTicks += (line.delaySec * 20L);

            String cmd = PlaceholderUtil.replace(sender, line.text, args);

            Runnable task = () -> {
                if (line.sound != null && player != null) {
                    MessageUtil.playSound(player, line.sound, 1.0f, 1.0f);
                }
                runCommand(sender, cmd, asConsole);
            };

            if (accumulatedTicks == 0) {
                task.run();
            } else {
                Bukkit.getScheduler().runTaskLater(plugin, task, accumulatedTicks);
            }
        }
    }

    private void runCommand(CommandSender sender, String command, boolean asConsole) {
        if (command == null || command.isEmpty()) return;
        String sanitized = command.replace("\n", "").replace("\r", "").trim();
        if (sanitized.startsWith("/")) sanitized = sanitized.substring(1);
        if (sanitized.isEmpty()) return;

        if (asConsole) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sanitized);
        } else if (sender instanceof Player player) {
            player.performCommand(sanitized);
        } else {
            Bukkit.dispatchCommand(sender, sanitized);
        }
    }

    private LineData parseLine(String line) {
        if (line == null) return new LineData(0, null, "");

        int delay = 0;
        String sound = null;
        String content = line;

        Matcher dm = DELAY_PATTERN.matcher(content);
        if (dm.find()) {
            String group = dm.group(1);
            delay = (group != null) ? Integer.parseInt(group) : 1;
            content = content.substring(dm.end());
        }

        Matcher sm = SOUND_PATTERN.matcher(content);
        if (sm.find()) {
            sound = (sm.group(1) != null) ? sm.group(1) : sm.group(2);
            content = content.substring(sm.end());
        }

        return new LineData(delay, sound, content);
    }

    private record LineData(int delaySec, String sound, String text) {}
}
