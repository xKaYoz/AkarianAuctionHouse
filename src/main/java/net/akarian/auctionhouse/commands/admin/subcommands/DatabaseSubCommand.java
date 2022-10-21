package net.akarian.auctionhouse.commands.admin.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.database.MainDatabaseGUI;
import net.akarian.auctionhouse.guis.admin.database.transfer.DatabaseTransferStatusGUI;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DatabaseSubCommand extends AkarianCommand {

    public DatabaseSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();

        if (!(sender instanceof Player)) {
            chat.sendMessage(sender, AuctionHouse.getInstance().getMessages().getError_player());
            return;
        }
        Player p = (Player) sender;
        if (AuctionHouse.getInstance().getMySQL().getTransferring() == null) {
            p.openInventory(new MainDatabaseGUI(p).getInventory());
            return;
        }
        if (AuctionHouse.getInstance().getMySQL().getTransferring().toString().equalsIgnoreCase(p.getUniqueId().toString())) {
            p.openInventory(new DatabaseTransferStatusGUI(p).getInventory());
        } else {
            chat.sendMessage(p, "&cThe database transfer has been initialized by " + Bukkit.getOfflinePlayer(AuctionHouse.getInstance().getMySQL().getTransferring()).getName() + ".");
        }
    }
}
