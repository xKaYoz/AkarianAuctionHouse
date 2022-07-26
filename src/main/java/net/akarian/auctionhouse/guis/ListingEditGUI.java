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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
                chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getGui_le_pc());
                priceMap.put(player.getUniqueId(), this);
                player.closeInventory();
                break;
            case 15:
                chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getGui_le_ac());
                amountMap.put(player.getUniqueId(), this);
                player.closeInventory();
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format(AuctionHouse.getInstance().getMessages().getGui_le_title()));
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_rt(), AuctionHouse.getInstance().getMessages().getGui_buttons_rd()));

        inv.setItem(11, ItemBuilder.build(Material.PAPER, 1, AuctionHouse.getInstance().getMessages().getGui_le_pn(), AuctionHouse.getInstance().getMessages().getGui_le_pd()));
        listing.setupActive(player);
        inv.setItem(13, listing.getDisplay());
        inv.setItem(15, ItemBuilder.build(Material.ANVIL, 1, AuctionHouse.getInstance().getMessages().getGui_le_an(), AuctionHouse.getInstance().getMessages().getGui_le_ad()));

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        return inv;
    }

    public void updateInventory() {
        listing.setupActive(player);
        inv.setItem(13, listing.getDisplay());
    }

}
