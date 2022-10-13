package net.akarian.auctionhouse.events.aahEvents;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.events.ListingCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ListingCreateEvents implements Listener {

    Chat chat = AuctionHouse.getInstance().getChat();

    @EventHandler
    public void onCreate(ListingCreateEvent event) {
        Listing listing = event.getListing();

        for (User u : AuctionHouse.getInstance().getUserManager().getUsers()) {
            if (u.getUserSettings().isAlertCreateListings())
                chat.sendMessage(Bukkit.getPlayer(u.getUuid()), "&e" + Bukkit.getOfflinePlayer(listing.getCreator()).getName() + "&f has created a listing for &e" + chat.formatItem(listing.getItemStack()) + "&f for &2$" + chat.formatMoney(listing.getPrice()) + "&f.");
        }

    }
}
