package net.akarian.auctionhouse.events.aahEvents;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.events.ListingBoughtEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class ListingBoughtEvents implements Listener {

    Chat chat = AuctionHouse.getInstance().getChat();

    @EventHandler
    public void onBuy(ListingBoughtEvent event) {
        Listing listing = event.getListing();

        for (User u : AuctionHouse.getInstance().getUserManager().getUsers()) {
            if (u.getUserSettings().isAlertListingBought() && (!listing.getBuyer().toString().equals(u.getUuid().toString()) && !listing.getCreator().toString().equals(u.getUuid().toString())))
                chat.sendMessage(Objects.requireNonNull(Bukkit.getPlayer(u.getUuid())), AuctionHouse.getInstance().getMessages().getSt_bought_message().replace("%buyer%", Bukkit.getPlayer(listing.getBuyer()).getName()).replace("%price%", chat.formatMoney(listing.getPrice()))
                        .replace("%seller%", AuctionHouse.getInstance().getNameManager().getName(listing.getCreator())).replace("%listing%", chat.formatItem(listing.getItemStack())));
        }
    }

}
