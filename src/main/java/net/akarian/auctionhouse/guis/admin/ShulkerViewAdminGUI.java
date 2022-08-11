package net.akarian.auctionhouse.guis.admin;

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
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Collections;
import java.util.UUID;

public class ShulkerViewAdminGUI implements AkarianInventory {

    private final Listing listing;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final int mainPage;
    private final SortType sortType;
    private final boolean sortBool;
    private final String search;
    private final UUID activeListings;
    private Player player;

    /**
     * Shulker view admin GUI
     *
     * @param listing  Listing viewing
     * @param sortType Main page's sort type
     * @param sortBool Main page's Greater than or less than
     * @param mainPage Main page's page number
     * @param search   Main page's search query
     */
    public ShulkerViewAdminGUI(Listing listing, SortType sortType, boolean sortBool, int mainPage, String search) {
        this.listing = listing;
        this.sortType = sortType;
        this.sortBool = sortBool;
        this.mainPage = mainPage;
        this.search = search;
        this.activeListings = null;
    }

    /**
     * Shulker view admin GUI from Player Active Listings
     *
     * @param listing        Listing viewing
     * @param player         Player viewing listing
     * @param activeListings UUID of player whose active listings are being viewed
     * @param mainPage       Main page's page number
     */
    public ShulkerViewAdminGUI(Listing listing, Player player, UUID activeListings, int mainPage) {
        this.listing = listing;
        this.sortType = null;
        this.sortBool = false;
        this.mainPage = mainPage;
        this.search = null;
        this.player = player;
        this.activeListings = activeListings;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {

        if (slot == 8) {
            if (activeListings != null)
                player.openInventory(new PlayerActiveListings(this.player, activeListings, mainPage).getInventory());
            else
                player.openInventory(new AuctionHouseGUI(player, sortType, sortBool, mainPage).search(search).adminMode().getInventory());
        } else {
            return;
        }

    }

    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 36, chat.format("&4Admin Shulker View"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, "&c&lReturn", Collections.singletonList("&7&oReturn the AuctionHouse.")));

        int start = 9;

        if (listing.getItemStack().getItemMeta() instanceof BlockStateMeta) {
            BlockStateMeta im = (BlockStateMeta) listing.getItemStack().getItemMeta();
            if (im.getBlockState() instanceof ShulkerBox) {
                ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                for (ItemStack itemStack : shulker.getInventory().getContents()) {
                    if (itemStack != null) {
                        inv.setItem(start, itemStack);
                        start++;
                    }
                }
            }
        }
        return inv;
    }
}
