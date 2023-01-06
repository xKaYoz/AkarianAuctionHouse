package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.edit.LayoutEditGUI;
import net.akarian.auctionhouse.guis.admin.settings.ServerSettingsGUI;
import net.akarian.auctionhouse.layouts.Layout;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LayoutEditEvents implements Listener {

    private static final HashMap<UUID, Integer> taskMap = new HashMap<>();
    private static final List<UUID> confirm27 = new ArrayList<>();

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
                if (LayoutEditGUI.getHelpPage().get(player.getUniqueId()) == 2) {
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
                        chat.sendRawMessage(player, "&eType \"next\" for next page or \"cancel\" to go go back to the editor.");
                        break;
                    case 2:
                        chat.sendRawMessage(player, "&eType \"previous\" for previous page or \"cancel\" to go go back to the editor.");
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
        else if(LayoutEditGUI.getInventorySizeEdit().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            Layout layout = LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId()).getLayout();
            if(input.equalsIgnoreCase("confirm")) {
                if(confirm27.contains(player.getUniqueId())) {
                    confirm27.remove(player.getUniqueId());
                    setDefault27(layout);
                    Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> LayoutEditGUI.getInventorySizeEdit().get(player.getUniqueId()).returnFromInventorySizeEdit());
                }
                return;
            }
            switch (input) {
                case "27" :
                    if(needsReset27(layout)) {
                        chat.sendMessage(player, "&c&lCAUTION! &eYour current Auction House layout does not support an inventory size of 27. To set this layout to the default size 27 inventory, type \"confirm\". If not " +
                                "please select another size or use \"cancel\" to return to the editor.");
                        confirm27.add(player.getUniqueId());
                        break;
                    }
                    break;
                case "36" :
                    confirm27.remove(player.getUniqueId());
                    break;
            }
        }
    }

    public static void sendHelpMessage(Player player, int page) {
        Chat chat = AuctionHouse.getInstance().getChat();
        switch (page) {
            case 1:
                taskMap.put(player.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), () -> {
                    chat.sendRawMessage(player, "                  &6&lAuction House Editor Guide &7(1/2) ");
                    chat.sendRawMessage(player, "&7> &fWelcome to the Auction House Editor Guide!");
                    chat.sendRawMessage(player, "&7> &fFrom the main menu you have two options, &e&nLayout Settings&f and &e&nLayout Items&f.");
                    chat.sendRawMessage(player, "&7> &fIn the &e&nLayout Settings&f, you can customize the settings such as the size and name unique to each layout.");
                    chat.sendRawMessage(player, "&7> &fIn the &e&nLayout Items&f, you have all of the items that you can place in the auction house.");
                    chat.sendRawMessage(player, "&7> &fBe cautious, some items (which are marked) may only be placed once.");
                    chat.sendRawMessage(player, " ");
                    chat.sendRawMessage(player, "&eType \"next\" for next page or \"cancel\" to go go back to the editor.");
                }, 0, 20 * 30));
                break;
            case 2:
                taskMap.put(player.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), () -> {
                    chat.sendRawMessage(player, "                  &6&lAuction House Editor Guide &7(2/2) ");
                    chat.sendRawMessage(player, "&7> &e&nReset to Default&f will reset the current layout to the default auction house layout.");
                    chat.sendRawMessage(player, "&7> &e&nReset to Current&f will reset the layout to how it was before you started editing it.");
                    chat.sendRawMessage(player, "&7> &e&nExit and Save&f will save the current layout and return you to the previous layout selector screen.");
                    chat.sendRawMessage(player, "&7> &e&nExit&f will return you to the previous layout selector screen WITHOUT saving.");
                    chat.sendRawMessage(player, " ");
                    chat.sendRawMessage(player, "&eType \"previous\" for previous page or \"cancel\" to go go back to the editor.");
                }, 0, 20 * 30));
                break;
        }
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (LayoutEditGUI.getLayoutNameEdit().containsKey(player.getUniqueId())
                || LayoutEditGUI.getHelpMessage().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    private boolean needsReset27(Layout layout) {
        for (Integer i : layout.getListingItems()) {
            if (i >= 27) return true;
        }
        for (Integer i : layout.getSpacerItems()) {
            if (i >= 27) return true;
        }
        return layout.getAdminButton() >= 27 || layout.getExitButton() >= 27 || layout.getPreviousPageButton() >= 27 || layout.getNextPageButton() >= 27
                || layout.getSearchButton() >= 27 || layout.getExpiredItemsButton() >= 27 || layout.getInfoButton() >= 27 || layout.getSortButton() >= 27;
    }

    private void setDefault27(Layout layout) {
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

}
