package net.akarian.auctionhouse.guis.admin;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.guis.admin.database.PlayerActiveListings;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class ListingEditAdminGUI implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, ListingEditAdminGUI> priceMap = new HashMap<>();
    @Getter
    private static final HashMap<UUID, ListingEditAdminGUI> amountMap = new HashMap<>();
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final int mainPage;
    private final SortType sortType;
    private final UUID player;
    private final boolean sortBool;
    @Getter
    private final Listing listing;
    private final String search;
    @Getter
    private Inventory inv;
    private final boolean activeListings;

    /**
     * Edit Listing in AuctionHouseAdminGUI
     *
     * @param listing  Listing to edit
     * @param sortType The type to sort by
     * @param sortBool Whether to sort by Greater to Least or visa versa
     * @param mainPage The page number of the page coming from
     * @param search   Search query of Main AuctionHouse GUI
     */
    public ListingEditAdminGUI(Listing listing, SortType sortType, boolean sortBool, int mainPage, String search) {
        this.listing = listing;
        this.player = null;
        this.sortType = sortType;
        this.sortBool = sortBool;
        this.mainPage = mainPage;
        this.search = search;
        this.activeListings = false;
    }

    /**
     * Edit listing in PlayerActiveListings
     *
     * @param listing Listing to edit
     * @param player  UUID of player who's listing is edited
     * @param page    Main page number
     */
    public ListingEditAdminGUI(Listing listing, UUID player, int page) {
        this.listing = listing;
        this.player = player;
        this.sortType = null;
        this.sortBool = false;
        this.mainPage = page;
        this.search = null;
        this.activeListings = true;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {
        switch (slot) {
            case 8:
                if (activeListings)
                    player.openInventory(new PlayerActiveListings(this.player, mainPage).getInventory());
                    //TODO Make so opens with Admin mode on
                else
                    player.openInventory(new AuctionHouseGUI(player, sortType, sortBool, mainPage).search(search).getInventory());
                break;
            case 10:
                chat.sendMessage(player, "&eLeft click to cancel");
                chat.sendMessage(player, "&eEnter the new price of the auction...");
                priceMap.put(player.getUniqueId(), this);
                player.closeInventory();
                break;
            case 12:
                if (!(clickType.isRightClick() && clickType.isShiftClick())) return;
                chat.sendMessage(player, "&4You have unsafely removed " + AuctionHouse.getInstance().getNameManager().getName(listing.getCreator())
                        + "'s auction of " + chat.formatItem(listing.getItemStack()) + "&4.");
                AuctionHouse.getInstance().getListingManager().remove(listing);
                if (activeListings)
                    player.openInventory(new PlayerActiveListings(this.player, mainPage).getInventory());
                else player.closeInventory();
                break;
            case 14:
                if (!clickType.isRightClick()) return;
                chat.sendMessage(player, "&cYou have safely removed " + AuctionHouse.getInstance().getNameManager().getName(listing.getCreator())
                        + "'s auction of " + chat.formatItem(listing.getItemStack()) + "&c.");
                AuctionHouse.getInstance().getListingManager().safeRemove(player.getUniqueId().toString(), listing);
                if (activeListings)
                    player.openInventory(new PlayerActiveListings(this.player, mainPage).getInventory());
                else player.closeInventory();
                break;
            case 16:
                chat.sendMessage(player, "&eLeft click to cancel");
                chat.sendMessage(player, "&eEnter the new amount of the auction...");
                amountMap.put(player.getUniqueId(), this);
                player.closeInventory();
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format("&4Edit " + chat.formatItem(listing.getItemStack())));
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, "&c&lReturn", Collections.singletonList("&7&oReturn the AuctionHouse.")));

        inv.setItem(10, ItemBuilder.build(Material.PAPER, 1, "&4Price", Collections.singletonList("&7&oClick to edit the price.")));
        inv.setItem(12, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&4&lUnsafe Remove", Arrays.asList(
                "&e&l!! &c&lCAUTION &e&l!!",
                "",
                "&4Remove this listing forcefully",
                "&4This option does not return the item to the creator!",
                "&4This action cannot be undone!",
                "",
                "&4Shift + Right Click to select"
        )));
        inv.setItem(14, ItemBuilder.build(Material.RED_WOOL, 1, "&4Safe Remove", Arrays.asList(
                "&cSafely remove this auction.",
                "&cThis option returns the item to the creator.",
                "",
                "&cRight Click to select"
        )));
        inv.setItem(16, ItemBuilder.build(Material.ANVIL, 1, "&4Amount", Arrays.asList(
                "&7&oClick to remove or add items to this listing."
        )));

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(22, listing.createAdminListing());
        return inv;
    }

    public void updateInventory() {
        inv.setItem(22, listing.createAdminListing());
    }

}