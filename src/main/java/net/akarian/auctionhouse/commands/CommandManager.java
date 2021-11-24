package net.akarian.auctionhouse.commands;

import lombok.Getter;
import net.akarian.auctionhouse.commands.subcommands.*;
import net.akarian.auctionhouse.utils.AkarianCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    @Getter
    private static CommandManager instance;
    @Getter
    private final Map<String, AkarianCommand> commands = new HashMap<>();

    public CommandManager() {

        instance = this;

        commands.put("help", new HelpSubCommand("help", "auctionhouse.help", "/auctionhouse help", "Displays useful information about the plugin."));
        commands.put("list", new ListSubCommand("list", "auctionhouse.list", "/auctionhouse list", "Create an AuctionHouse listing", "create", "c", "l", "sell"));
        commands.put("admin", new AdminSubCommand("admin", "auctionhouse.admin", "/auctionhouse admin", "Open the AuctionHouse Admin Menu"));
        commands.put("search", new SearchSubCommand("search", "auctionhouse.search", "/auctionhouse search <query>", "Open the AuctionHouse Menu with a pre defined search."));
        commands.put("expired", new ExpiredSubCommand("expired", "auctionhouse.expired", "/auctionhouse expired", "Open your expired listings."));

    }

    public AkarianCommand find(String command) {
        return commands.get(command);
    }


}
