package ca.klns.chatter.commands;

import ca.klns.chatter.Chatter;
import ca.klns.chatter.utils.ConfigManager;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("chatter")
public class ChatterCommand extends BaseCommand {
    private Chatter core;

    public ChatterCommand(Chatter core) {
        this.core = core;
        core.getCommandManager().registerCommand(this);
    }

    @Subcommand("reload")
    @CommandPermission("chatter.reload")
    public void reload(Player player) {
        ConfigManager configManager = core.getConfigManager();
        configManager.reload();

        player.sendMessage(Component.text("Reloaded Chatter Config", NamedTextColor.AQUA));
    }
}
