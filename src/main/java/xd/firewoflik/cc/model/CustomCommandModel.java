package xd.firewoflik.cc.model;

import java.util.ArrayList;
import java.util.List;

public class CustomCommandModel {

    private final String id;
    private final String name;
    private final List<String> aliases;
    private final CommandType type;
    private final List<String> lines;
    private final String aliasTarget;
    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;
    private final String actionBar;
    private final String permission;
    private final String permissionMessage;
    private final int cooldownSeconds;
    private final String cooldownMessage;
    private final int minArgs;
    private final String minArgsMessage;
    private final double cost;
    private final String costMessage;
    private final String sound;
    private final float soundVolume;
    private final float soundPitch;
    private final boolean onlyPlayer;
    private final List<String> tabCompletions;

    public CustomCommandModel(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.aliases = builder.aliases;
        this.type = builder.type;
        this.lines = builder.lines;
        this.aliasTarget = builder.aliasTarget;
        this.title = builder.title;
        this.subtitle = builder.subtitle;
        this.fadeIn = builder.fadeIn;
        this.stay = builder.stay;
        this.fadeOut = builder.fadeOut;
        this.actionBar = builder.actionBar;
        this.permission = builder.permission;
        this.permissionMessage = builder.permissionMessage;
        this.cooldownSeconds = builder.cooldownSeconds;
        this.cooldownMessage = builder.cooldownMessage;
        this.minArgs = builder.minArgs;
        this.minArgsMessage = builder.minArgsMessage;
        this.cost = builder.cost;
        this.costMessage = builder.costMessage;
        this.sound = builder.sound;
        this.soundVolume = builder.soundVolume;
        this.soundPitch = builder.soundPitch;
        this.onlyPlayer = builder.onlyPlayer;
        this.tabCompletions = builder.tabCompletions;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getAliases() { return aliases; }
    public CommandType getType() { return type; }
    public List<String> getLines() { return lines; }
    public String getAliasTarget() { return aliasTarget; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getFadeIn() { return fadeIn; }
    public int getStay() { return stay; }
    public int getFadeOut() { return fadeOut; }
    public String getActionBar() { return actionBar; }
    public String getPermission() { return permission; }
    public String getPermissionMessage() { return permissionMessage; }
    public int getCooldownSeconds() { return cooldownSeconds; }
    public String getCooldownMessage() { return cooldownMessage; }
    public int getMinArgs() { return minArgs; }
    public String getMinArgsMessage() { return minArgsMessage; }
    public double getCost() { return cost; }
    public String getCostMessage() { return costMessage; }
    public String getSound() { return sound; }
    public float getSoundVolume() { return soundVolume; }
    public float getSoundPitch() { return soundPitch; }
    public boolean isOnlyPlayer() { return onlyPlayer; }
    public List<String> getTabCompletions() { return tabCompletions; }

    public static class Builder {
        private final String id;
        private final String name;
        private List<String> aliases = new ArrayList<>();
        private CommandType type = CommandType.TEXT;
        private List<String> lines = new ArrayList<>();
        private String aliasTarget = "";
        private String title = "";
        private String subtitle = "";
        private int fadeIn = 10;
        private int stay = 40;
        private int fadeOut = 10;
        private String actionBar = "";
        private String permission = null;
        private String permissionMessage = "";
        private int cooldownSeconds = 0;
        private String cooldownMessage = "";
        private int minArgs = 0;
        private String minArgsMessage = "";
        private double cost = 0.0;
        private String costMessage = "";
        private String sound = null;
        private float soundVolume = 1.0f;
        private float soundPitch = 1.0f;
        private boolean onlyPlayer = false;
        private List<String> tabCompletions = new ArrayList<>();

        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder aliases(List<String> aliases) {
            if (aliases != null) this.aliases = aliases;
            return this;
        }

        public Builder type(CommandType type) {
            if (type != null) this.type = type;
            return this;
        }

        public Builder lines(List<String> lines) {
            if (lines != null) this.lines = lines;
            return this;
        }

        public Builder aliasTarget(String aliasTarget) {
            this.aliasTarget = aliasTarget != null ? aliasTarget : "";
            return this;
        }

        public Builder title(String title) {
            this.title = title != null ? title : "";
            return this;
        }

        public Builder subtitle(String subtitle) {
            this.subtitle = subtitle != null ? subtitle : "";
            return this;
        }

        public Builder fadeIn(int fadeIn) {
            this.fadeIn = fadeIn;
            return this;
        }

        public Builder stay(int stay) {
            this.stay = stay;
            return this;
        }

        public Builder fadeOut(int fadeOut) {
            this.fadeOut = fadeOut;
            return this;
        }

        public Builder actionBar(String actionBar) {
            this.actionBar = actionBar != null ? actionBar : "";
            return this;
        }

        public Builder permission(String permission) {
            this.permission = (permission != null && !permission.trim().isEmpty()) ? permission.trim() : null;
            return this;
        }

        public Builder permissionMessage(String permissionMessage) {
            if (permissionMessage != null && !permissionMessage.trim().isEmpty()) {
                this.permissionMessage = permissionMessage;
            }
            return this;
        }

        public Builder cooldownSeconds(int cooldownSeconds) {
            this.cooldownSeconds = Math.max(0, cooldownSeconds);
            return this;
        }

        public Builder cooldownMessage(String cooldownMessage) {
            if (cooldownMessage != null && !cooldownMessage.trim().isEmpty()) {
                this.cooldownMessage = cooldownMessage;
            }
            return this;
        }

        public Builder minArgs(int minArgs) {
            this.minArgs = Math.max(0, minArgs);
            return this;
        }

        public Builder minArgsMessage(String minArgsMessage) {
            if (minArgsMessage != null && !minArgsMessage.trim().isEmpty()) {
                this.minArgsMessage = minArgsMessage;
            }
            return this;
        }

        public Builder cost(double cost) {
            this.cost = Math.max(0, cost);
            return this;
        }

        public Builder costMessage(String costMessage) {
            if (costMessage != null && !costMessage.trim().isEmpty()) {
                this.costMessage = costMessage;
            }
            return this;
        }

        public Builder sound(String sound) {
            this.sound = (sound != null && !sound.trim().isEmpty()) ? sound.trim() : null;
            return this;
        }

        public Builder soundVolume(float soundVolume) {
            this.soundVolume = soundVolume;
            return this;
        }

        public Builder soundPitch(float soundPitch) {
            this.soundPitch = soundPitch;
            return this;
        }

        public Builder onlyPlayer(boolean onlyPlayer) {
            this.onlyPlayer = onlyPlayer;
            return this;
        }

        public Builder tabCompletions(List<String> tabCompletions) {
            if (tabCompletions != null) this.tabCompletions = tabCompletions;
            return this;
        }

        public CustomCommandModel build() {
            return new CustomCommandModel(this);
        }
    }
}
