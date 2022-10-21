package net.akarian.auctionhouse.utils.events;

import lombok.Getter;
import net.akarian.auctionhouse.listings.Listing;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ListingBoughtEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Listing listing;

    public ListingBoughtEvent(Listing listing) {
        this.listing = listing;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
