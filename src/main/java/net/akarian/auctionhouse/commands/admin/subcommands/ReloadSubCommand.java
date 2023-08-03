package net.akarian.auctionhouse.commands.admin.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.MessageType;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends AkarianCommand {

    public ReloadSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();

        if (args.length == 1) {
            chat.sendMessage(sender, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_RELOADSTART));
            AuctionHouse.getInstance().getListingManager().cancelExpireTimer();
            AuctionHouse.getInstance().getListingManager().cancelRefreshTimer();
            AuctionHouse.getInstance().getConfigFile().reloadConfig();
            chat.setPrefix(AuctionHouse.getInstance().getConfigFile().getPrefix());
            AuctionHouse.getInstance().getListingManager().startAuctionHouseRefresh();
            AuctionHouse.getInstance().getListingManager().startExpireCheck();
            chat.sendMessage(sender, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_RELOADCOMPLETE));
        } else if (args.length == 2) {
            if (args[1].equalsIgnoreCase("messages")) {
                chat.sendMessage(sender, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_RELOADSTART));
                chat.sendMessage(sender, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_RELOADCOMPLETE));
            } else if (args[1].equalsIgnoreCase("config")) {
                chat.sendMessage(sender, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_RELOADSTART));
                AuctionHouse.getInstance().getConfigFile().reloadConfig();
                chat.sendMessage(sender, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_RELOADCOMPLETE));
            } else {
                chat.usage(sender, "/aha reload [messages/config]");
            }
        }
    }
}
