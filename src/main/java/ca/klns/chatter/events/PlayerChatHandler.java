package ca.klns.chatter.events;

import ca.klns.chatter.Chatter;
import dev.vankka.enhancedlegacytext.EnhancedLegacyText;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;

public class PlayerChatHandler implements Listener {
    private static final TextComponent CHAT_SEPARATOR = Component.text(" >> ", NamedTextColor.DARK_GRAY);

    private final Chatter core;

    public PlayerChatHandler(Chatter core) {
        this.core = core;
        Bukkit.getServer().getPluginManager().registerEvents(this, core);
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

        return EnhancedLegacyText.get().parse(prefix);
    }

    private Component constructSuffixComponent(Player player) {
        LuckPerms luckPerms = core.luckPerms;
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);

        ArrayList<SuffixNode> suffixNodes = new ArrayList<>(user.resolveInheritedNodes(NodeType.SUFFIX, QueryOptions.nonContextual()));
        if(suffixNodes.isEmpty()) return Component.empty();

        suffixNodes.sort(Comparator.comparingInt(SuffixNode::getPriority));

        String suffix = suffixNodes.getLast().getKey().split("\\.")[2];

        return EnhancedLegacyText.get().parse(suffix);
    }

    private Component constructNameComponent(Player player) {
        LuckPerms luckPerms = core.luckPerms;

        CachedMetaData metaData = luckPerms.getPlayerAdapter(Player.class).getMetaData(player);

        String color = metaData.getMetaValue("namecolor");

        if(color == null) {
            return Component.text(player.getName());
        }


        return EnhancedLegacyText.get().buildComponent(color + player.getName()).build();
    }

    private void sendChatMessage(Player source, Component message, Audience viewer) {
        Component prefix = constructPrefixComponent(source);
        Component suffix = constructSuffixComponent(source);
        Component displayName = constructNameComponent(source);

        String displayNamePlain = PlainTextComponentSerializer.plainText().serialize(displayName);

        String messagePlain = PlainTextComponentSerializer.plainText().serialize(message);
        if(!message.hasStyling()) {
            message = EnhancedLegacyText.get().parse(messagePlain);
        }

        Component component = Component.text().append(prefix, displayName, suffix, CHAT_SEPARATOR, message).build();

        viewer.sendMessage(component);

        core.getLogger().log(Level.INFO, displayNamePlain + " >> " + messagePlain);
    }
}
