package net.akarian.auctionhouse.commands.admin.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends AkarianCommand {

    public ReloadSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();

        if (args.length == 1) {
            chat.sendMessage(sender, "&7Reloading...");
            AuctionHouse.getInstance().getConfigFile().reloadConfig();
            AuctionHouse.getInstance().getMessages().reloadMessages();
            chat.setPrefix(AuctionHouse.getInstance().getConfigFile().getPrefix());
            chat.sendMessage(sender, "&aReload complete.");
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("messages")) {
                chat.sendMessage(sender, "&7Reloading Messages...");
                AuctionHouse.getInstance().getMessages().reloadMessages();
                chat.sendMessage(sender, "&aReload complete.");
            } else if (args[1].equalsIgnoreCase("config")) {
                chat.sendMessage(sender, "&7Reloading Config...");
                AuctionHouse.getInstance().getConfigFile().reloadConfig();
                chat.sendMessage(sender, "&aReload complete.");
            } else {
                chat.usage(sender, "/aha reload [messages/config]");
            }
        }
    }
}
