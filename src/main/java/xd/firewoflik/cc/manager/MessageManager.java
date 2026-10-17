package xd.firewoflik.cc.manager;

import org.bukkit.configuration.file.FileConfiguration;
import xd.firewoflik.cc.Main;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    private final Main plugin;

    public MessageManager(Main plugin) {
        this.plugin = plugin;
    }

    public String get(String key) {
        FileConfiguration config = plugin.getConfig();
        return config.getString("messages." + key, "");
    }

    public List<String> getList(String key) {
        FileConfiguration config = plugin.getConfig();
        if (config.isList("messages." + key)) {
            return config.getStringList("messages." + key);
        }
        String single = config.getString("messages." + key);
        if (single != null && !single.isEmpty()) {
            List<String> list = new ArrayList<>();
            list.add(single);
            return list;
        }
        return new ArrayList<>();
    }
}
