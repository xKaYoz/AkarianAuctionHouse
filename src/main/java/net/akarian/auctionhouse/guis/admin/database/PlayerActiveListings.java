package net.akarian.auctionhouse.guis.admin.database;

import net.akarian.auctionhouse.AuctionHouse;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerActiveListings implements AkarianInventory {

    private final int page;
    private final UUID uuid;
    private final Chat chat;
    private Inventory inv;
    private List<Listing> listings;
    private final Player player;

    /**
     * @param player Player viewing active listings
     * @param uuid   UUID of player viewing
     * @param page   page number
     */
    public PlayerActiveListings(Player player, UUID uuid, int page) {
        this.player = player;
        this.uuid = uuid;
        this.page = page;
        this.chat = AuctionHouse.getInstance().getChat();
        this.listings = new ArrayList<>();
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 45:
                p.openInventory(new PlayerActiveListings(player, uuid, page - 1).getInventory());
                return;
            case 53:
                p.openInventory(new PlayerActiveListings(player, uuid, page + 1).getInventory());
                return;
        }

        //Is a Listing
        if (slot >= 8 && slot <= 45) {
            Listing listing = AuctionHouse.getInstance().getListingManager().get(item);

            if (listing == null) return;

            if (type.isLeftClick()) {
                if (type.isShiftClick()) {
                    if (item.getType() == Material.SHULKER_BOX) {
                        p.openInventory(new ShulkerViewAdminGUI(listing, player, uuid, page).getInventory());
                        return;
                    }
                }
                p.openInventory(new ListingEditAdminGUI(listing, player, uuid, page).getInventory());
            } else {
                switch (AuctionHouse.getInstance().getListingManager().safeRemove(p.getUniqueId().toString(), listing)) {
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
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format("&e&l" + Bukkit.getOfflinePlayer(uuid).getName() + "'s Active Listings"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_cn(), AuctionHouse.getInstance().getMessages().getGui_ah_cd()));
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

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

        return inv;
    }

    public void updateInventory() {

        listings = AuctionHouse.getInstance().getListingManager().getActive(uuid);

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
            inv.setItem(slot, listings.get(i).createAdminListing());
            t++;
            slot++;
        }

    }
}
