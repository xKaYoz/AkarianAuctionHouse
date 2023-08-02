package net.akarian.auctionhouse.commands.main.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.ExpireReclaimGUI;
import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpiredSubCommand extends AkarianCommand {

    public ExpiredSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();

        if (!(sender instanceof Player)) {
            chat.sendMessage(sender, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PLAYER));
            return;
        }

        Player p = (Player) sender;
        p.openInventory(new ExpireReclaimGUI(p, new AuctionHouseGUI(p, SortType.TIME_LEFT, true, 1), 1).getInventory());
    }
}
