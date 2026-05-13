package ca.klns.chatter.events;

import ca.klns.chatter.Chatter;
import ca.klns.chatter.utils.ChatComponent;
import ca.klns.chatter.utils.ConfigManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class PlayerChatHandler implements Listener {
    private final Chatter core;

    public PlayerChatHandler(Chatter core) {
        this.core = core;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncChatEvent event) {
        if(!event.isAsynchronous()) return;

        Player player = event.getPlayer();
        if(!player.isOnline()) return;

        event.setCancelled(true);

        sendChatMessage(player, event.message(), Audience.audience(Bukkit.getOnlinePlayers()));
    }

    private void sendChatMessage(Player source, Component message, Audience viewer) {
        Component prefix = ChatComponent.constructPrefix(source, core);
        Component suffix = ChatComponent.constructSuffix(source, core);
        Component displayName = ChatComponent.constructName(source, core);

        String rawMessage = PlainTextComponentSerializer.plainText().serialize(message);

        MiniMessage miniMessage = MiniMessage.miniMessage();

        // Parse player messages
        Component parsedMessage = miniMessage.deserialize(rawMessage);

        ConfigManager configManager = core.getConfigManager();
        String format = configManager.getChatFormat();

        Component finalMessage = miniMessage.deserialize(
                format,
                Placeholder.component("prefix", prefix),
                Placeholder.component("player", displayName),
                Placeholder.component("suffix", suffix),
                Placeholder.component("message", parsedMessage)
        );

        viewer.sendMessage(finalMessage);

        String displayNamePlain = PlainTextComponentSerializer.plainText().serialize(displayName);

        core.getLogger().log(Level.INFO, displayNamePlain + " >> " + rawMessage);
    }
}
