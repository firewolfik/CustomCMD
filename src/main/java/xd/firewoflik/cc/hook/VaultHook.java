package xd.firewoflik.cc.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private Economy economy = null;
    private boolean available = false;

    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            available = false;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
            available = (economy != null);
        }
    }

    public boolean isAvailable() {
        return available && economy != null;
    }

    public boolean hasMoney(Player player, double amount) {
        if (!isAvailable()) return true;
        return economy.has(player, amount);
    }

    public boolean withdraw(Player player, double amount) {
        if (!isAvailable()) return true;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }
}
