package ca.klns.chatter;

import ca.klns.chatter.events.PlayerChatHandler;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Chatter extends JavaPlugin {

    public LuckPerms luckPerms;

    @Override
    public void onEnable() {
        loadModules();

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

    private void loadModules() {
        // call commands, and listeners
        new PlayerChatHandler(this);
    }
}