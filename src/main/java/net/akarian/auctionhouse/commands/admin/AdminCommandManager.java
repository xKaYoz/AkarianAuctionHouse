package net.akarian.auctionhouse.commands.admin;

import lombok.Getter;
import net.akarian.auctionhouse.commands.admin.subcommands.DatabaseSubCommand;
import net.akarian.auctionhouse.commands.admin.subcommands.HelpSubCommand;
import net.akarian.auctionhouse.commands.admin.subcommands.ReloadSubCommand;
import net.akarian.auctionhouse.commands.admin.subcommands.UpdateSubCommand;
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

        commands.put("help", new HelpSubCommand("help", "auctionhouseadmin.help", "/aha help", "Displays Admin Commands.", "h"));
        commands.put("update", new UpdateSubCommand("update", "auctionhouseadmin.update", "/aha update [enable/disable/toggle]", "Manage the AuctionHouse updater.", "u"));
        commands.put("database", new DatabaseSubCommand("database", "auctionhouseadmin.database", "/aha database", "Manage the AuctionHouse database.", "db"));
        commands.put("reload", new ReloadSubCommand("reload", "auctionhouseadmin.reload", "/aha reload [messages/config]", "Reload the AuctionHouse files.", "rl"));

    }

    public AkarianCommand find(String command) {
        return commands.get(command);
    }

}
