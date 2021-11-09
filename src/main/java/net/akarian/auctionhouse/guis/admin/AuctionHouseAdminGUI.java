package net.akarian.auctionhouse.guis.admin;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.comparators.*;
import net.akarian.auctionhouse.guis.*;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AuctionHouseAdminGUI implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, AuctionHouseAdminGUI> searchMap = new HashMap<>();
    @Getter
    private final int page;
    @Getter
    private final Player player;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private final SortType sortType;
    @Getter
    private final boolean sortBool;
    @Getter
    private boolean search = false;
    @Getter
    private String searchStr = "";
    @Getter
    @Setter
    private List<Listing> listings;
    @Getter
    private Inventory inv;
    @Getter
    private int viewable;

    public AuctionHouseAdminGUI(Player player, SortType sortType, boolean sortBool, int page) {
        this.player = player;
        this.sortType = sortType;
        this.sortBool = sortBool;
        this.page = page;
    }

    public AuctionHouseAdminGUI search(String search) {
        this.search = true;
        this.searchStr = search;
        return this;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {
        switch (slot) {
            case 8:
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                return;
            case 45:
                player.openInventory(new AuctionHouseGUI(player, sortType, sortBool, (page - 1)).getInventory());
                return;
            case 47:
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                searchMap.put(player.getUniqueId(), this);
                chat.sendMessage(player, "&eLeft click to clear");
                chat.sendMessage(player, "&eEnter your search query...");
                return;
            case 51:
                player.openInventory(new SortGUI(sortType, sortBool, page, searchStr).getInventory());
                return;
            case 53:
                player.openInventory(new AuctionHouseGUI(player, sortType, sortBool, (page + 1)).getInventory());
                return;
        }

        //Is a Listing
        if (slot >= 8 && slot <= 45) {
            Listing listing = AuctionHouse.getInstance().getListingManager().get(itemStack);

            if (clickType.isLeftClick()) {
                if (clickType.isShiftClick()) {
                    if (itemStack.getType() == Material.SHULKER_BOX) {
                        player.openInventory(new ShulkerViewAdminGUI(listing, sortType, sortBool, page, searchStr).getInventory());
                        return;
                    }
                }
                player.openInventory(new ListingEditAdminGUI(listing, player, sortType, sortBool, page, searchStr).getInventory());
            } else {
                switch (AuctionHouse.getInstance().getListingManager().safeRemove(listing)) {
                    case -1:
                        chat.log("Error while trying to safe remove " + chat.formatItem(listing.getItemStack()));
                        break;
                    case 0:
                        chat.log("Tried to safe remove listing " + listing.getId().toString() + " but it is not active.");
                        break;
                }
            }
            return;

        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format("&c&lAuction&f&lHouse &4Admin Menu"));

        //Top Lining
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        //Close Button
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, "&c&lClose", Collections.singletonList("&7&oClose the menu.")));

        //Listings
        updateInventory();


        //Bottom Lining
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, "&6Previous Page", Collections.singletonList("&7Go to the previous page."));
            inv.setItem(45, previous);
        }
        //Next Page
        if (listings.size() > 36 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, "&6Next Page", Collections.singletonList("&7Go to the next page."));
            inv.setItem(53, next);
        }

        //Search Item
        inv.setItem(47, ItemBuilder.build(Material.HOPPER, 1, "&6Search", Collections.singletonList("&7Search for specific listings.")));

        //Sort Item
        inv.setItem(51, ItemBuilder.build(Material.PAPER, 1, "&6Sort", Arrays.asList(
                "&7&oSort the listings."
        )));

        return inv;
    }

    private Listing[] sortedList() {

        Listing[] listings = AuctionHouse.getInstance().getListingManager().getListings().toArray(new Listing[0]);

        switch (sortType) {
            case OVERALL_PRICE:
                if (!sortBool)
                    Arrays.sort(listings, new PriceComparatorLG());
                else
                    Arrays.sort(listings, new PriceComparatorGL());

                break;
            case TIME_LEFT:
                if (!sortBool)
                    Arrays.sort(listings, new TimeRemainingComparatorLG());
                else
                    Arrays.sort(listings, new TimeRemainingComparatorGL());
                break;
            case AMOUNT:
                if (!sortBool)
                    Arrays.sort(listings, new AmountComparatorLG());
                else
                    Arrays.sort(listings, new AmountComparatorGL());
                break;
            case COST_PER_ITEM:
                if (!sortBool)
                    Arrays.sort(listings, new CostPerComparatorLG());
                else
                    Arrays.sort(listings, new CostPerComparatorGL());
                break;
        }

        return listings;
    }

    public void updateInventory() {
        List<Listing> newListings = new ArrayList<>();
        for (Listing listing : sortedList()) {
            if (search) {
                if (chat.formatItem(listing.getItemStack()).toLowerCase(Locale.ROOT).contains(searchStr.toLowerCase(Locale.ROOT))) {
                    newListings.add(listing);
                }
            } else {
                newListings.add(listing);
            }
        }
        listings = newListings;
        viewable = 0;

        int end = page * 36;
        int start = end - 36;
        int t = start;
        int slot = 9;

        for (int i = 9; i <= 44; i++) {
            inv.setItem(i, null);
        }

        for (int i = start; i <= end; i++) {
            if (listings.size() == t || t >= end) {
                break;
            }
            Listing listing = listings.get(i);
            listing.setupDisplay(player);
            inv.setItem(slot, listing.createAdminListing());
            viewable++;
            slot++;
            t++;
        }

        //Info Item
        inv.setItem(49, ItemBuilder.build(Material.BOOK, 1, "&6Information", Arrays.asList(
                "&8&m&l--------------------",
                "",
                "  &fYour Balance &8&m&l-&2 $" + chat.formatMoney(AuctionHouse.getInstance().getEcon().getBalance(player)),
                "",
                "&8&m&l--------------------"
        )));
    }

}
