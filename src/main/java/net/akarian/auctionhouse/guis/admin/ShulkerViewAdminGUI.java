package net.akarian.auctionhouse.guis.admin;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.admin.database.active.PlayerActiveListings;
import net.akarian.auctionhouse.guis.admin.database.completed.PlayerCompletedListings;
import net.akarian.auctionhouse.guis.admin.database.expired.PlayerExpiredListings;
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

public class ShulkerViewAdminGUI implements AkarianInventory {

    private final Listing listing;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private Player player;
    private final AuctionHouseGUI auctionHouseGUI;
    private final PlayerActiveListings playerActiveListings;
    private final PlayerExpiredListings playerExpiredListings;
    private final PlayerCompletedListings playerCompletedListings;
    private int adminGUI;

    /**
     * Shulker view admin GUI
     *
     * @param listing         Listing viewing
     * @param auctionHouseGUI Instance of AuctionHouseGUI
     */
    public ShulkerViewAdminGUI(Listing listing, AuctionHouseGUI auctionHouseGUI) {
        this.listing = listing;
        this.auctionHouseGUI = auctionHouseGUI;
        this.playerActiveListings = null;
        this.playerExpiredListings = null;
        this.playerCompletedListings = null;
    }

    /**
     * Shulker view admin GUI from Player Active Listings
     *
     * @param listing              Listing viewing
     * @param player               Player viewing listing
     * @param playerActiveListings Instance of PlayerActiveListings
     */
    public ShulkerViewAdminGUI(Listing listing, Player player, PlayerActiveListings playerActiveListings) {
        this.listing = listing;
        this.playerActiveListings = playerActiveListings;
        this.player = player;
        this.playerExpiredListings = null;
        this.auctionHouseGUI = null;
        this.playerCompletedListings = null;
    }

    /**
     * Shulker view admin GUI from Player Active Listings
     *
     * @param listing               Listing viewing
     * @param player                Player viewing listing
     * @param playerExpiredListings Instance of PlayerExpiredListings
     */
    public ShulkerViewAdminGUI(Listing listing, Player player, PlayerExpiredListings playerExpiredListings) {
        this.listing = listing;
        this.playerActiveListings = null;
        this.player = player;
        this.playerExpiredListings = playerExpiredListings;
        this.auctionHouseGUI = null;
        this.playerCompletedListings = null;
    }

    /**
     * Shulker view admin GUI from Player Active Listings
     *
     * @param listing                 Listing viewing
     * @param player                  Player viewing listing
     * @param playerCompletedListings Instance of PlayerCompletedListings
     */
    public ShulkerViewAdminGUI(Listing listing, Player player, PlayerCompletedListings playerCompletedListings) {
        this.listing = listing;
        this.playerActiveListings = null;
        this.player = player;
        this.playerExpiredListings = null;
        this.auctionHouseGUI = null;
        this.playerCompletedListings = playerCompletedListings;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {

        if (slot == 8) {
            if (playerExpiredListings != null) {
                player.openInventory(playerExpiredListings.getInventory());
            } else if (playerActiveListings != null) {
                player.openInventory(playerActiveListings.getInventory());
            } else if (auctionHouseGUI != null) {
                player.openInventory(auctionHouseGUI.getInventory());
            } else if (playerCompletedListings != null) {
                player.openInventory(playerCompletedListings.getInventory());
            }
        }

    }

    @Override
    public void updateInventory() {

    }

    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 36, chat.format("&4Admin Shulker View"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_rt(), AuctionHouse.getInstance().getMessages().getGui_buttons_rd()));

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
