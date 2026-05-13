package ca.klns.chatter.utils;

import ca.klns.chatter.Chatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.ChatMetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.ToIntFunction;

public final class ChatComponent {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    public static Component constructPrefix(Player player, Chatter core) {
        User user = getUser(player, core);

        return getHighestPriorityNode(
                user.resolveInheritedNodes(NodeType.PREFIX, QueryOptions.nonContextual()),
                PrefixNode::getPriority
        );
    }

    public static Component constructSuffix(Player player, Chatter core) {
        User user = getUser(player, core);

        return getHighestPriorityNode(
                user.resolveInheritedNodes(NodeType.SUFFIX, QueryOptions.nonContextual()),
                SuffixNode::getPriority
        );
    }

    public static Component constructName(Player player, Chatter core) {
        CachedMetaData metaData = core.luckPerms.getPlayerAdapter(Player.class).getMetaData(player);

        String colour = metaData.getMetaValue("namecolour");

        if (colour == null || colour.isBlank()) {
            return player.displayName();
        }

        String plainName = PLAIN.serialize(player.displayName());

        return MINI_MESSAGE.deserialize(colour + plainName);
    }

    private static User getUser(Player player, Chatter core) {
        return core.luckPerms.getPlayerAdapter(Player.class).getUser(player);
    }

    private static <T extends ChatMetaNode<?, ?>> Component getHighestPriorityNode(Collection<T> nodes, ToIntFunction<T> priorityFunction) {
        return nodes.stream()
                .max(Comparator.comparingInt(priorityFunction))
                .map(node -> MINI_MESSAGE.deserialize(node.getMetaValue()))
                .orElse(Component.empty());
    }
}
