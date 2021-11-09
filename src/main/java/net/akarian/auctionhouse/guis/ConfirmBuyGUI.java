package net.akarian.auctionhouse.guis;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
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

import java.util.Collections;

public class ConfirmBuyGUI implements AkarianInventory {

    @Getter
    private final Listing listing;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final SortType sortType;
    private final boolean sortBool;
    private final int mainPage;
    private final String search;
    private final Player player;
    @Getter
    private Inventory inv;

    public ConfirmBuyGUI(Player player, Listing listing, SortType sortType, boolean sortBool, int mainPage, String search) {
        this.player = player;
        this.listing = listing;
        this.sortType = sortType;
        this.sortBool = sortBool;
        this.mainPage = mainPage;
        this.search = search;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int i, ItemStack itemStack, ClickType clickType) {

        switch (itemStack.getType()) {

            case LIME_STAINED_GLASS_PANE:
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);

                switch (AuctionHouse.getInstance().getListingManager().buy(listing, player)) {
                    case -1:
                        chat.sendMessage(player, "&cThat listing no longer exists.");
                        break;
                    case 0:
                        chat.sendMessage(player, "&cYou do not have enough money to purchase that.");
                        break;
                    case 1:
                        chat.log("Listing " + chat.formatItem(listing.getItemStack()) + " has been bought by " + player.getName() + ". Creator offline.");
                        break;
                    case 2:
                        chat.log("Listing " + chat.formatItem(listing.getItemStack()) + " has been bought by " + player.getName() + ". Creator online.");
                        break;
                }
                break;
            case RED_STAINED_GLASS_PANE:
                player.openInventory(new AuctionHouseGUI(player, sortType, sortBool, mainPage).search(search).getInventory());
                break;
        }

    }

    @Override
    public @NotNull Inventory getInventory() {
        inv = Bukkit.createInventory(this, 9, chat.format("&eAre you sure???"));

        inv.setItem(0, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, "&a&lConfirm", Collections.singletonList("&7Click to confirm your order.")));
        inv.setItem(1, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, "&a&lConfirm", Collections.singletonList("&7Click to confirm your order.")));
        inv.setItem(2, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, "&a&lConfirm", Collections.singletonList("&7Click to confirm your order.")));
        inv.setItem(3, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, "&a&lConfirm", Collections.singletonList("&7Click to confirm your order.")));

        listing.setupDisplay(player);
        inv.setItem(4, listing.getDisplay());

        inv.setItem(5, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, "&c&lCancel", Collections.singletonList("&7Click to cancel your order.")));
        inv.setItem(6, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, "&c&lCancel", Collections.singletonList("&7Click to cancel your order.")));
        inv.setItem(7, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, "&c&lCancel", Collections.singletonList("&7Click to cancel your order.")));
        inv.setItem(8, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, "&c&lCancel", Collections.singletonList("&7Click to cancel your order.")));


        return inv;
    }

    public void updateInventory() {
        listing.setupDisplay(player);
        inv.setItem(4, listing.getDisplay());
    }
}
