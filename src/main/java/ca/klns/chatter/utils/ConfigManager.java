package ca.klns.chatter.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigManager {

    private JavaPlugin plugin;

    private String chatFormat;

    //TODO add variable for different config options

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();

        chatFormat = config.getString("chat-format", "");
    }

    public void reload() {
        plugin.reloadConfig();
        load();
    }

    public String getChatFormat() {
        return chatFormat;
    }
}
