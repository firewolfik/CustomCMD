package xd.firewoflik.cc;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import xd.firewoflik.cc.command.AdminCommand;
import xd.firewoflik.cc.hook.VaultHook;
import xd.firewoflik.cc.manager.CommandManager;
import xd.firewoflik.cc.manager.CooldownManager;
import xd.firewoflik.cc.manager.MessageManager;
import xd.firewoflik.cc.service.CommandExecutorService;

public final class Main extends JavaPlugin {

    private static Main instance;
    private MessageManager messageManager;
    private VaultHook vaultHook;
    private CooldownManager cooldownManager;
    private CommandExecutorService executorService;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.messageManager = new MessageManager(this);
        this.vaultHook = new VaultHook();
        this.vaultHook.init();

        this.cooldownManager = new CooldownManager();
        this.executorService = new CommandExecutorService(this, cooldownManager, vaultHook);
        this.commandManager = new CommandManager(this, executorService);

        AdminCommand adminCmd = new AdminCommand(this);
        PluginCommand cmd = getCommand("customcommand");
        if (cmd != null) {
            cmd.setExecutor(adminCmd);
            cmd.setTabCompleter(adminCmd);
        }

        this.commandManager.loadCommands();
    }

    @Override
    public void onDisable() {
        if (this.commandManager != null) {
            this.commandManager.unregisterAll();
        }
        if (this.cooldownManager != null) {
            this.cooldownManager.clear();
        }
    }

    public void reloadPlugin() {
        reloadConfig();
        if (this.vaultHook != null) {
            this.vaultHook.init();
        }
        if (this.cooldownManager != null) {
            this.cooldownManager.clear();
        }
        if (this.commandManager != null) {
            this.commandManager.loadCommands();
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }
}
