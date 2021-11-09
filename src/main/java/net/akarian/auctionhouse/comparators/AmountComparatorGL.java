package net.akarian.auctionhouse.comparators;

import net.akarian.auctionhouse.listings.Listing;

import java.util.Comparator;

public class AmountComparatorGL implements Comparator<Listing> {
    @Override
    public int compare(Listing listing1, Listing listing2) {
        return listing2.getItemStack().getAmount() - listing1.getItemStack().getAmount();
    }
}
