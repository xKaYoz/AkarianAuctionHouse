package net.akarian.auctionhouse.commands.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ListSubCommand extends AkarianCommand {
    public ListSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();

        if(!(sender instanceof Player)) {
            chat.sendMessage(sender, AuctionHouse.getInstance().getMessages().getError_player());
            return;
        }

        if(args.length != 2) {
            chat.usage(sender, AuctionHouse.getInstance().getMessages().getList_syntax());
            return;
        }

        Player p = (Player) sender;
        ItemStack itemStack = p.getInventory().getItemInMainHand();

        if(itemStack.getType().isAir()) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getList_item());
            return;
        }

        double price;

        try {
            price = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getList_price());
            return;
        }

        if (price > AuctionHouse.getInstance().getConfig().getDouble("Maximum Listing")) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getMaximumListing()
                    .replace("%price%", AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfig().getDouble("Maximum Listing"))));
            return;
        }

        if (price < AuctionHouse.getInstance().getConfig().getDouble("Minimum Listing")) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getMinimumListing()
                    .replace("%price%", AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfig().getDouble("Minimum Listing"))));
            return;
        }

        if (price <= 0) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getList_price());
            return;
        }

        Listing l = AuctionHouse.getInstance().getListingManager().create(p.getUniqueId(), itemStack, price);

        chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getCreateListing()
                .replace("%item%", chat.formatItem(l.getItemStack())).replace("%price%", chat.formatMoney(l.getPrice())));

        p.getInventory().remove(itemStack);
    }
}
