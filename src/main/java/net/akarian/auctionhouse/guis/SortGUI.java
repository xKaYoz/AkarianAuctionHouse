package net.akarian.auctionhouse.guis;
import net.akarian.auctionhouse.AuctionHouse;
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

public class SortGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final int mainPage;
    private SortType sortType;
    private boolean sortBool;
    private final String search;

    public SortGUI(SortType sortType, boolean sortBool, int mainPage, String search) {
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
            case 10:
                if (sortType == SortType.OVERALL_PRICE) {
                    sortBool = clickType.isLeftClick();
                } else {
                    sortType = SortType.OVERALL_PRICE;
                }
                player.openInventory(getInventory());
                break;
            case 12:
                if (sortType == SortType.TIME_LEFT) {
                    sortBool = clickType.isLeftClick();
                } else {
                    sortType = SortType.TIME_LEFT;
                }
                player.openInventory(getInventory());
                break;
            case 14:
                if (sortType == SortType.COST_PER_ITEM) {
                    sortBool = clickType.isLeftClick();
                } else {
                    sortType = SortType.COST_PER_ITEM;
                }
                player.openInventory(getInventory());
                break;
            case 16:
                if (sortType == SortType.AMOUNT) {
                    sortBool = clickType.isLeftClick();
                } else {
                    sortType = SortType.AMOUNT;
                }
                player.openInventory(getInventory());
                break;
        }

    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, chat.format(AuctionHouse.getInstance().getMessages().getGui_st_title()));
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_rt(), AuctionHouse.getInstance().getMessages().getGui_buttons_rd()));

        if (sortType == SortType.OVERALL_PRICE)
            inv.setItem(10, ItemBuilder.build(Material.LIME_DYE, 1, "&a&l" + AuctionHouse.getInstance().getMessages().getGui_st_op(), highestList()));
        else
            inv.setItem(10, ItemBuilder.build(Material.GRAY_DYE, 1, "&7" + AuctionHouse.getInstance().getMessages().getGui_st_op(), AuctionHouse.getInstance().getMessages().getGui_st_od()));
        if (sortType == SortType.TIME_LEFT)
            inv.setItem(12, ItemBuilder.build(Material.LIME_DYE, 1, "&a&l" + AuctionHouse.getInstance().getMessages().getGui_st_tl(), longestList()));
        else
            inv.setItem(12, ItemBuilder.build(Material.GRAY_DYE, 1, "&7" + AuctionHouse.getInstance().getMessages().getGui_st_tl(), AuctionHouse.getInstance().getMessages().getGui_st_td()));
        if (sortType == SortType.COST_PER_ITEM)
            inv.setItem(14, ItemBuilder.build(Material.LIME_DYE, 1, "&a&l" + AuctionHouse.getInstance().getMessages().getGui_st_cp(), highestList()));
        else
            inv.setItem(14, ItemBuilder.build(Material.GRAY_DYE, 1, "&7" + AuctionHouse.getInstance().getMessages().getGui_st_cp(), AuctionHouse.getInstance().getMessages().getGui_st_cd()));

        if (sortType == SortType.AMOUNT)
            inv.setItem(16, ItemBuilder.build(Material.LIME_DYE, 1, "&a&l" + AuctionHouse.getInstance().getMessages().getGui_st_ai(), highestList()));
        else
            inv.setItem(16, ItemBuilder.build(Material.GRAY_DYE, 1, "&7" + AuctionHouse.getInstance().getMessages().getGui_st_ai(), AuctionHouse.getInstance().getMessages().getGui_st_ad()));

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        return inv;
    }

    private List<String> longestList() {
        List<String> lore = new ArrayList<>();

        lore.add("&8&m&l---------------------------");
        lore.add("");
        if (sortBool) {
            lore.add("    &6→ " + AuctionHouse.getInstance().getMessages().getGui_st_lg() + " &8| &7" + AuctionHouse.getInstance().getMessages().getGui_st_st());
        } else {
            lore.add("      &7" + AuctionHouse.getInstance().getMessages().getGui_st_lg() + " &8| &6" + AuctionHouse.getInstance().getMessages().getGui_st_st() + " ←");
        }
        lore.add("");
        lore.add("&8&m&l---------------------------");
        return lore;
    }

    private List<String> highestList() {
        List<String> lore = new ArrayList<>();

        lore.add("&8&m&l---------------------------");
        lore.add("");
        if (sortBool) {
            lore.add("      &6→ " + AuctionHouse.getInstance().getMessages().getGui_st_hg() + " &8| &7" + AuctionHouse.getInstance().getMessages().getGui_st_lw());
        } else {
            lore.add("        &7" + AuctionHouse.getInstance().getMessages().getGui_st_hg() + " &8| &6" + AuctionHouse.getInstance().getMessages().getGui_st_lw() + " ←");
        }
        lore.add("");
        lore.add("&8&m&l---------------------------");
        return lore;
    }

}
