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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class ListingEditGUI implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, ListingEditGUI> priceMap = new HashMap<>();
    @Getter
    private static final HashMap<UUID, ListingEditGUI> amountMap = new HashMap<>();
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final int mainPage;
    private final SortType sortType;
    private final Player player;
    private final boolean sortBool;
    @Getter
    private final Listing listing;
    private final String search;
    @Getter
    private Inventory inv;

    public ListingEditGUI(Player player, Listing listing, SortType sortType, boolean sortBool, int mainPage, String search) {
        this.listing = listing;
        this.player = player;
        this.sortType = sortType;
        this.sortBool = sortBool;
        this.mainPage = mainPage;
        this.search = search;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {
        switch (slot) {
            case 8:
                player.openInventory(new AuctionHouseGUI(player, sortType, sortBool, mainPage).search(search).getInventory());
                break;
            case 11:
                chat.sendMessage(player, "&eLeft click to cancel");
                chat.sendMessage(player, "&eEnter the new price of your auction...");
                priceMap.put(player.getUniqueId(), this);
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                break;
            case 15:
                chat.sendMessage(player, "&eLeft click to cancel");
                chat.sendMessage(player, "&eEnter the new amount of your auction...");
                amountMap.put(player.getUniqueId(), this);
                player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
                break;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format("&eEdit your listing"));
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, "&c&lReturn", Collections.singletonList("&7&oReturn the AuctionHouse.")));

        inv.setItem(11, ItemBuilder.build(Material.PAPER, 1, "&6Price", Collections.singletonList("&7&oClick to edit the price.")));
        listing.setupDisplay(player);
        inv.setItem(13, listing.getDisplay());
        inv.setItem(15, ItemBuilder.build(Material.ANVIL, 1, "&6Amount", Arrays.asList(
                "&eIf you enter a higher amount than the current listing,",
                "&eyou must have the items in your inventory.",
                "&eIf you enter a lower amount than the current listing,",
                "&eyou will be given the items you removed.",
                "",
                "&7&oClick to remove or add items to this listing."
        )));

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        return inv;
    }

    public void updateInventory() {
        listing.setupDisplay(player);
        inv.setItem(13, listing.getDisplay());
    }

}
