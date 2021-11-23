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
            chat.sendMessage(sender, "&cYou must be a player to execute this command.");
            return;
        }

        if(args.length != 2) {
            chat.usage(sender, "/ah list <price>");
            return;
        }

        Player p = (Player) sender;
        ItemStack itemStack = p.getInventory().getItemInMainHand();

        if(itemStack.getType().isAir()) {
            chat.sendMessage(p, "&cYou must be holding an item");
        }

        double price;

        try {
            price = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            chat.sendMessage(p, "&cThe 2nd argument must be the price.");
            return;
        }

        if (price <= 0) {
            chat.sendMessage(p, "&cThe price must be above $0.");
            return;
        }

        Listing l = AuctionHouse.getInstance().getListingManager().create(p.getUniqueId(), itemStack, price);

        chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getCreateListing()
                .replace("%item%", chat.formatItem(l.getItemStack())).replace("%price%", chat.formatMoney(l.getPrice())));

        p.getInventory().remove(itemStack);
    }
}
