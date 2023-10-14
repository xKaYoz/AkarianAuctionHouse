package net.akarian.auctionhouse.guis;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.InventoryHandler;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class ListingMainGUI implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, ListingMainGUI> buyNowMap = new HashMap<>();
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final ItemStack listingItem;
    private final ListingBiddingGUI listingBiddingGUI;
    @Getter
    private Inventory inv;
    private boolean buyNow;
    private boolean bidding;
    @Getter
    @Setter
    private double buyNowPrice;

    public ListingMainGUI(ItemStack listingItem, double buyNowPrice, double startingBid, double minimumIncrement) {
        this.listingItem = listingItem;
        this.listingBiddingGUI = new ListingBiddingGUI(this);
        this.buyNowPrice = buyNowPrice != -1 ? buyNowPrice : AuctionHouse.getInstance().getConfigFile().getMinListing();
        buyNow = this.buyNowPrice != -1;
        listingBiddingGUI.setStartingBid(startingBid != -1 ? startingBid : AuctionHouse.getInstance().getConfigFile().getMinStartingBid());
        listingBiddingGUI.setMinimumIncrement(minimumIncrement != -1 ? minimumIncrement : AuctionHouse.getInstance().getConfigFile().getMinIncrement());
        bidding = startingBid != -1;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {
        switch (slot) {

            case 8:
                p.closeInventory();
                break;
            case 20:
                p.openInventory(listingBiddingGUI.getInventory());
                break;
            case 24:
                p.closeInventory();
                chat.sendMessage(p, "&eEnter the amount to buy the listing now...");
                chat.sendMessage(p, "&eType \"cancel\" or left click to exit.");
                buyNowMap.put(p.getUniqueId(), this);
                break;
            case 29:
                bidding = !bidding;
                updateInventory();
                break;
            case 31:
                User user = AuctionHouse.getInstance().getUserManager().getUser(p);
                if (bidding || buyNow) {
                    if (bidding && buyNow) {
                        AuctionHouse.getInstance().getListingManager().create(p.getUniqueId(), AuctionHouse.getInstance().encode(listingItem, false), buyNowPrice, listingBiddingGUI.getStartingBid(), listingBiddingGUI.getMinimumIncrement());
                    } else if (bidding) {
                        AuctionHouse.getInstance().getListingManager().create(p.getUniqueId(), AuctionHouse.getInstance().encode(listingItem, false), -1, listingBiddingGUI.getStartingBid(), listingBiddingGUI.getMinimumIncrement());
                    } else if (buyNow) {
                        AuctionHouse.getInstance().getListingManager().create(p.getUniqueId(), AuctionHouse.getInstance().encode(listingItem, false), buyNowPrice, -1, -1);
                    }
                    InventoryHandler.removeItemFromPlayer(p, listingItem, listingItem.getAmount(), true);
                    if (user.getUserSettings().isSounds())
                        p.playSound(p.getLocation(), AuctionHouse.getInstance().getConfigFile().getCreateListingSound(), 5, 1);
                    p.closeInventory();
                }
                break;
            case 33:
                buyNow = !buyNow;
                updateInventory();
                break;
        }
    }

    @Override
    public void updateInventory() {

        inv.setItem(20, ItemBuilder.build(Material.EMERALD, 1, "&eBidding Options", Arrays.asList("&fStarting Bid: &2" + chat.formatMoney(listingBiddingGUI.getStartingBid()), "&fMinimum Increment: &2" + chat.formatMoney(listingBiddingGUI.getMinimumIncrement()), "", "&7Click to edit the bidding options.")));
        inv.setItem(24, ItemBuilder.build(Material.CHEST, 1, "&eBuy Now", Arrays.asList("&fCurrent: &2" + chat.formatMoney(buyNowPrice), "", "&7Click to edit the buy now price.")));

        inv.setItem(29, ItemBuilder.build(bidding ? Material.LIME_DYE : Material.GRAY_DYE, 1, "&eToggle Bidding", Collections.singletonList("&7Click to toggle bidding.")));
        inv.setItem(33, ItemBuilder.build(buyNow ? Material.LIME_DYE : Material.GRAY_DYE, 1, "&eToggle Buy Now", Collections.singletonList("&7Click to toggle buying the auction instantly.")));

        if (!bidding && !buyNow) {
            inv.setItem(31, ItemBuilder.build(Material.LIGHT_GRAY_WOOL, 1, "&cCannot Confirm", Collections.singletonList("&7You must have either Bidding or Buy Now enabled.")));
        } else {
            inv.setItem(31, ItemBuilder.build(Material.LIME_WOOL, 1, "&aConfirm Listing", Collections.singletonList("&7Click to list " + chat.formatItem(listingItem) + "&7 onto the Auction House!")));

        }

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 45, chat.format("&6&lCreate Listing"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_CLOSE_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_CLOSE_LORE)));
        for (int i = 36; i <= 44; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(13, listingItem);

        updateInventory();

        return inv;
    }
}
