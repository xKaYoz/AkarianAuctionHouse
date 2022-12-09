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
                int newPage = LayoutEditGUI.getHelpPage().get(p.getUniqueId()) + 1;
                LayoutEditGUI.getHelpPage().put(p.getUniqueId(), newPage);
                LayoutEditGUI.getHelpPage().remove(p.getUniqueId(), newPage - 1);
            }
        }
    }

    public void sendHelpMessage(Player player, int page) {
        Chat chat = AuctionHouse.getInstance().getChat();
        switch (page) {
            case 1:
                chat.sendMessage(player, "");
                break;
            case 2:
                break;
        }
    }

}
