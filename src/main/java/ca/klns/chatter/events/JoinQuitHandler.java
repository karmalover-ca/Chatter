package ca.klns.chatter.events;

import ca.klns.chatter.Chatter;
import ca.klns.chatter.utils.ChatComponent;
import ca.klns.chatter.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitHandler implements Listener {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final Chatter core;

    public JoinQuitHandler(Chatter core) {
        this.core = core;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        Player player = event.getPlayer();

        Component prefix = ChatComponent.constructPrefix(player, core);
        Component suffix = ChatComponent.constructSuffix(player, core);
        Component displayName = ChatComponent.constructName(player, core);

        ConfigManager configManager = core.getConfigManager();
        String format = configManager.getJoinMessage();

        Component message = MINI_MESSAGE.deserialize(
                format,
                Placeholder.component("prefix", prefix),
                Placeholder.component("suffix", suffix),
                Placeholder.component("player", displayName)
        );

        Bukkit.broadcast(message);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(null);

        Player player = event.getPlayer();

        Component prefix = ChatComponent.constructPrefix(player, core);
        Component suffix = ChatComponent.constructSuffix(player, core);
        Component displayName = ChatComponent.constructName(player, core);

        ConfigManager configManager = core.getConfigManager();
        String format = configManager.getQuitMessage();

        Component message = MINI_MESSAGE.deserialize(
                format,
                Placeholder.component("prefix", prefix),
                Placeholder.component("suffix", suffix),
                Placeholder.component("player", displayName)
        );

        Bukkit.broadcast(message);
    }
}
