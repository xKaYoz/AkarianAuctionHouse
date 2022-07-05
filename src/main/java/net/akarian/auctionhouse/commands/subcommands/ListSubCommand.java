package net.akarian.auctionhouse.commands.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.concurrent.atomic.AtomicInteger;

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

        //Check if the player is holding an item
        if (itemStack.getType().isAir()) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getList_item());
            return;
        }

        //Check max listings
        AtomicInteger maxListings = new AtomicInteger();
        p.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).map(String::toLowerCase).filter(value -> value.startsWith("auctionhouse.listings.")).map(value -> value.replace("auctionhouse.listings.", "")).forEach(value -> {
            //Player has unlimited listings
            if (value.equalsIgnoreCase("*")) {
                maxListings.set(-1);
            }

            //Get amount of max listings
            try {
                int amount = Integer.parseInt(value);

                if (amount > maxListings.get())
                    maxListings.set(amount);
            } catch (NumberFormatException ignored) {
            }
        });

        if (!p.isOp() && maxListings.get() > 0 && AuctionHouse.getInstance().getListingManager().getListings(p.getUniqueId()).size() >= maxListings.get()) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getMaxListings().replace("%max%", maxListings.get() + ""));
            return;
        }

        //Check Cooldowns
        if (AuctionHouse.getInstance().getConfigFile().getListingDelay() > 0 && !p.hasPermission("auctionhouse.delay.bypass")) {
            if (AuctionHouse.getInstance().getCooldownManager().isCooldown(p)) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getCooldownTimer().replace("%time%", chat.formatTime(AuctionHouse.getInstance().getCooldownManager().getRemaining(p))));
                return;
            }
        }

        //Check given price
        double price;
        try {
            price = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getList_price());
            return;
        }

        if (price > AuctionHouse.getInstance().getConfigFile().getMaxListing()) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getMaximumListing()
                    .replace("%price%", AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMaxListing())));
            return;
        }

        if (price < AuctionHouse.getInstance().getConfigFile().getMinListing()) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getMinimumListing()
                    .replace("%price%", AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMinListing())));
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
        AuctionHouse.getInstance().getCooldownManager().setCooldown(p);
    }
}
