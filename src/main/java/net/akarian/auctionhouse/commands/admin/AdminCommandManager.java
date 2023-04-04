package net.akarian.auctionhouse.commands.admin;

import lombok.Getter;
import net.akarian.auctionhouse.commands.admin.subcommands.*;
import net.akarian.auctionhouse.utils.AkarianCommand;

import java.util.HashMap;
import java.util.Map;

public class AdminCommandManager {

    @Getter
    private static AdminCommandManager instance;
    @Getter
    private final Map<String, AkarianCommand> commands = new HashMap<>();

    public AdminCommandManager() {

        instance = this;

        commands.put("help", new HelpSubCommand("help", "auctionhouse.admin.help", "/aha help", "Displays Admin Commands.", "h"));
        commands.put("update", new UpdateSubCommand("update", "auctionhouse.admin.update", "/aha update [enable/disable/toggle]", "Manage the AuctionHouse updater.", "u"));
        commands.put("database", new DatabaseSubCommand("database", "auctionhouse.admin.database", "/aha database", "Manage the AuctionHouse database.", "db"));
        commands.put("reload", new ReloadSubCommand("reload", "auctionhouse.admin.reload", "/aha reload [messages/config]", "Reload the AuctionHouse files.", "rl"));
        commands.put("settings", new SettingsSubCommand("settings", "auctionhouse.admin.settings", "/aha settings", "Manage the default player and server settings."));
        commands.put("layout", new LayoutSubCommand("layout", "auctionhouse.admin.edit", "/aha layout", "Manage and edit the Auction House Layouts.", "layouts", "edit"));


    }

    public AkarianCommand find(String command) {
        return commands.get(command);
    }

}
