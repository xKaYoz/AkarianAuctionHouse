package net.akarian.auctionhouse.commands.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.guis.admin.AuctionHouseAdminGUI;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminSubCommand extends AkarianCommand {

    public AdminSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();

        if (args.length == 1) {
            helpMenu(sender);
            return;
        }

        if (args[1].equalsIgnoreCase("update") && sender.hasPermission("auctionhouse.admin.update")) {
            switch (AuctionHouse.getInstance().getUpdateManager().isUpdate()) {
                case -1:
                    chat.sendRawMessage(sender, "&8&m&l------------------------------------");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "  &c&lAuctionHouse Updater");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "&cUpdates are disabled.");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "&8&m&l------------------------------------");
                    return;
                case 0:
                    chat.sendRawMessage(sender, "&8&m&l------------------------------------");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "  &c&lAuctionHouse Updater");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "&cError whilst trying to fetch update. Bad ID.");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "&8&m&l------------------------------------");
                    return;
                case 1:
                    chat.sendRawMessage(sender, "&8&m&l------------------------------------");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "  &c&lAuctionHouse Updater");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "&aThe plugin is up to date!");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "&8&m&l------------------------------------");
                    return;
                case 2:
                    chat.sendRawMessage(sender, "&8&m&l------------------------------------");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "  &c&lAuctionHouse Updater");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "&cCurrent Version &8- &7AkarianAuctionHouse v" + AuctionHouse.getInstance().getDescription().getVersion());
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "&cNewest Version &8- &7AkarianAuctionHouse v" + AuctionHouse.getInstance().getUpdateManager().getUpdater().getVersion());
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "&7&oYou can download the latest version at https://www.spigotmc.org/resources/akarian-auction-house-1-17-x.97504/");
                    chat.sendRawMessage(sender, "");
                    chat.sendRawMessage(sender, "&8&m&l------------------------------------");
                    return;
            }
        } else if (args[1].equalsIgnoreCase("reload") && sender.hasPermission("auctionhouse.admin.reload")) {

            chat.sendMessage(sender, "&7Reloading...");
            AuctionHouse.getInstance().reloadConfig();
            AuctionHouse.getInstance().getMessages().reloadMessages();
            chat.setPrefix(AuctionHouse.getInstance().getConfig().getString("Prefix"));
            chat.sendMessage(sender, "&aReload complete.");

        } else if (args[1].equalsIgnoreCase("menu") && sender.hasPermission("auctionhouse.admin.menu")) {

            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.openInventory(new AuctionHouseAdminGUI(p, SortType.TIME_LEFT, true, 1).getInventory());
            } else {
                chat.sendMessage(sender, AuctionHouse.getInstance().getMessages().getError_player());
            }

        } else {
            helpMenu(sender);
        }

    }

    private void helpMenu(CommandSender sender) {
        Chat chat = AuctionHouse.getInstance().getChat();
        chat.sendRawMessage(sender, "&8&m----------------------------------------");
        chat.sendRawMessage(sender, "&c&l  AuctionHouse Admin Help Menu");
        chat.sendRawMessage(sender, "");
        chat.sendRawMessage(sender, "&c/ah admin help &8- &7Open this help menu.");
        chat.sendRawMessage(sender, "&c/ah admin reload &8- &7Reload plugin files.");
        chat.sendRawMessage(sender, "&c/ah admin update &8- &7Check if there is an update.");
        chat.sendRawMessage(sender, "&c/ah admin menu &8- &7Open up the AuctionHouse Admin Menu");
        chat.sendRawMessage(sender, "&8&m----------------------------------------");
    }

}
