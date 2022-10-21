package net.akarian.auctionhouse.guis;

import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.comparators.*;
import net.akarian.auctionhouse.guis.admin.ListingEditAdminGUI;
import net.akarian.auctionhouse.guis.admin.ShulkerViewAdminGUI;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AuctionHouseGUI implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, AuctionHouseGUI> searchMap = new HashMap<>();
    @Getter
    private int page;
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
    @Getter
    private boolean adminMode;

    /**
     * Auction House GUI
     *
     * @param player   Admin Player
     * @param sortType SortType to sort by
     * @param sortBool Greater than or Less than
     * @param page     Auction House page
     */
    public AuctionHouseGUI(Player player, SortType sortType, boolean sortBool, int page) {
        this.player = player;
        this.sortType = sortType;
        this.sortBool = sortBool;
        this.page = page;
        this.adminMode = false;
    }

    public AuctionHouseGUI adminMode() {
        this.adminMode = true;
        return this;
    }

    /**
     * Search the Auction House
     *
     * @param search Search query
     * @return instance of this class with the search query
     */
    public AuctionHouseGUI search(String search) {
        this.search = !search.equals("");
        this.searchStr = search;
        this.page = 1;
        return this;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {

        switch (slot) {
            //Admin Button
            case 1:
                if (itemStack.getType() == Material.GRAY_DYE && player.hasPermission("auctionhouse.admin.manage")) {
                    inv.setItem(1, ItemBuilder.build(Material.LIME_DYE, 1, "&cAdmin Mode", Collections.singletonList("&aAdmin mode is enabled.")));
                    adminMode = true;
                } else if (itemStack.getType() == Material.LIME_DYE && player.hasPermission("auctionhouse.admin.manage")) {
                    inv.setItem(1, ItemBuilder.build(Material.GRAY_DYE, 1, "&cAdmin Mode", Collections.singletonList("&cAdmin mode is disabled.")));
                    adminMode = false;
                }
                return;
            //Close Button
            case 8:
                player.closeInventory();
                return;
            //Previous Page Button
            case 45:
                if (adminMode)
                    player.openInventory(new AuctionHouseGUI(player, sortType, sortBool, (page - 1)).adminMode().getInventory());
                else player.openInventory(new AuctionHouseGUI(player, sortType, sortBool, (page - 1)).getInventory());
                return;
            //Search Button
            case 46:
                player.closeInventory();
                searchMap.put(player.getUniqueId(), this);
                chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getGui_ah_sl());
                chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getGui_ah_sr());
                return;
            //Expired Reclaim Button
            case 50:
                player.openInventory(new ExpireReclaimGUI(player, this, 1).getInventory());
                break;
            //Sort Button
            case 52:
                player.openInventory(new SortGUI(this).getInventory());
                return;
            //Next Page Button
            case 53:
                if (adminMode)
                    player.openInventory(new AuctionHouseGUI(player, sortType, sortBool, (page + 1)).getInventory());
                else player.openInventory(new AuctionHouseGUI(player, sortType, sortBool, (page + 1)).getInventory());
                return;
        }

        //Is a Listing
        if (slot >= 8 && slot <= 45) {
            Listing listing = AuctionHouse.getInstance().getListingManager().get(itemStack);

            if (listing == null) return;

            //Is in admin mode
            if (adminMode) {

                if (clickType.isLeftClick()) {
                    if (clickType.isShiftClick()) {
                        if (itemStack.getType() == Material.SHULKER_BOX) {
                            player.openInventory(new ShulkerViewAdminGUI(listing, this).getInventory());
                            return;
                        }
                    }
                    player.openInventory(new ListingEditAdminGUI(listing, this).getInventory());
                } else if (clickType.isRightClick() && clickType.isShiftClick()) {
                    switch (AuctionHouse.getInstance().getListingManager().safeRemove(player.getUniqueId().toString(), listing)) {
                        case -1:
                            chat.log("Error while trying to safe remove " + chat.formatItem(listing.getItemStack()));
                            break;
                        case 0:
                            chat.log("Tried to safe remove listing " + listing.getId().toString() + " but it is not active.");
                            break;
                        case 1:
                            chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getSafeRemove());
                    }
                }
                return;
            }

            //Is the creator of the listing
            if (listing.getCreator().toString().equals(player.getUniqueId().toString())) {
                if (clickType.isLeftClick()) {
                    player.openInventory(new ListingEditGUI(player, listing, this).getInventory());
                } else if (clickType.isRightClick() && clickType.isShiftClick()) {
                    switch (AuctionHouse.getInstance().getListingManager().expire(listing, false, true, player.getUniqueId().toString())) {
                        case -3:
                            chat.sendMessage(player, "&eThat item is already expired.");
                            break;
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

            //View shulker or confirm buy
            if (itemStack.getType() == Material.SHULKER_BOX)
                player.openInventory(new ShulkerViewGUI(player, listing, this).getInventory());
            else
                player.openInventory(new ConfirmBuyGUI(player, listing, this).getInventory());
        }

    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format(AuctionHouse.getInstance().getMessages().getAuctionHouseTitle()));

        //Top Lining
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        //Admin Button
        if (player.hasPermission("auctionhouse.admin.manage")) {
            if (!adminMode)
                inv.setItem(1, ItemBuilder.build(Material.GRAY_DYE, 1, "&cAdmin Mode", Collections.singletonList("&cAdmin mode is disabled.")));
            else {
                inv.setItem(1, ItemBuilder.build(Material.LIME_DYE, 1, "&cAdmin Mode", Collections.singletonList("&aAdmin mode is enabled.")));
            }
        }
        //Close Button
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_cn(), AuctionHouse.getInstance().getMessages().getGui_ah_cd()));

        //Listings
        updateInventory();


        //Bottom Lining
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_ppn(), AuctionHouse.getInstance().getMessages().getGui_buttons_ppd());
            inv.setItem(45, previous);
        }

        //Next Page
        if (listings.size() > 36 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_npn(), AuctionHouse.getInstance().getMessages().getGui_buttons_npd());
            inv.setItem(53, next);
        }

        //Search Item
        inv.setItem(46, ItemBuilder.build(Material.HOPPER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_sn(), AuctionHouse.getInstance().getMessages().getGui_ah_sd()));

        //Info Item
        List<String> infoDesc = new ArrayList<>();
        for (String s : AuctionHouse.getInstance().getMessages().getGui_ah_id()) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                infoDesc.add(PlaceholderAPI.setPlaceholders(player, s.replace("%balance%", chat.formatMoney(AuctionHouse.getInstance().getEcon().getBalance(player)))));
            else
                infoDesc.add(s.replace("%balance%", chat.formatMoney(AuctionHouse.getInstance().getEcon().getBalance(player))));
        }
        inv.setItem(48, ItemBuilder.build(Material.BOOK, 1, AuctionHouse.getInstance().getMessages().getGui_ah_in(), infoDesc));

        //Expired Reclaim Item
        inv.setItem(50, ItemBuilder.build(Material.CHEST, 1, AuctionHouse.getInstance().getMessages().getGui_ah_en(), AuctionHouse.getInstance().getMessages().getGui_ah_ed()));

        //Sort Item
        inv.setItem(52, ItemBuilder.build(Material.PAPER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_stn(), AuctionHouse.getInstance().getMessages().getGui_ah_std()));

        return inv;
    }

    public boolean search(Listing listing) {
        //Checking if player is searching
        if(this.search) {
            //Check if the search is searching by seller
            if (this.searchStr.startsWith(AuctionHouse.getInstance().getMessages().getGui_ah_st() + ":")) {
                String playerName = searchStr.split(":")[1];
                UUID playerUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                return listing.getCreator().toString().equalsIgnoreCase(playerUUID.toString());
            } else {
                //Returning whether the listing's item contains the search query
                return chat.formatItem(listing.getItemStack()).toLowerCase(Locale.ROOT).contains(searchStr.toLowerCase(Locale.ROOT));
            }
        }
        return true;
    }
    private Listing[] sortedList() {

        Listing[] listings = AuctionHouse.getInstance().getListingManager().getActive().toArray(new Listing[0]);

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
            if (search(listing)) {
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
            if (adminMode)
                inv.setItem(slot, listing.createAdminActiveListing(player));
            else
                inv.setItem(slot, listing.createActiveListing(player));
            viewable++;
            slot++;
            t++;
        }

        //Info Item
        List<String> infoDesc = new ArrayList<>();
        for (String s : AuctionHouse.getInstance().getMessages().getGui_ah_id()) {
            infoDesc.add(s.replace("%balance%", chat.formatMoney(AuctionHouse.getInstance().getEcon().getBalance(player))));
        }
        inv.setItem(48, ItemBuilder.build(Material.BOOK, 1, AuctionHouse.getInstance().getMessages().getGui_ah_in(), infoDesc));


        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_ppn(), AuctionHouse.getInstance().getMessages().getGui_buttons_ppd());
            inv.setItem(45, previous);
        } else {
            inv.setItem(45, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        //Next Page
        if (listings.size() > 36 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_npn(), AuctionHouse.getInstance().getMessages().getGui_buttons_npd());
            inv.setItem(53, next);
        } else {
            inv.setItem(53, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

    }
}

