package net.akarian.auctionhouse.guis;
import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
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

public class SortGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private SortType sortType;
    private boolean sortBool;
    @Getter
    private Inventory inv;

    private final AuctionHouseGUI auctionHouseGUI;
    private final ExpireReclaimGUI reclaimGUI;

    /**
     * Sort the AuctionHouse
     *
     * @param auctionHouseGUI Instance of the Auction House to sort
     */
    public SortGUI(AuctionHouseGUI auctionHouseGUI) {
        this.auctionHouseGUI = auctionHouseGUI;
        this.reclaimGUI = null;
        sortType = auctionHouseGUI.getSortType();
        sortBool = auctionHouseGUI.isSortBool();
    }

    public SortGUI(ExpireReclaimGUI reclaimGUI) {
        this.reclaimGUI = reclaimGUI;
        this.auctionHouseGUI = null;
        sortType = reclaimGUI.getSortType();
        sortBool = reclaimGUI.isSortBool();
    }


    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {

        switch (slot) {
            case 8:
                if (auctionHouseGUI == null)
                    player.openInventory(reclaimGUI.getInventory());
                else
                    player.openInventory(auctionHouseGUI.getInventory());
                return;
            case 10:
                sortBool = !sortBool;
                sortType = SortType.OVERALL_PRICE;
                break;
            case 12:
                sortBool = !sortBool;
                if (auctionHouseGUI == null) {
                    sortType = SortType.EXPIRE_TIME;
                } else {
                    sortType = SortType.TIME_LEFT;
                }
                break;
            case 14:
                sortBool = !sortBool;
                sortType = SortType.COST_PER_ITEM;
                break;
            case 16:
                sortBool = !sortBool;
                sortType = SortType.AMOUNT;
                break;
        }

        if (auctionHouseGUI == null) {
            reclaimGUI.setSortBool(sortBool);
            reclaimGUI.setSortType(sortType);
        } else {
            auctionHouseGUI.setSortBool(sortBool);
            auctionHouseGUI.setSortType(sortType);
        }
        updateInventory();

    }

    @Override
    public void updateInventory() {
        if (sortType == SortType.OVERALL_PRICE)
            inv.setItem(10, ItemBuilder.build(Material.LIME_DYE, 1, "&a&l" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_OVERALLPRICE_NAME), highestList()));
        else
            inv.setItem(10, ItemBuilder.build(Material.GRAY_DYE, 1, "&7" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_OVERALLPRICE_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_SORT_OVERALLPRICE_LORE)));
        if (sortType == SortType.TIME_LEFT || sortType == SortType.EXPIRE_TIME) //TODO Create own message
            inv.setItem(12, ItemBuilder.build(Material.LIME_DYE, 1, "&a&l" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_TIMELEFT_NAME), longestList()));
        else
            inv.setItem(12, ItemBuilder.build(Material.GRAY_DYE, 1, "&7" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_TIMELEFT_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_SORT_TIMELEFT_LORE)));
        if (sortType == SortType.COST_PER_ITEM)
            inv.setItem(14, ItemBuilder.build(Material.LIME_DYE, 1, "&a&l" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_COSTPERITEM_NAME), highestList()));
        else
            inv.setItem(14, ItemBuilder.build(Material.GRAY_DYE, 1, "&7" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_COSTPERITEM_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_SORT_COSTPERITEM_LORE)));

        if (sortType == SortType.AMOUNT)
            inv.setItem(16, ItemBuilder.build(Material.LIME_DYE, 1, "&a&l" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_AMOUNTOFITEMS_NAME), highestList()));
        else
            inv.setItem(16, ItemBuilder.build(Material.GRAY_DYE, 1, "&7" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_AMOUNTOFITEMS_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_SORT_AMOUNTOFITEMS_LORE)));
    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format(AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_TITLE)));
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));

        updateInventory();

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        return inv;
    }

    private List<String> longestList() {
        List<String> lore = new ArrayList<>();

        lore.add("&8&m&l---------------------------");
        lore.add("");
        if (sortBool) {
            lore.add("    &6→ " + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_LONGEST) + " &8| &7" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_SHORTEST));
        } else {
            lore.add("      &7" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_LONGEST) + " &8| &6" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_SHORTEST) + " ←");
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
            lore.add("      &6→ " + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_HIGHEST) + " &8| &7" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_LOWEST));
        } else {
            lore.add("        &7" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_HIGHEST) + " &8| &6" + AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SORT_LOWEST) + " ←");
        }
        lore.add("");
        lore.add("&8&m&l---------------------------");
        return lore;
    }

}
