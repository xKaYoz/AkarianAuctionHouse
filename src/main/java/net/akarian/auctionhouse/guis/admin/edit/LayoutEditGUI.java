package net.akarian.auctionhouse.guis.admin.edit;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.layouts.Layout;
import net.akarian.auctionhouse.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LayoutEditGUI implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, LayoutEditGUI> helpMessage = new HashMap<>();
    @Getter
    private static final HashMap<UUID, Integer> helpPage = new HashMap<>();
    private Inventory inv;
    private final HashMap<Integer, ItemStack> playerInventory;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    private final Layout layout;
    private boolean isItems = false;
    private boolean isSettings = false;
    private int updateItems;
    private boolean clear = false;

    public LayoutEditGUI(Player player, Layout layout) {
        this.player = player;
        this.playerInventory = new HashMap<>();
        this.layout = layout;
        cloneInventory(true);
        updateItems = 4;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        if(isSettings) {
            switch (slot) {
                case 54:  //Spacer Page Items
                    layout.setSpacerPageItems(!layout.isSpacerPageItems());
            }
        }

        switch (slot) {
            case 65:
                if(!isSettings && !isItems) {
                    isSettings = true;
                    clear = true;
                }
                break;
            case 69:
                if(!isItems && !isSettings) {
                    isItems = true;
                    clear = true;
                }
                break;
            case 76:
                if(isItems || isSettings) {
                    isSettings = isItems = false;
                    clear = true;
                    updateItems = 4;
                }
            case 80:
                break;
            case 82:
                break;
            case 84:
                player.openInventory(getInventory());
                player.updateInventory();
                break;
            case 85:
                p.closeInventory();
                chat.sendMessage(p, "&eType \"next\" for next page, \"previous\" for previous page, and \"cancel\" to go go back to the editor.");
                helpMessage.put(p.getUniqueId(), this);
                helpPage.put(p.getUniqueId(), 1);
                break;
            case 86:
                save();
                player.openInventory(new LayoutSelectGUI(player, 1).getInventory());
                layout.saveLayout();
                restoreInventory(true);
            case 88:
                player.openInventory(new LayoutSelectGUI(player, 1).getInventory());
                restoreInventory(true);
                break;
        }

    }

    @Override
    public void updateInventory() {
        if(clear){
            for (int i = 9; i <= 35; i++) {
                player.getInventory().setItem(i, null);
            }
            clear = false;
            if(isSettings) {
                for(int i = 27; i <= 35; i++){
                    player.getInventory().setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
                }
                player.getInventory().setItem(31, ItemBuilder.build(Material.BARRIER, 1, "&cReturn", Collections.singletonList("&7Return to the previous page.")));
            }
        }
        if (!isSettings && !isItems) {
            updateItems++;
            if (updateItems == 5) {

                player.getInventory().setItem(20, ItemBuilder.build(Material.LIME_DYE, 1, "&6Layout Settings", Collections.singletonList("&7Click to edit the settings of this layout.")));

                Random ran = new Random();
                switch (ran.nextInt(10)) {
                    case 0:
                        player.getInventory().setItem(24, ItemBuilder.build(Material.MAGENTA_CONCRETE, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
                        break;
                    case 1:
                        player.getInventory().setItem(24, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
                        break;
                    case 2:
                        player.getInventory().setItem(24, ItemBuilder.build(Material.GRAY_DYE, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
                        break;
                    case 3:
                        player.getInventory().setItem(24, ItemBuilder.build(Material.LIME_DYE, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
                        break;
                    case 4:
                        player.getInventory().setItem(24, ItemBuilder.build(Material.BARRIER, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
                        break;
                    case 5:
                        player.getInventory().setItem(24, ItemBuilder.build(Material.NETHER_STAR, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
                        break;
                    case 6:
                        player.getInventory().setItem(24, ItemBuilder.build(Material.HOPPER, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
                        break;
                    case 7:
                        player.getInventory().setItem(24, ItemBuilder.build(Material.BOOK, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
                        break;
                    case 8:
                        player.getInventory().setItem(24, ItemBuilder.build(Material.CHEST, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
                        break;
                    case 9:
                        player.getInventory().setItem(24, ItemBuilder.build(Material.PAPER, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
                        break;
                }
                updateItems = 0;
            }
        } else if(isSettings) {
            player.getInventory().setItem(9, ItemBuilder.build(layout.isSpacerPageItems() ? Material.LIME_DYE : Material.GRAY_DYE, 1, "&6Spacer Items on Page Items", Collections.singletonList("&7Whether to replace page items with spacers if there are no other pages.")));
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, layout.getInventorySize(), chat.format(layout.getInventoryName()));

        //Spacer Items
        for (Integer i : layout.getSpacerItems()) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }


        inv.setItem(layout.getAdminButton(), ItemBuilder.build(Material.GRAY_DYE, 1, "&cAdmin Mode", Collections.singletonList("&cAdmin mode is disabled.")));

        inv.setItem(layout.getAdminButton(), ItemBuilder.build(Material.LIME_DYE, 1, "&cAdmin Mode", Collections.singletonList("&aAdmin mode is enabled.")));


        //Close Button
        inv.setItem(layout.getExitButton(), ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_cn(), AuctionHouse.getInstance().getMessages().getGui_ah_cd()));

        //Listings
        for (Integer i : layout.getListingItems()) {
            inv.setItem(i, ItemBuilder.build(Material.MAGENTA_CONCRETE, 1, "&5Listing Item", Collections.singletonList("&7Place where you want listings to be placed.")));
        }

        //Previous Page

        ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_ppn(), AuctionHouse.getInstance().getMessages().getGui_buttons_ppd());
        inv.setItem(layout.getPreviousPageButton(), previous);


        //Next Page

        ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_npn(), AuctionHouse.getInstance().getMessages().getGui_buttons_npd());
        inv.setItem(layout.getNextPageButton(), next);


        //Search Item
        inv.setItem(layout.getSearchButton(), ItemBuilder.build(Material.HOPPER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_sn(), AuctionHouse.getInstance().getMessages().getGui_ah_sd()));

        //Info Item
        List<String> infoDesc = new ArrayList<>();
        for (String s : AuctionHouse.getInstance().getMessages().getGui_ah_id()) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                infoDesc.add(PlaceholderAPI.setPlaceholders(player, s.replace("%balance%", chat.formatMoney(AuctionHouse.getInstance().getEcon().getBalance(player))).replace("%items%", AuctionHouse.getInstance().getListingManager().getActive().size() + "")));
            else
                infoDesc.add(s.replace("%balance%", chat.formatMoney(AuctionHouse.getInstance().getEcon().getBalance(player))).replace("%items%", AuctionHouse.getInstance().getListingManager().getActive().size() + ""));
        }
        inv.setItem(layout.getInfoButton(), ItemBuilder.build(Material.BOOK, 1, AuctionHouse.getInstance().getMessages().getGui_ah_in(), infoDesc));

        //Expired Reclaim Item
        inv.setItem(layout.getExpiredItemsButton(), ItemBuilder.build(Material.CHEST, 1, AuctionHouse.getInstance().getMessages().getGui_ah_en(), AuctionHouse.getInstance().getMessages().getGui_ah_ed()));

        //Sort Item
        inv.setItem(layout.getSortButton(), ItemBuilder.build(Material.PAPER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_stn(), AuctionHouse.getInstance().getMessages().getGui_ah_std()));


        //Top Lining
        for (int i = 0; i <= 8; i++) {
            player.getInventory().setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        player.getInventory().setItem(1, ItemBuilder.build(Material.LAVA_BUCKET, 1, "&cReset to Default", Collections.singletonList("&7Set the layout to the default layout.")));
        player.getInventory().setItem(3, ItemBuilder.build(Material.WATER_BUCKET, 1, "&aReset to Current", Collections.singletonList("&7Reset the layout to the current layout.")));
        player.getInventory().setItem(4, ItemBuilder.build(Material.BOOK, 1, "&6Editor Guide", Collections.singletonList("&7Click to open a book explaining how to use the editor.")));
        player.getInventory().setItem(5, ItemBuilder.build(Material.SUNFLOWER, 1, "&6Exit and Save", Collections.singletonList("&7Exit and save the current layout.")));
        player.getInventory().setItem(7, ItemBuilder.build(Material.BARRIER, 1, "&cExit", Collections.singletonList("&cExit and do not save&7.")));

        player.getInventory().setItem(20, ItemBuilder.build(Material.LIME_DYE, 1, "&6Layout Settings", Collections.singletonList("&7Click to edit the settings of this layout.")));
        player.getInventory().setItem(24, ItemBuilder.build(Material.MAGENTA_CONCRETE, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));


        return inv;
    }

    private void cloneInventory(boolean clear) {
        for (int i = 0; i <= 35; i++) {
            if (player.getInventory().getItem(i) != null) {
                playerInventory.put(i, player.getInventory().getItem(i));
                if (clear) player.getInventory().setItem(i, null);
            }
        }
    }

    public void restoreInventory(boolean clear) {
        HashMap<Integer, ItemStack> cloned = (HashMap<Integer, ItemStack>) playerInventory.clone();
        player.getInventory().clear();
        for (Map.Entry<Integer, ItemStack> set : cloned.entrySet()) {
            ItemStack itemStack = set.getValue();
            int slot = set.getKey();
            player.getInventory().setItem(slot, itemStack);
            if (clear) playerInventory.remove(itemStack, slot);
        }

        player.updateInventory();
    }

    private void save() {
        List<Integer> listings = new ArrayList<>();
        List<Integer> spacers = new ArrayList<>();

        for (int i = 0; i < layout.getInventorySize(); i++) {
            if (inv.getItem(i) == null) return;
            switch (Objects.requireNonNull(inv.getItem(i)).getType()) {
                case LIME_DYE:
                case GRAY_DYE:
                    layout.setAdminButton(i);
                    break;
                case BARRIER:
                    layout.setExitButton(i);
                    break;
                case NETHER_STAR:
                    if (inv.getItem(i).getItemMeta().getDisplayName().contains("Previous")) {
                        layout.setPreviousPageButton(i);
                    } else {
                        layout.setNextPageButton(i);
                    }
                    break;
                case HOPPER:
                    layout.setSearchButton(i);
                    break;
                case BOOK:
                    layout.setInfoButton(i);
                    break;
                case CHEST:
                    layout.setExpiredItemsButton(i);
                    break;
                case PAPER:
                    layout.setSortButton(i);
                    break;
                case MAGENTA_CONCRETE:
                    listings.add(i);
                    break;
                case GRAY_STAINED_GLASS_PANE:
                    spacers.add(i);
                    break;
            }
        }
        layout.setListingItems(listings);
        layout.setSpacerItems(spacers);
    }
}
