package net.akarian.auctionhouse.guis;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.comparators.*;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ExpireReclaimGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    private final AuctionHouseGUI auctionHouseGUI;
    @Getter
    private static final HashMap<UUID, ExpireReclaimGUI> searchMap = new HashMap<>();
    private int page;
    @Getter
    private Inventory inv;
    @Getter
    @Setter
    private ArrayList<Listing> listings;
    @Getter
    @Setter
    private boolean update;
    private boolean search = false;
    private String searchStr = "";
    @Getter
    @Setter
    private boolean sortBool;
    @Getter
    @Setter
    private SortType sortType;

    /**
     * Reclaim expired listings
     *
     * @param player          Player reclaiming listings
     * @param auctionHouseGUI Instance of AuctionHouseGUI
     * @param page            Page number
     */
    public ExpireReclaimGUI(Player player, AuctionHouseGUI auctionHouseGUI, int page) {
        this.player = player;
        this.auctionHouseGUI = auctionHouseGUI;
        this.listings = new ArrayList<>();
        this.page = page;
        this.sortType = SortType.EXPIRE_TIME;
        this.sortBool = true;
        this.update = true;
    }

    public ExpireReclaimGUI searchListings(String search) {
        this.search = !search.equals("");
        this.searchStr = search;
        this.page = 1;
        update = true;
        return this;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {
        switch (slot) {
            case 8:
                player.openInventory(auctionHouseGUI.getInventory());
                break;
            case 45:
                if (item.getType() == Material.NETHER_STAR && page != 1) {
                    page--;
                    update = true;
                    updateInventory();
                }
                return;
            case 47:
                player.closeInventory();
                searchMap.put(player.getUniqueId(), this);
                chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_SEARCH_LEFT));
                chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_SEARCH_RIGHT));
                return;
            case 51:
                player.openInventory(new SortGUI(this).getInventory());
                update = true;
                return;
            case 53:
                if (item.getType() == Material.NETHER_STAR && (listings.size() > 36 * page)) {
                    page++;
                    update = true;
                    updateInventory();
                }
                return;
        }

        //Is a Listing
        if (slot >= 9 && slot <= 45) {
            update = true;
            Listing listing = AuctionHouse.getInstance().getListingManager().get(item);
            if (listing == null) return;
            //Is reclaiming a completed listing
            if (listing.isCompleted()) {
                if (AuctionHouse.getInstance().getListingManager().reclaimCompleted(listing, player, true) == -2) {
                    chat.sendMessage(player, "&cThat listing is already reclaimed!");
                }
            }
            //Is reclaiming an expired listing
            else if (listing.isExpired()) {
                if (AuctionHouse.getInstance().getListingManager().reclaimExpire(listing, player, true) == -2) {
                    chat.sendMessage(player, "&cThat listing is already reclaimed!");
                }
            }
        }
    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format(AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_UNCLAIMED_TITLE)));

        //Top Lining
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        //Return Button
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));

        //Bottom Lining
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(47, ItemBuilder.build(Material.HOPPER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_SEARCH_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_MAIN_SEARCH_LORE)));

        //Sort Item
        inv.setItem(51, ItemBuilder.build(Material.PAPER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_SORT_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_MAIN_SORT_LORE)));

        //Expired Listings
        updateInventory();

        return inv;
    }

    public void updateInventory() {

        if (AuctionHouse.getInstance().getListingManager().getExpired(player.getUniqueId()).size() == 0 && AuctionHouse.getInstance().getListingManager().getUnclaimedCompleted(player.getUniqueId()).size() == 0) {
            clear();
            updateButtons();
            return;
        }

        int amountToDisplay;
        int amountCanDisplay = 36;
        int end = page * amountCanDisplay;
        int displayStart = end - amountCanDisplay;

        ArrayList<Listing> expiredListings = AuctionHouse.getInstance().getListingManager().getUnclaimedExpired(player.getUniqueId());
        ArrayList<Listing> completed = AuctionHouse.getInstance().getListingManager().getUnclaimedCompleted(player.getUniqueId());
        ArrayList<Listing> allListings;

        if (update) {
            ArrayList<Listing> searchedListings = new ArrayList<>();
            if (search) {
                for (Listing listing : expiredListings) {
                    if (searchListings(listing)) searchedListings.add(listing);
                }
                for (Listing listing : completed) {
                    if (searchListings(listing)) searchedListings.add(listing);
                }
            } else {
                searchedListings.addAll(expiredListings);
                searchedListings.addAll(completed);
            }
            listings = sortListings(searchedListings);

            allListings = listings;
            amountToDisplay = Math.min(allListings.size(), amountCanDisplay);
            ArrayList<ItemStack> displayItems = getDisplays(displayStart, amountToDisplay);
            int tick = 0;
            int slot = 9;

            for (int i = displayStart; i <= end; i++) {
                if (displayItems.size() <= tick) {
                    inv.setItem(slot, null);
                } else inv.setItem(slot, displayItems.get(tick));
                tick++;
                slot++;
            }

            update = false;
        }

        updateButtons();
    }

    public ArrayList<ItemStack> getDisplays(int start, int amount) {
        ArrayList<ItemStack> displays = new ArrayList<>();
        List<Listing> allListings = listings;
        for (int i = start; i < start + amount; i++) {
            if (i >= allListings.size()) break;
            Listing listing = allListings.get(i);
            if (listing.isExpired()) {
                displays.add(listing.createExpiredListing(player));
            } else if (listing.isCompleted()) {
                displays.add(listing.createUnclaimedCompleteListing(player));
            }
        }
        return displays;
    }

    public ArrayList<Listing> sortListings(ArrayList<Listing> l) {
        //Get all active listings and put them in an array
        Listing[] listings = l.toArray(new Listing[0]);

        //Switch between the sort type and set according to the determined direction
        switch (sortType) {
            case OVERALL_PRICE:
                if (!sortBool) Arrays.sort(listings, new PriceComparatorLG());
                else Arrays.sort(listings, new PriceComparatorGL());

                break;
            case EXPIRE_TIME:
                if (!sortBool) Arrays.sort(listings, new TimeExpiredComparatorLG());
                else Arrays.sort(listings, new TimeExpiredComparatorLG());
                break;
            case AMOUNT:
                if (!sortBool) Arrays.sort(listings, new AmountComparatorLG());
                else Arrays.sort(listings, new AmountComparatorGL());
                break;
            case COST_PER_ITEM:
                if (!sortBool) Arrays.sort(listings, new CostPerComparatorLG());
                else Arrays.sort(listings, new CostPerComparatorGL());
                break;
        }
        return new ArrayList<>(Arrays.asList(listings));
    }

    public boolean searchListings(Listing listing) {
        //Checking if player is searching
        if (this.search) {
            //Check if the search is searching by seller
            if (this.searchStr.startsWith(AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_SYNTAX_SELLERTAG) + ":")) {
                String playerName = searchStr.split(":")[1];
                UUID playerUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                return listing.getCreator().toString().equalsIgnoreCase(playerUUID.toString());
            } else {
                //Returning whether the listing's item contains the search query
                return chat.formatItem(listing.getItemStack()).toLowerCase().contains(searchStr.toLowerCase());
            }
        }
        return true;
    }

    private void clear() {
        for (int i = 9; i <= 44; i++) {
            inv.setItem(i, null);
        }
    }

    private void updateButtons() {
        //Previous Page

        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_PPAGE_NAME, "%previous%;" + (page - 1), "%max%;" + (listings.size() % 36 == 0 ? String.valueOf(listings.size() / 36) : String.valueOf((listings.size() / 36) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_PPAGE_LORE));
            inv.setItem(45, previous);
        } else {
            inv.setItem(45, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }


        if (listings.size() > 36 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_NPAGE_NAME, "%next%;" + (page + 1), "%max%;" + (listings.size() % 36 == 0 ? String.valueOf(listings.size() / 36) : String.valueOf((listings.size() / 36) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_NPAGE_LORE));
            inv.setItem(53, next);
        } else {
            inv.setItem(53, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
    }

}
