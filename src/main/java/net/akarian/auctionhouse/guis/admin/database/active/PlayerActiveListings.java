package net.akarian.auctionhouse.guis.admin.database.active;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.ListingEditAdminGUI;
import net.akarian.auctionhouse.guis.admin.ShulkerViewAdminGUI;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PlayerActiveListings implements AkarianInventory {

    private int page;
    private final UUID uuid;
    private final Chat chat;
    private Inventory inv;
    private List<Listing> listings;
    private final Player player;
    private final ActiveListingsGUI previousGUI;
    private boolean update;

    /**
     * @param player Player viewing active listings
     * @param uuid   UUID of player viewing
     * @param page   page number
     */
    public PlayerActiveListings(Player player, UUID uuid, int page, ActiveListingsGUI gui) {
        this.player = player;
        this.uuid = uuid;
        this.page = page;
        this.chat = AuctionHouse.getInstance().getChat();
        this.listings = new ArrayList<>();
        this.previousGUI = gui;
        this.update = false;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                p.openInventory(previousGUI.getInventory());
                return;
            case 45:
                if (page == 1) return;
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

            if (type.isLeftClick()) {
                if (type.isShiftClick()) {
                    if (item.getType() == Material.SHULKER_BOX) {
                        p.openInventory(new ShulkerViewAdminGUI(listing, player, this).getInventory());
                        return;
                    }
                }
                p.openInventory(new ListingEditAdminGUI(listing, player, this).getInventory());
            } else {
                switch (AuctionHouse.getInstance().getListingManager().safeRemove(p.getUniqueId().toString(), listing)) {
                    case -1:
                        chat.log("Error while trying to safe remove " + chat.formatItem(listing.getItemStack()), AuctionHouse.getInstance().isDebug());
                        break;
                    case 0:
                        chat.log("Tried to safe remove listing " + listing.getId().toString() + " but it is not active.", AuctionHouse.getInstance().isDebug());
                        break;
                }
            }
        }
    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format("&a" + Bukkit.getOfflinePlayer(uuid).getName() + "'s Active Listings"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

        return inv;
    }

    public void updateInventory() {
        List<Listing> oldListings = listings;

        listings = AuctionHouse.getInstance().getListingManager().getActive(uuid);

        if (oldListings.equals(listings) && !update) return;

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
            inv.setItem(slot, listings.get(i).createAdminActiveListing(player));
            t++;
            slot++;
        }

        update = false;

        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_PPAGE_NAME, "%previous%;" + (page - 1), "%max%;" + (listings.size() % 36 == 0 ? String.valueOf(listings.size() / 36) : String.valueOf((listings.size() / 36) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_PPAGE_LORE));
            inv.setItem(45, previous);
        }

        //Next Page
        if (listings.size() > 36 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_NPAGE_NAME, "%next%;" + (page + 1), "%max%;" + (listings.size() % 36 == 0 ? String.valueOf(listings.size() / 36) : String.valueOf((listings.size() / 36) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_NPAGE_LORE));
            inv.setItem(53, next);
        }

    }
}
