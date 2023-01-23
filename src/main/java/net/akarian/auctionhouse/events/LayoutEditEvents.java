package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.edit.LayoutEditGUI;
import net.akarian.auctionhouse.layouts.Layout;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LayoutEditEvents implements Listener {

    private static final HashMap<UUID, Integer> taskMap = new HashMap<>();
    private static final List<UUID> confirm27 = new ArrayList<>();
    private static final List<UUID> confirm36 = new ArrayList<>();
    private static final List<UUID> confirm45 = new ArrayList<>();

    public static void sendHelpMessage(Player player, int page) {
        Chat chat = AuctionHouse.getInstance().getChat();
        switch (page) {
            case 1:
                taskMap.put(player.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), () -> {
                    chat.sendRawMessage(player, "                  &6&lAuction House Editor Guide &7(1/3) ");
                    chat.sendRawMessage(player, "&7> &fWelcome to the Auction House Editor Guide!");
                    chat.sendRawMessage(player, "&7> &fFrom the main menu you have two options, &e&nLayout Settings&f and &e&nLayout Items&f.");
                    chat.sendRawMessage(player, "&7> &fIn the &e&nLayout Settings&f, you can customize the settings such as the size and name unique to each layout.");
                    chat.sendRawMessage(player, "&7> &fIn the &e&nLayout Items&f, you have all of the items that you can place in the auction house.");
                    chat.sendRawMessage(player, "&7> &fBe cautious, some items (which are marked) may only be placed once.");
                    chat.sendRawMessage(player, " ");
                    chat.sendRawMessage(player, "&eType \"next\" for next page or \"cancel\" to go back to the editor.");
                }, 0, 20 * 30));
                break;
            case 2:
                taskMap.put(player.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), () -> {
                    chat.sendRawMessage(player, "                  &6&lAuction House Editor Guide &7(2/3) ");
                    chat.sendRawMessage(player, "&7> &e&nReset to Default&f will reset the current layout to the default auction house layout.");
                    chat.sendRawMessage(player, "&7> &e&nReset to Current&f will reset the layout to how it was before you started editing it.");
                    chat.sendRawMessage(player, "&7> &e&nExit and Save&f will save the current layout and return you to the previous layout selector screen.");
                    chat.sendRawMessage(player, "&7> &e&nExit&f will return you to the previous layout selector screen WITHOUT saving.");
                    chat.sendRawMessage(player, " ");
                    chat.sendRawMessage(player, "&eType \"previous\" for previous page, \"next\" for the next page, or \"cancel\" to go back to the editor.");
                }, 0, 20 * 30));
                break;
            case 3:
                taskMap.put(player.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), () -> {
                    chat.sendRawMessage(player, "                  &6&lAuction House Editor Guide &7(3/3) ");
                    chat.sendRawMessage(player, "&7> &fWhile in the editor, you can &e&nRight Click&f am item to remove it.");
                    chat.sendRawMessage(player, "&7> &fYou can also click the &e&nMiddle Mouse Button&f to get another of the clicked item.");
                    chat.sendRawMessage(player, "&7> &fTo get rid of an item that you have on your cursor, just drop it outside of the inventory.");
                    chat.sendRawMessage(player, " ");
                    chat.sendRawMessage(player, "&eType \"previous\" for previous page or \"cancel\" to go back to the editor.");
                }, 0, 20 * 30));
                break;
        }
    }

    public static boolean needsReset27(Layout layout) {
        for (Integer i : layout.getListingItems()) {
            if (i >= 27) return true;
        }
        for (Integer i : layout.getSpacerItems()) {
            if (i >= 27) return true;
        }
        return layout.getAdminButton() >= 27 || layout.getExitButton() >= 27 || layout.getPreviousPageButton() >= 27 || layout.getNextPageButton() >= 27 || layout.getSearchButton() >= 27 || layout.getExpiredItemsButton() >= 27 || layout.getInfoButton() >= 27 || layout.getSortButton() >= 27;
    }

    public static boolean needsReset36(Layout layout) {
        for (Integer i : layout.getListingItems()) {
            if (i >= 36) return true;
        }
        for (Integer i : layout.getSpacerItems()) {
            if (i >= 36) return true;
        }
        return layout.getAdminButton() >= 36 || layout.getExitButton() >= 36 || layout.getPreviousPageButton() >= 36 || layout.getNextPageButton() >= 36 || layout.getSearchButton() >= 36 || layout.getExpiredItemsButton() >= 36 || layout.getInfoButton() >= 36 || layout.getSortButton() >= 36;
    }

    public static boolean needsReset45(Layout layout) {
        for (Integer i : layout.getListingItems()) {
            if (i >= 45) return true;
        }
        for (Integer i : layout.getSpacerItems()) {
            if (i >= 45) return true;
        }
        return layout.getAdminButton() >= 45 || layout.getExitButton() >= 45 || layout.getPreviousPageButton() >= 45 || layout.getNextPageButton() >= 45 || layout.getSearchButton() >= 45 || layout.getExpiredItemsButton() >= 45 || layout.getInfoButton() >= 45 || layout.getSortButton() >= 45;
    }

    public static void setDefault27(Layout layout) {
        List<Integer> listingSlots = new ArrayList<>();
        List<Integer> spacerSlots = new ArrayList<>();

        for (int i = 10; i <= 16; i++) {
            listingSlots.add(i);
        }

        spacerSlots.add(1);
        spacerSlots.add(2);
        spacerSlots.add(3);
        spacerSlots.add(5);
        spacerSlots.add(6);
        spacerSlots.add(7);
        spacerSlots.add(19);
        spacerSlots.add(20);
        spacerSlots.add(21);
        spacerSlots.add(23);
        spacerSlots.add(24);
        spacerSlots.add(25);

        layout.setSpacerItems(spacerSlots);
        layout.setListingItems(listingSlots);

        layout.setInventorySize(27);
        layout.setAdminButton(4);
        layout.setExitButton(8);
        layout.setPreviousPageButton(9);
        layout.setNextPageButton(17);
        layout.setSearchButton(0);
        layout.setExpiredItemsButton(26);
        layout.setInfoButton(22);
        layout.setSortButton(18);

        layout.saveLayout();
    }

    public static void setDefault36(Layout layout) {
        List<Integer> listingSlots = new ArrayList<>();
        List<Integer> spacerSlots = new ArrayList<>();

        for (int i = 10; i <= 16; i++) {
            listingSlots.add(i);
        }
        for (int i = 19; i <= 25; i++) {
            listingSlots.add(i);
        }

        spacerSlots.add(0);
        spacerSlots.add(2);
        spacerSlots.add(3);
        spacerSlots.add(5);
        spacerSlots.add(6);
        spacerSlots.add(7);
        spacerSlots.add(27);
        spacerSlots.add(28);
        spacerSlots.add(29);
        spacerSlots.add(30);
        spacerSlots.add(32);
        spacerSlots.add(33);
        spacerSlots.add(34);
        spacerSlots.add(35);

        layout.setSpacerItems(spacerSlots);
        layout.setListingItems(listingSlots);

        layout.setInventorySize(36);

        layout.setAdminButton(1);
        layout.setExitButton(8);
        layout.setPreviousPageButton(18);
        layout.setNextPageButton(26);
        layout.setSearchButton(9);
        layout.setExpiredItemsButton(17);
        layout.setInfoButton(4);
        layout.setSortButton(31);

        layout.saveLayout();
    }

    public static void setDefault45(Layout layout) {
        List<Integer> listingSlots = new ArrayList<>();
        List<Integer> spacerSlots = new ArrayList<>();

        for (int i = 10; i <= 16; i++) {
            listingSlots.add(i);
        }
        for (int i = 19; i <= 25; i++) {
            listingSlots.add(i);
        }
        for (int i = 28; i <= 34; i++) {
            listingSlots.add(i);
        }

        spacerSlots.add(0);
        spacerSlots.add(1);
        spacerSlots.add(2);
        spacerSlots.add(3);
        spacerSlots.add(5);
        spacerSlots.add(6);
        spacerSlots.add(7);
        spacerSlots.add(9);
        spacerSlots.add(17);
        spacerSlots.add(27);
        spacerSlots.add(35);
        spacerSlots.add(36);
        spacerSlots.add(38);
        spacerSlots.add(40);
        spacerSlots.add(42);
        spacerSlots.add(44);

        layout.setSpacerItems(spacerSlots);
        layout.setListingItems(listingSlots);

        layout.setInventorySize(45);

        layout.setAdminButton(4);
        layout.setExitButton(8);
        layout.setPreviousPageButton(18);
        layout.setNextPageButton(26);
        layout.setSearchButton(37);
        layout.setInfoButton(39);
        layout.setExpiredItemsButton(41);
        layout.setSortButton(43);

        layout.saveLayout();
    }

    public static void setDefault54(Layout layout) {
        List<Integer> listingSlots = new ArrayList<>();
        List<Integer> spacerSlots = new ArrayList<>();
        for (int i = 9; i <= 44; i++) {
            listingSlots.add(i);
        }
        for (int i = 0; i <= 7; i++) {
            spacerSlots.add(i);
        }
        for (int i = 45; i <= 53; i++) {
            spacerSlots.add(i);
        }
        layout.setListingItems(listingSlots);
        layout.setSpacerItems(spacerSlots);

        layout.setInventoryName("&6&lAuction&f&lHouse");
        layout.setInventorySize(54);

        layout.setAdminButton(1);
        layout.setExitButton(8);
        layout.setPreviousPageButton(45);
        layout.setNextPageButton(53);
        layout.setSearchButton(46);
        layout.setInfoButton(48);
        layout.setExpiredItemsButton(50);
        layout.setSortButton(52);
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (LayoutEditGUI.getLayoutNameEdit().containsKey(player.getUniqueId()) || LayoutEditGUI.getHelpMessage().containsKey(player.getUniqueId()) || LayoutEditGUI.getDisplayNameEdit().containsKey(player.getUniqueId()) || LayoutEditGUI.getInventorySizeEdit().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (LayoutEditGUI.getLayoutNameEdit().containsKey(player.getUniqueId()) || LayoutEditGUI.getHelpMessage().containsKey(player.getUniqueId()) || LayoutEditGUI.getDisplayNameEdit().containsKey(player.getUniqueId()) || LayoutEditGUI.getInventorySizeEdit().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (LayoutEditGUI.getLayoutNameEdit().containsKey(player.getUniqueId()) || LayoutEditGUI.getHelpMessage().containsKey(player.getUniqueId()) || LayoutEditGUI.getDisplayNameEdit().containsKey(player.getUniqueId()) || LayoutEditGUI.getInventorySizeEdit().containsKey(player.getUniqueId())) {

            LayoutEditGUI gui;

            if (LayoutEditGUI.getLayoutNameEdit().containsKey(player.getUniqueId())) {
                gui = LayoutEditGUI.getLayoutNameEdit().get(player.getUniqueId());
            } else if (LayoutEditGUI.getHelpMessage().containsKey(player.getUniqueId())) {
                gui = LayoutEditGUI.getHelpMessage().get(player.getUniqueId());
            } else if (LayoutEditGUI.getDisplayNameEdit().containsKey(player.getUniqueId())) {
                gui = LayoutEditGUI.getDisplayNameEdit().get(player.getUniqueId());
            } else {
                gui = LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId());
            }

            gui.restoreInventory(false);

            LayoutEditGUI.getLayoutNameEdit().remove(player.getUniqueId());
            LayoutEditGUI.getHelpMessage().remove(player.getUniqueId());
            LayoutEditGUI.getDisplayNameEdit().remove(player.getUniqueId());
            LayoutEditGUI.getInventorySizeEdit().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) e.getEntity();
        if (LayoutEditGUI.getLayoutNameEdit().containsKey(player.getUniqueId()) || LayoutEditGUI.getHelpMessage().containsKey(player.getUniqueId()) || LayoutEditGUI.getDisplayNameEdit().containsKey(player.getUniqueId()) || LayoutEditGUI.getInventorySizeEdit().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String input = e.getMessage();
        Chat chat = AuctionHouse.getInstance().getChat();
        User user = AuctionHouse.getInstance().getUserManager().getUser(player);

        //Editor Guide
        if (LayoutEditGUI.getHelpMessage().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            if (input.equalsIgnoreCase("next") || input.equalsIgnoreCase("n")) {
                if (LayoutEditGUI.getHelpPage().get(player.getUniqueId()) == 3) {
                    chat.sendMessage(player, "&eYou are on the last page!");
                    return;
                }
                Bukkit.getScheduler().cancelTask(taskMap.get(player.getUniqueId()));
                int newPage = LayoutEditGUI.getHelpPage().get(player.getUniqueId()) + 1;
                sendHelpMessage(player, newPage);
                LayoutEditGUI.getHelpPage().remove(player.getUniqueId());
                LayoutEditGUI.getHelpPage().put(player.getUniqueId(), newPage);
            } else if (input.equalsIgnoreCase("previous") || input.equalsIgnoreCase("p")) {
                if (LayoutEditGUI.getHelpPage().get(player.getUniqueId()) == 1) {
                    chat.sendMessage(player, "&eYou are on the first page!");
                    return;
                }
                Bukkit.getScheduler().cancelTask(taskMap.get(player.getUniqueId()));
                int newPage = LayoutEditGUI.getHelpPage().get(player.getUniqueId()) - 1;
                sendHelpMessage(player, newPage);
                LayoutEditGUI.getHelpPage().remove(player.getUniqueId());
                LayoutEditGUI.getHelpPage().put(player.getUniqueId(), newPage);
            } else if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("exit")) {
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    LayoutEditGUI.getHelpMessage().get(player.getUniqueId()).returnFromHelp();
                    Bukkit.getScheduler().cancelTask(taskMap.get(player.getUniqueId()));
                });
            } else {
                switch (LayoutEditGUI.getHelpPage().get(player.getUniqueId())) {
                    case 1:
                        chat.sendRawMessage(player, "&eType \"next\" for next page or \"cancel\" to go back to the editor.");
                        break;
                    case 2:
                        chat.sendRawMessage(player, "&eType \"previous\" for previous page, \"next\" for the next page, or \"cancel\" to go back to the editor.");
                        break;
                    case 3:
                        chat.sendRawMessage(player, "&eType \"previous\" for previous page or \"cancel\" to go back to the editor.");
                        break;
                }
            }
        }
        //Layout Name Edit
        else if (LayoutEditGUI.getLayoutNameEdit().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            LayoutEditGUI.getLayoutNameEdit().get(player.getUniqueId()).getLayout().setName(input);
            chat.sendMessage(player, "&fYou have changed the name of this layout to &e&n" + input + "&f.");
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> LayoutEditGUI.getLayoutNameEdit().get(player.getUniqueId()).returnFromLayoutName());
        }
        //Display Name Edit
        else if (LayoutEditGUI.getDisplayNameEdit().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            LayoutEditGUI.getDisplayNameEdit().get(player.getUniqueId()).getLayout().setInventoryName(input);
            chat.sendMessage(player, "&fYou have changed the name of this layout to \"&r" + input + "&f\".");
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> LayoutEditGUI.getDisplayNameEdit().get(player.getUniqueId()).returnFromDisplayName());
        }
        //Inventory Size Edit
        else if (LayoutEditGUI.getInventorySizeEdit().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            Layout layout = LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId()).getLayout();
            if (input.equalsIgnoreCase("confirm")) {
                if (confirm27.contains(player.getUniqueId())) {
                    confirm27.remove(player.getUniqueId());
                    setDefault27(layout);
                    Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId()).returnFromInventorySizeEdit());
                } else if (confirm36.contains(player.getUniqueId())) {
                    confirm36.remove(player.getUniqueId());
                    setDefault36(layout);
                    Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId()).returnFromInventorySizeEdit());
                } else if (confirm45.contains(player.getUniqueId())) {
                    confirm45.remove(player.getUniqueId());
                    setDefault45(layout);
                    Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId()).returnFromInventorySizeEdit());
                }
                return;
            }
            switch (input) {
                case "27":
                    confirm27.remove(player.getUniqueId());
                    confirm36.remove(player.getUniqueId());
                    confirm45.remove(player.getUniqueId());
                    if (needsReset27(layout)) {
                        chat.sendMessage(player, "&c&lCAUTION! &eYour current Auction House layout does not support an inventory size of 27. To set this layout to the default size 27 inventory, type \"confirm\". If not " + "please select another size or use \"cancel\" to return to the editor.");
                        confirm27.add(player.getUniqueId());
                        return;
                    } else {
                        setDefault27(layout);
                        Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId()).returnFromInventorySizeEdit());
                    }
                    return;
                case "36":
                    confirm27.remove(player.getUniqueId());
                    confirm36.remove(player.getUniqueId());
                    confirm45.remove(player.getUniqueId());
                    if (needsReset36(layout)) {
                        chat.sendMessage(player, "&c&lCAUTION! &eYour current Auction House layout does not support an inventory size of 36. To set this layout to the default size 36 inventory, type \"confirm\". If not " + "please select another size or use \"cancel\" to return to the editor.");
                        confirm36.add(player.getUniqueId());
                        return;
                    } else {
                        setDefault36(layout);
                        Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId()).returnFromInventorySizeEdit());
                    }
                    return;
                case "45":
                    confirm27.remove(player.getUniqueId());
                    confirm36.remove(player.getUniqueId());
                    confirm45.remove(player.getUniqueId());
                    if (needsReset45(layout)) {
                        chat.sendMessage(player, "&c&lCAUTION! &eYour current Auction House layout does not support an inventory size of 45. To set this layout to the default size 45 inventory, type \"confirm\". If not " + "please select another size or use \"cancel\" to return to the editor.");
                        confirm45.add(player.getUniqueId());
                        return;
                    } else {
                        setDefault45(layout);
                        Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId()).returnFromInventorySizeEdit());
                    }
                    return;
                case "54":
                    layout.setInventorySize(54);
                    Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId()).returnFromInventorySizeEdit());
                    return;
            }
            chat.sendMessage(player, "&6&n" + input + "&e is not a valid input. Please specify whether you'd like a 27, 36, 45, or 54 size Auction House...");
        }
    }

}
