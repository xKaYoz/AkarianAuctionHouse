package net.akarian.auctionhouse.commands.main.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.ListingMainGUI;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.InventoryHandler;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.GameMode;
import org.bukkit.Material;
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

        if (!(sender instanceof Player)) {
            chat.sendMessage(sender, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PLAYER));
            return;
        }

        Player p = (Player) sender;
        User user = AuctionHouse.getInstance().getUserManager().getUser(p);
        ItemStack itemStack = p.getInventory().getItemInMainHand();

        //Check if the player is holding an item
        if (itemStack.getType().isAir() || !itemStack.getType().isItem()) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_NOITEM));
            return;
        }

        //Check create listing
        if (p.getGameMode() == GameMode.CREATIVE && !AuctionHouse.getInstance().getConfigFile().isCreativeListing()) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SETTINGS_CREATIVELISTING_MESSAGE));
            return;
        }

        if (!AuctionHouse.getInstance().getListingManager().checkBlacklist(itemStack)) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_BLACKLIST_BLOCKED));
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

                if (amount > maxListings.get()) maxListings.set(amount);
            } catch (NumberFormatException ignored) {
            }
        });
        if (!p.isOp() && maxListings.get() > 0 && maxListings.get() != -1 && AuctionHouse.getInstance().getListingManager().getActive(p.getUniqueId()).size() >= maxListings.get()) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_MAXLISTINGS, "%max%;" + maxListings.get()));
            return;
        }

        //Check Cooldowns
        if (AuctionHouse.getInstance().getConfigFile().getListingDelay() > 0 && !p.hasPermission("auctionhouse.delay.bypass")) {
            if (AuctionHouse.getInstance().getCooldownManager().isCooldown(p)) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_COOLDOWN, "%time%;" + chat.formatTime(AuctionHouse.getInstance().getCooldownManager().getRemaining(p))));
                return;
            }
        }

        //Encode check
        String encoded = AuctionHouse.getInstance().encode(itemStack, false);
        ItemStack decoded = AuctionHouse.getInstance().decode(encoded);
        if (decoded == null || decoded.getType() == Material.AIR) {
            chat.sendMessage(p, "There was an error creating this listing. Please try again.");
            return;
        }

        //Open GUI since no price is given
        if (args.length == 1) {
            p.openInventory(new ListingMainGUI(itemStack, 0, 0, 0).getInventory());
            return;
        }

        //Check given price
        double buyNowPrice;
        try {
            buyNowPrice = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PRICE));
            return;
        }

        double startingBid;
        try {
            if (args.length >= 3) {
                startingBid = Double.parseDouble(args[2]);
            } else {
                startingBid = -1;
            }
        } catch (NumberFormatException e) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PRICE));
            return;
        }

        double minIncrement;
        try {
            if (args.length >= 4) {
                minIncrement = Double.parseDouble(args[3]);
            } else {
                minIncrement = -1;
            }
        } catch (NumberFormatException e) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PRICE));
            return;
        }

        if (args[1].equalsIgnoreCase("NaN")) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PRICE));
            return;
        }
        if (args.length >= 3) {
            if (args[2].equalsIgnoreCase("NaN")) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PRICE));
                return;
            }
        }
        if (args.length >= 4) {
            if (args[3].equalsIgnoreCase("NaN")) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PRICE));
                return;
            }
        }

        if (buyNowPrice > AuctionHouse.getInstance().getConfigFile().getMaxListing()) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_MAXLISTINGPRICE, "%price%;" + AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMaxListing())));
            return;
        }

        if (buyNowPrice < AuctionHouse.getInstance().getConfigFile().getMinListing()) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_MINLISTINGPRICE, "%price%;" + AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMinListing())));
            return;
        }

        if (AuctionHouse.getInstance().getEcon().getBalance(p) < AuctionHouse.getInstance().getConfigFile().calculateListingFee(buyNowPrice)) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_POORLISTINGFEE));
            return;
        }

        //Determine if auto-confirm
        if (user.getUserSettings().isAutoConfirmListing()) {
            //Remove item from Inventory
            InventoryHandler.removeItemFromPlayer(p, itemStack, itemStack.getAmount(), true);
            AuctionHouse.getInstance().getListingManager().create(p.getUniqueId(), encoded, buyNowPrice, startingBid, minIncrement);
            //Play sounds
            if (user.getUserSettings().isSounds())
                p.playSound(p.getLocation(), AuctionHouse.getInstance().getConfigFile().getCreateListingSound(), 5, 1);
        } else {
            p.openInventory(new ListingMainGUI(itemStack, buyNowPrice, startingBid, minIncrement).getInventory());

        }
    }
}
