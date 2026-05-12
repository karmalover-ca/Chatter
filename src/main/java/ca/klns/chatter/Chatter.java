package ca.klns.chatter;

import ca.klns.chatter.events.PlayerChatHandler;
import co.aikar.commands.PaperCommandManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.print.Paper;
import java.util.logging.Level;

public final class Chatter extends JavaPlugin {

    public LuckPerms luckPerms;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        setupACF();
        loadCommands();
        registerListeners();

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if(provider == null) {
            getLogger().log(Level.SEVERE, "LuckPerms not found. Disabling Chatter");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        luckPerms = provider.getProvider();

        getLogger().info("Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down!");
    }

    private void setupACF() {
        commandManager = new PaperCommandManager(this);
    }

    private void loadCommands() {
        // call commands
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerChatHandler(this), this);
    }
}