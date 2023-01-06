package net.akarian.auctionhouse.guis.admin.edit;

import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.events.LayoutEditEvents;
import net.akarian.auctionhouse.layouts.Layout;
import net.akarian.auctionhouse.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LayoutEditGUI implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, LayoutEditGUI> helpMessage = new HashMap<>();
    @Getter
    private static final HashMap<UUID, Integer> helpPage = new HashMap<>();
    @Getter
    private static final HashMap<UUID, LayoutEditGUI> layoutNameEdit = new HashMap<>();
    @Getter
    private static final HashMap<UUID, LayoutEditGUI> displayNameEdit = new HashMap<>();
    @Getter
    private static final HashMap<UUID, LayoutEditGUI> inventorySizeEdit = new HashMap<>();
    @Getter
    private static Inventory inv;
    private final HashMap<Integer, ItemStack> playerInventory;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    @Getter
    private final Layout layout;
    private boolean isItems = false;
    private boolean isSettings = false;
    private int updateItems;
    private boolean clear = false;
    @Getter
    @Setter
    private boolean spacerItem;
    @Getter
    @Setter
    private boolean adminButton;
    @Getter
    @Setter
    private boolean closeButton;
    @Getter
    @Setter
    private boolean listingItem;
    @Getter
    @Setter
    private boolean previousPageButton;
    @Getter
    @Setter
    private boolean nextPageButton;
    @Getter
    @Setter
    private boolean sortButton;
    @Getter
    @Setter
    private boolean searchButton;
    @Getter
    @Setter
    private boolean informationButton;
    @Getter
    @Setter
    private boolean expiredListingsButton;

    public LayoutEditGUI(Player player, Layout layout) {
        this.player = player;
        this.playerInventory = new HashMap<>();
        this.layout = layout;
        updateItems = 4;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        int i = layout.getInventorySize();

        if(isSettings) {
            if(slot == i) { //Spacer page item
                layout.setSpacerPageItems(!layout.isSpacerPageItems());
                return;
            } else if(slot == i + 1) { //Layout Name
                layoutNameEdit.put(player.getUniqueId(), this);
                chat.sendMessage(player, "&eEnter the new name for this layout...");
                player.closeInventory();
                isSettings = false;
                return;
            } else if(slot == i + 2) { // Display Name
                displayNameEdit.put(player.getUniqueId(), this);
                chat.sendMessage(player, "&eEnter the new display name for this layout...");
                player.closeInventory();
                isSettings = false;
                return;
            } else if(slot == i + 3) { //Inventory size
                inventorySizeEdit.put(player.getUniqueId(), this);
                chat.sendMessage(player, "&ePlease enter whether you'd like a 27, 36, 45, or 54 size Auction House...");
                player.closeInventory();
                isSettings = false;
                return;
            } else if(slot == i + 22) { //Return button
                isSettings = false;
                clear = true;
                updateItems = 4;
                return;
            }
        } else if (isItems) {
            if (slot == i) {
                spacerItem = true;
                return;
            } else if (slot == i + 1) {
                adminButton = true;
                return;
            } else if (slot == i + 2) {
                closeButton = true;
                return;
            } else if (slot == i + 3) {
                listingItem = true;
                return;
            } else if (slot == i + 4) {
                previousPageButton = true;
                return;
            } else if (slot == i + 5) {
                nextPageButton = true;
                return;
            } else if (slot == i + 6) {
                searchButton = true;
                return;
            } else if (slot == i + 7) {
                informationButton = true;
                return;
            } else if (slot == i + 8) {
                sortButton = true;
                return;
            } else if(slot == i + 9) {
                expiredListingsButton = true;
                return;
            } else if (slot == i + 22) {
                isItems = false;
                clear = true;
                updateItems = 4;
                return;
            }
        } else {
            if(slot == i + 11) {
                isSettings = true;
                clear = true;
                return;
            } else if(slot == i + 15) {
                isItems = true;
                clear = true;
                return;
            }
        }

        if(slot == i + 28) {// Reset to Default
            //TODO Rest to default
        } else if(slot == i + 30) {
            isItems = false;
            isSettings = false;
            clear = true;
            resetInventory();
        } else if(slot == i + 31) {
            sendHelpMessage();
        } else if(slot == i + 32) {
            save();
            player.openInventory(new LayoutSelectGUI(player, 1).getInventory());
            layout.saveLayout();
            resetInventory();
        } else if(slot == i + 35) {
            player.openInventory(new LayoutSelectGUI(player, 1).getInventory());
            restoreInventory(true);
        }
    }

    //Send the player the Editor Guide
    public void sendHelpMessage() {
        helpMessage.put(player.getUniqueId(), this);
        helpPage.put(player.getUniqueId(), 1);
        player.closeInventory();
        LayoutEditEvents.sendHelpMessage(player, 1);
    }

    //Run when returning from the Editor Guide
    public void returnFromHelp() {
        player.openInventory(getInv());
        giveEditorMenu();
        updateInventory();

        helpMessage.remove(player.getUniqueId());
        helpPage.remove(player.getUniqueId());
    }
    public void returnFromLayoutName() {
        player.openInventory(getInv());
        giveEditorMenu();
        updateInventory();

        layoutNameEdit.remove(player.getUniqueId());
    }
    public void returnFromDisplayName() {
        player.openInventory(getInv());
        giveEditorMenu();
        updateInventory();

        displayNameEdit.remove(player.getUniqueId());
    }
    public void returnFromInventorySizeEdit() {
        player.openInventory(getInventory());
        inventorySizeEdit.remove(player.getUniqueId());
    }

    public void giveEditorMenu() {
        for (int i = 0; i <= 8; i++) {
            player.getInventory().setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()));
        }
        player.getInventory().setItem(1, ItemBuilder.build(Material.LAVA_BUCKET, 1, "&cReset to Default", Collections.singletonList("&7Set the layout to the default layout.")));
        player.getInventory().setItem(3, ItemBuilder.build(Material.WATER_BUCKET, 1, "&aReset to Current", Collections.singletonList("&7Reset the layout to the current layout.")));
        player.getInventory().setItem(4, ItemBuilder.build(Material.BOOK, 1, "&6Editor Guide", Collections.singletonList("&7Click to open a book explaining how to use the editor.")));
        player.getInventory().setItem(5, ItemBuilder.build(Material.SUNFLOWER, 1, "&6Exit and Save", Collections.singletonList("&7Exit and save the current layout.")));
        player.getInventory().setItem(7, ItemBuilder.build(Material.BARRIER, 1, "&cExit", Collections.singletonList("&cExit and do not save&7.")));

        player.getInventory().setItem(20, ItemBuilder.build(Material.LIME_DYE, 1, "&6Layout Settings", Collections.singletonList("&7Click to edit the settings of this layout.")));
        player.getInventory().setItem(24, ItemBuilder.build(Material.MAGENTA_CONCRETE, 1, "&6Layout Items", Collections.singletonList("&7Click to get more items for your layout.")));
    }

    //Update the inventory
    @Override
    public void updateInventory() {
        //Clear the top rows of the player's inventory
        if (clear) {
            for (int i = 9; i <= 35; i++) {
                player.getInventory().setItem(i, null);
            }
            clear = false;
            if (isSettings || isItems) {
                for (int i = 27; i <= 35; i++) {
                    player.getInventory().setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.emptyList()));
                }
                player.getInventory().setItem(31, ItemBuilder.build(Material.BARRIER, 1, "&cReturn", Collections.singletonList("&7Return to the previous page.")));
            }
        }
        //Main menu
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
        }
        //Layout Settings Menu
        else if (isSettings) {
            player.getInventory().setItem(9, ItemBuilder.build(layout.isSpacerPageItems() ? Material.LIME_DYE : Material.GRAY_DYE, 1, "&6Spacer Items on Page Items", Collections.singletonList("&7Whether to replace page items with spacers if there are no other pages.")));
            player.getInventory().setItem(10, ItemBuilder.build(Material.PAPER, 1, "&6Layout Name", Collections.singletonList("&7Rename the layout.")));
            player.getInventory().setItem(11, ItemBuilder.build(Material.PAPER, 1, "&6Display Name", Collections.singletonList("&7Edit the displayed name for this layout.")));
            player.getInventory().setItem(12, ItemBuilder.build(Material.CHEST, 1, "&6Auction House Size", Collections.singletonList("&7Edit the size of your auction house layout.")));
        }
        //Layout Items Menu
        else {
            player.getInventory().setItem(9, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, "&6Spacer Item", Collections.singletonList("&7Click to get a Spacer item.")));
            player.getInventory().setItem(10, ItemBuilder.build(Material.LIME_DYE, 1, "&cAdmin Mode", Collections.singletonList("&7Click to get the Admin Mode button.")));
            player.getInventory().setItem(11, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_cn(), Collections.singletonList("&7Click to get the Close button.")));
            player.getInventory().setItem(12, ItemBuilder.build(Material.MAGENTA_CONCRETE, 1, "&5Listing Item", Collections.singletonList("&7Click to get a Listing Item item.")));
            player.getInventory().setItem(13, ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_ppn(), Collections.singletonList("&7Click to get a Previous Page button.")));
            player.getInventory().setItem(14, ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_npn(), Collections.singletonList("&7Click to get the Next Page button.")));
            player.getInventory().setItem(15, ItemBuilder.build(Material.HOPPER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_sn(), Collections.singletonList("&7Click to get the Search button.")));
            player.getInventory().setItem(16, ItemBuilder.build(Material.BOOK, 1, AuctionHouse.getInstance().getMessages().getGui_ah_in(), Collections.singletonList("&7Click to get the Information button.")));
            player.getInventory().setItem(17, ItemBuilder.build(Material.PAPER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_stn(), Collections.singletonList("&7Click to get the Sort button.")));
            player.getInventory().setItem(18, ItemBuilder.build(Material.CHEST, 1, AuctionHouse.getInstance().getMessages().getGui_ah_en(), Collections.singletonList("&7Click to get the Expired Listings button.")));
        }
    }

    //Reset the inventory to what it looked like before edited
    public void resetInventory() {
        //Spacer Items
        for (Integer i : layout.getSpacerItems()) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

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


        //Bottom Lining
        giveEditorMenu();
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, layout.getInventorySize(), chat.format(layout.getInventoryName()));

        //Spacer Items
        for (Integer i : layout.getSpacerItems()) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        if (layout.getAdminButton() != -1)
            inv.setItem(layout.getAdminButton(), ItemBuilder.build(Material.LIME_DYE, 1, "&cAdmin Mode", Collections.singletonList("&aAdmin mode is enabled.")));


        //Close Button
        if (layout.getExitButton() != -1)
            inv.setItem(layout.getExitButton(), ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_cn(), AuctionHouse.getInstance().getMessages().getGui_ah_cd()));

        //Listings
        for (Integer i : layout.getListingItems()) {
            inv.setItem(i, ItemBuilder.build(Material.MAGENTA_CONCRETE, 1, "&5Listing Item", Collections.singletonList("&7Place where you want listings to be placed.")));
        }

        //Previous Page
        if (layout.getPreviousPageButton() != -1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_ppn(), AuctionHouse.getInstance().getMessages().getGui_buttons_ppd());
            inv.setItem(layout.getPreviousPageButton(), previous);
        }


        //Next Page
        if (layout.getNextPageButton() != -1) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_npn(), AuctionHouse.getInstance().getMessages().getGui_buttons_npd());
            inv.setItem(layout.getNextPageButton(), next);
        }


        //Search Item
        if (layout.getSearchButton() != -1) {
            inv.setItem(layout.getSearchButton(), ItemBuilder.build(Material.HOPPER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_sn(), AuctionHouse.getInstance().getMessages().getGui_ah_sd()));
        }

        //Info Item
        if (layout.getInfoButton() != -1) {
            List<String> infoDesc = new ArrayList<>();
            for (String s : AuctionHouse.getInstance().getMessages().getGui_ah_id()) {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    infoDesc.add(PlaceholderAPI.setPlaceholders(player, s.replace("%balance%", chat.formatMoney(AuctionHouse.getInstance().getEcon().getBalance(player))).replace("%items%", AuctionHouse.getInstance().getListingManager().getActive().size() + "")));
                else
                    infoDesc.add(s.replace("%balance%", chat.formatMoney(AuctionHouse.getInstance().getEcon().getBalance(player))).replace("%items%", AuctionHouse.getInstance().getListingManager().getActive().size() + ""));
            }
            inv.setItem(layout.getInfoButton(), ItemBuilder.build(Material.BOOK, 1, AuctionHouse.getInstance().getMessages().getGui_ah_in(), infoDesc));
        }

        //Expired Reclaim Item
        if (layout.getExpiredItemsButton() != -1)
            inv.setItem(layout.getExpiredItemsButton(), ItemBuilder.build(Material.CHEST, 1, AuctionHouse.getInstance().getMessages().getGui_ah_en(), AuctionHouse.getInstance().getMessages().getGui_ah_ed()));

        //Sort Item
        if (layout.getSortButton() != -1)
            inv.setItem(layout.getSortButton(), ItemBuilder.build(Material.PAPER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_stn(), AuctionHouse.getInstance().getMessages().getGui_ah_std()));


        giveEditorMenu();

        return inv;
    }

    /**
     * Clone the player's inventory
     *
     * @param clear Clear the player's inventory while cloning
     */
    public LayoutEditGUI cloneInventory(boolean clear) {
        for (int i = 0; i <= 35; i++) {
            if (player.getInventory().getItem(i) != null) {
                playerInventory.put(i, player.getInventory().getItem(i));
                chat.alert("cloned " + player.getInventory().getItem(i).getType().name() + " at " + i);
                if (clear) player.getInventory().setItem(i, null);
            }
        }
        player.updateInventory();
        return this;
    }

    /**
     * Restore the player's inventory
     *
     * @param clear Clear the saved player inventory
     */
    public void restoreInventory(boolean clear) {
        HashMap<Integer, ItemStack> cloned = (HashMap<Integer, ItemStack>) playerInventory.clone();
        for(int i = 0; i <= 35; i++) {
            player.getInventory().setItem(i, null);
        }
        player.updateInventory();
        for (Map.Entry<Integer, ItemStack> set : cloned.entrySet()) {
            ItemStack itemStack = set.getValue();
            int slot = set.getKey();
            player.getInventory().setItem(slot, itemStack);
            chat.alert("restored "+ itemStack.getType().name() + " at " + slot);
            if (clear) playerInventory.remove(itemStack, slot);
        }
    }

    /**
     * Save the edit
     */
    private void save() {
        List<Integer> listings = new ArrayList<>();
        List<Integer> spacers = new ArrayList<>();

        layout.setAdminButton(-1);
        layout.setExitButton(-1);
        layout.setPreviousPageButton(-1);
        layout.setNextPageButton(-1);
        layout.setSearchButton(-1);
        layout.setInfoButton(-1);
        layout.setExpiredItemsButton(-1);
        layout.setSortButton(-1);

        for (int i = 0; i < layout.getInventorySize(); i++) {
            if (inv.getItem(i) == null) continue;
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
