package net.akarian.auctionhouse.commands.admin.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.command.CommandSender;

public class UpdateSubCommand extends AkarianCommand {

    public UpdateSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();

        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("on")) {
                AuctionHouse.getInstance().setUpdate(true);
                AuctionHouse.getInstance().getConfigFile().setUpdates(true);
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                chat.sendMessage(sender, "&aUpdates have been enabled.");
                return;
            } else if (args[1].equalsIgnoreCase("disable") || args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("off")) {
                AuctionHouse.getInstance().setUpdate(false);
                AuctionHouse.getInstance().getConfigFile().setUpdates(false);
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                chat.sendMessage(sender, "&cUpdates have been disabled.");
                return;
            } else if (args[1].equalsIgnoreCase("toggle")) {
                if (AuctionHouse.getInstance().isUpdate()) {
                    AuctionHouse.getInstance().setUpdate(false);
                    AuctionHouse.getInstance().getConfigFile().setUpdates(false);
                    AuctionHouse.getInstance().getConfigFile().saveConfig();
                    chat.sendMessage(sender, "&cUpdates have been disabled.");
                } else {
                    AuctionHouse.getInstance().setUpdate(true);
                    AuctionHouse.getInstance().getConfigFile().setUpdates(true);
                    AuctionHouse.getInstance().getConfigFile().saveConfig();
                    chat.sendMessage(sender, "&aUpdates have been enabled.");
                }
                return;
            }
            chat.usage(sender, "aha update [enable/disable/toggle]");
            return;
        }

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
                chat.sendRawMessage(sender, "&cCurrent Version &8- &7AuctionHouse v" + AuctionHouse.getInstance().getDescription().getVersion());
                chat.sendRawMessage(sender, "");
                chat.sendRawMessage(sender, "&cNewest Version &8- &7AuctionHouse v" + AuctionHouse.getInstance().getUpdateManager().getUpdater().getVersion());
                chat.sendRawMessage(sender, "");
                chat.sendRawMessage(sender, "&7&oYou can download the latest version at https://www.spigotmc.org/resources/akarian-auction-house-1-14-1-19-2.97504/");
                chat.sendRawMessage(sender, "");
                chat.sendRawMessage(sender, "&8&m&l------------------------------------");
                return;
        }
    }
}
