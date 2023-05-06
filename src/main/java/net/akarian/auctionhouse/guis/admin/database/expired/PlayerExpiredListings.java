package net.akarian.auctionhouse.guis.admin.database.expired;

import net.akarian.auctionhouse.AuctionHouse;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerExpiredListings implements AkarianInventory {

    private int page;
    private final UUID uuid;
    private final Chat chat;
    private final Player player;
    private Inventory inv;
    private List<Listing> listings;
    private final ExpiredListingsGUI previousGUI;
    private boolean update;

    /**
     * @param player Player viewing expired listings
     * @param uuid   UUID of player viewing
     * @param page   page number
     * @param gui    previous GUI
     */
    public PlayerExpiredListings(Player player, UUID uuid, int page, ExpiredListingsGUI gui) {
        this.player = player;
        this.uuid = uuid;
        this.page = page;
        this.chat = AuctionHouse.getInstance().getChat();
        this.listings = new ArrayList<>();
        this.previousGUI = gui;
        update = false;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                p.openInventory(previousGUI.getInventory());
                return;
            case 45:
                if (page == 1) break;
                page--;
                update = true;
                updateInventory();
                return;
            case 53:
                if (!(listings.size() > 36 * page)) break;
                page++;
                update = true;
                updateInventory();
                return;
        }

        //Is a Listing
        if (slot >= 8 && slot <= 45) {
            Listing listing = AuctionHouse.getInstance().getListingManager().get(item);
            if (listing == null) return;
            if (type.isLeftClick() && item.getType() == Material.SHULKER_BOX) {
                p.openInventory(new ShulkerViewAdminGUI(listing, player, this).getInventory());
            } else if (type.isRightClick() && type.isShiftClick()) {
                if (AuctionHouse.getInstance().getListingManager().removeExpired(listing)) {
                    chat.sendMessage(player, "&eRemoved...");
                }
            }
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format("&c" + Bukkit.getOfflinePlayer(uuid).getName() + "'s Expired Listings"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_rt(), AuctionHouse.getInstance().getMessages().getGui_buttons_rd()));
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

        return inv;
    }

    public void updateInventory() {
        List<Listing> oldListings = listings;

        listings = AuctionHouse.getInstance().getListingManager().getExpired(uuid);

        if (oldListings.equals(listings) && !update) return;

        int end = page * 36;
        int start = end - 36;
        int t = start;
        int slot = 9;
        int errors = 0;

        for (int i = 9; i <= 44; i++) {
            inv.setItem(i, null);
        }

        for (int i = start; i <= end; i++) {
            if (listings.size() == t || t >= end || t + errors >= end) {
                break;
            }
            ItemStack item = listings.get(i).createAdminExpiredListing(player);
            if (item == null) {
                t++;
                errors++;
                continue;
            }
            inv.setItem(slot, item);
            t++;
            slot++;
        }
        update = false;

        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_ppn().replace("%previous%", String.valueOf(page - 1)).replace("%max%", listings.size() % 36 == 0 ? String.valueOf(listings.size() / 36) : String.valueOf((listings.size() / 36) + 1)), AuctionHouse.getInstance().getMessages().getGui_buttons_ppd());
            inv.setItem(45, previous);
        } else {
            inv.setItem(45, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        //Next Page
        if (listings.size() > 36 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_npn().replace("%next%", String.valueOf(page + 1)).replace("%max%", listings.size() % 36 == 0 ? String.valueOf(listings.size() / 36) : String.valueOf((listings.size() / 36) + 1)), AuctionHouse.getInstance().getMessages().getGui_buttons_npd());
            inv.setItem(53, next);
        } else {
            inv.setItem(53, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

    }
}
