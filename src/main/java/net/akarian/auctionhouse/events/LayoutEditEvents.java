package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.edit.LayoutEditGUI;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class LayoutEditEvents implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String input = e.getMessage();
        Chat chat = AuctionHouse.getInstance().getChat();
        User user = AuctionHouse.getInstance().getUserManager().getUser(p);

        if(LayoutEditGUI.getHelpMessage().containsKey(p.getUniqueId())) {
            if(input.equalsIgnoreCase("next") || input.equalsIgnoreCase("n")) {
                if(LayoutEditGUI.getHelpPage().get(p.getUniqueId()) == 2) {
                    chat.sendMessage(p, "&eYou are on the last page!");
                    return;
                }
                int newPage = LayoutEditGUI.getHelpPage().get(p.getUniqueId()) + 1;
                sendHelpMessage(p, newPage);
                LayoutEditGUI.getHelpPage().remove(p.getUniqueId());
                LayoutEditGUI.getHelpPage().put(p.getUniqueId(), newPage);
            }else if(input.equalsIgnoreCase("previous") || input.equalsIgnoreCase("p")) {
                if(LayoutEditGUI.getHelpPage().get(p.getUniqueId()) == 2) {
                    chat.sendMessage(p, "&eYou are on the last page!");
                    return;
                }
                int newPage = LayoutEditGUI.getHelpPage().get(p.getUniqueId()) - 1;
                sendHelpMessage(p, newPage);
                LayoutEditGUI.getHelpPage().remove(p.getUniqueId());
                LayoutEditGUI.getHelpPage().put(p.getUniqueId(), newPage);
            } else if(input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("exit")) {
                LayoutEditGUI.getHelpPage().remove(p.getUniqueId());
                p.openInventory(LayoutEditGUI.getHelpMessage().get(p.getUniqueId()).getInventory());
            }
        }
    }

    public static void sendHelpMessage(Player player, int page) {
        Chat chat = AuctionHouse.getInstance().getChat();
        switch (page) {
            case 1:
                chat.sendMessage(player, "&eWelcome to the Auction House Editor Guide!");
                chat.sendMessage(player, "&eFrom the main menu you have two options, Layout Settings and Layout Items.");
                chat.sendMessage(player, "&eIn the Layout Settings, you can customize the settings such as the size and name");
                chat.sendMessage(player, "&eunique to each layout.");
                chat.sendMessage(player, "&eIn the layout Items, you have all of the items that you can place in the auction house.");
                chat.sendMessage(player, "&eBe cautious, some items (which are marked) may only be placed once.");
                chat.sendMessage(player, " ");
                chat.sendMessage(player, "&eType \"next\" for next page or \"cancel\" to go go back to the editor.");
                break;
            case 2:
                chat.sendMessage(player, "&eReset to Default will reset the current layout to the default auction house layout.");
                chat.sendMessage(player, "&eReset to Current will reset the layout to how it was before you started editing it.");
                chat.sendMessage(player, "&eExit and Save will save the current layout and return you to the previous layout selector screen.");
                chat.sendMessage(player, "&eExit will return you to the previous layout selector screen WITHOUT saving.");
                chat.sendMessage(player, " ");
                chat.sendMessage(player, "&eType \"previous\" for previous page or \"cancel\" to go go back to the editor.");
                break;
        }
    }

}
