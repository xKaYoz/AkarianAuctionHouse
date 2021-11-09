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
import org.jetbrains.annotations.NotNull;

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
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, chat.format("&eSort Menu"));
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, "&c&lReturn", Collections.singletonList("&7&oReturn the AuctionHouse.")));
        if (sortType == SortType.OVERALL_PRICE)
            inv.setItem(10, ItemBuilder.build(Material.LIME_DYE, 1, "&a&lOverall Price", highestList()));
        else
            inv.setItem(10, ItemBuilder.build(Material.GRAY_DYE, 1, "&7Overall Price", Collections.singletonList("&7&oClick to enable sorting by overall price.")));
        if (sortType == SortType.TIME_LEFT)
            inv.setItem(12, ItemBuilder.build(Material.LIME_DYE, 1, "&a&lTime Left", longestList()));
        else
            inv.setItem(12, ItemBuilder.build(Material.GRAY_DYE, 1, "&7Time Left", Collections.singletonList("&7&oClick to enable sorting by the time remaining.")));
        if (sortType == SortType.COST_PER_ITEM)
            inv.setItem(14, ItemBuilder.build(Material.LIME_DYE, 1, "&a&lCost per item", highestList()));
        else
            inv.setItem(14, ItemBuilder.build(Material.GRAY_DYE, 1, "&7Cost per item", Collections.singletonList("&7&oClick to enable sorting by the cost of each item.")));

        if (sortType == SortType.AMOUNT)
            inv.setItem(16, ItemBuilder.build(Material.LIME_DYE, 1, "&a&lAmount of Items", highestList()));
        else
            inv.setItem(16, ItemBuilder.build(Material.GRAY_DYE, 1, "&7Amount of Items", Collections.singletonList("&7&oClick to enable sorting by the amount of items.")));

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
            lore.add("    &6→ Longest &8| &7Shortest");
        } else {
            lore.add("      &7Longest &8| &6Shortest ←");
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
            lore.add("      &6→ Highest &8| &7Lowest");
        } else {
            lore.add("        &7Highest &8| &6Lowest ←");
        }
        lore.add("");
        lore.add("&8&m&l---------------------------");
        return lore;
    }

}
