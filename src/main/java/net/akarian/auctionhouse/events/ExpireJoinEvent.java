package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class ExpireJoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        List<Listing> expired = AuctionHouse.getInstance().getListingManager().getUnclaimedExpired(p.getUniqueId());
        Chat chat = AuctionHouse.getInstance().getChat();

        if(expired.size() > 0) {
            chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getExpiredJoinMessage().replace("%amount%", expired.size() + ""));
        }
    }
}
