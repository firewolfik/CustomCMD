package xd.firewoflik.cc.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();
    private static final LegacyComponentSerializer SECTION_SERIALIZER = LegacyComponentSerializer.legacySection();

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private MessageUtil() {}

    /**
     * Converts a string with both MiniMessage tags (<red>, <gradient...>)
     * and legacy color codes (&a, &#ff0000, §a) into a Component.
     */
    public static Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }

        // If it contains MiniMessage tags (<...>)
        if (input.contains("<") && input.contains(">")) {
            // First convert &#RRGGBB to <#RRGGBB>
            Matcher matcher = HEX_COLOR_PATTERN.matcher(input);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, "<#" + matcher.group(1) + ">");
            }
            matcher.appendTail(sb);
            String transformed = sb.toString();

            // Convert legacy & codes to MiniMessage tags if any
            transformed = convertLegacyToMiniMessage(transformed);

            try {
                return MINI_MESSAGE.deserialize(transformed);
            } catch (Exception e) {
                // Fallback to legacy serializer if MiniMessage fails
                return LEGACY_SERIALIZER.deserialize(input);
            }
        }

        // Otherwise deserialize standard legacy format (&a, &#123456, §a)
        if (input.contains("§")) {
            return SECTION_SERIALIZER.deserialize(input);
        }
        return LEGACY_SERIALIZER.deserialize(input);
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(parse(message));
    }

    public static void sendActionBar(Player player, String message) {
        if (message == null || message.isEmpty()) return;
        player.sendActionBar(parse(message));
    }

    private static String convertLegacyToMiniMessage(String text) {
        return text
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&c", "<red>")
                .replace("&d", "<light_purple>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&l", "<bold>")
                .replace("&o", "<italic>")
                .replace("&n", "<underlined>")
                .replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>")
                .replace("&r", "<reset>");
    }

    public static void playSound(Player player, String soundInput, float defaultVol, float defaultPitch) {
        if (soundInput == null || soundInput.trim().isEmpty() || player == null) return;

        String[] parts = soundInput.trim().split("[:;\\s]+");
        String name = parts[0];
        float volume = defaultVol;
        float pitch = defaultPitch;

        if (parts.length > 1) {
            try {
                volume = Float.parseFloat(parts[1]);
            } catch (NumberFormatException ignored) {}
        }
        if (parts.length > 2) {
            try {
                pitch = Float.parseFloat(parts[2]);
            } catch (NumberFormatException ignored) {}
        }

        try {
            Sound bukkitSound = Sound.valueOf(name.toUpperCase());
            player.playSound(player.getLocation(), bukkitSound, volume, pitch);
        } catch (IllegalArgumentException e) {
            // Support custom/minecraft namespaced sounds: "minecraft:entity.player.levelup" or "custom.sound"
            try {
                player.playSound(player.getLocation(), name.toLowerCase(), volume, pitch);
            } catch (Throwable ignored) {}
        }
    }
}
