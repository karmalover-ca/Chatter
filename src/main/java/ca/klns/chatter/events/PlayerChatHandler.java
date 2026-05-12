package ca.klns.chatter.events;

import ca.klns.chatter.Chatter;
import ca.klns.chatter.utils.ConfigManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Comparator;
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

    private Component constructPrefixComponent(Player player) {
        LuckPerms luckPerms = core.luckPerms;
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);

        ArrayList<PrefixNode> prefixNodes = new ArrayList<>(user.resolveInheritedNodes(NodeType.PREFIX, QueryOptions.nonContextual()));
        if(prefixNodes.isEmpty()) return Component.empty();

        prefixNodes.sort(Comparator.comparingInt(PrefixNode::getPriority));

        String prefix = prefixNodes.getLast().getKey().split("\\.")[2];

        MiniMessage miniMessage = MiniMessage.miniMessage();

        return miniMessage.deserialize(prefix);
    }

    private Component constructSuffixComponent(Player player) {
        LuckPerms luckPerms = core.luckPerms;
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);

        ArrayList<SuffixNode> suffixNodes = new ArrayList<>(user.resolveInheritedNodes(NodeType.SUFFIX, QueryOptions.nonContextual()));
        if(suffixNodes.isEmpty()) return Component.empty();

        suffixNodes.sort(Comparator.comparingInt(SuffixNode::getPriority));

        String suffix = suffixNodes.getLast().getKey().split("\\.")[2];

        MiniMessage miniMessage = MiniMessage.miniMessage();

        return miniMessage.deserialize(suffix);
    }

    private Component constructNameComponent(Player player) {
        LuckPerms luckPerms = core.luckPerms;

        CachedMetaData metaData = luckPerms.getPlayerAdapter(Player.class).getMetaData(player);

        String color = metaData.getMetaValue("namecolour");

        Component displayName = player.displayName();
        String plainDisplayName = PlainTextComponentSerializer.plainText().serialize(displayName);

        if (color == null || color.isBlank()) {
            return displayName;
        }

        MiniMessage miniMessage = MiniMessage.miniMessage();

        return miniMessage.deserialize(color + plainDisplayName);
    }

    private void sendChatMessage(Player source, Component message, Audience viewer) {
        Component prefix = constructPrefixComponent(source);
        Component suffix = constructSuffixComponent(source);
        Component displayName = constructNameComponent(source);

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
