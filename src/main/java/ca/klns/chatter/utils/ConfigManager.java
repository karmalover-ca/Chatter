package ca.klns.chatter.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigManager {

    private JavaPlugin plugin;

    private String chatFormat;
    private String joinMessage;
    private String quitMessage;

    //TODO add variable for different config options

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();

        chatFormat = config.getString("chat-format", "");
        joinMessage = config.getString("join-message", "");
        quitMessage = config.getString("quit-message", "");
    }

    public void reload() {
        plugin.reloadConfig();
        load();
    }

    public String getChatFormat() {
        return chatFormat;
    }

    public String getJoinMessage() {
        return joinMessage;
    }

    public String getQuitMessage() {
        return quitMessage;
    }
}
