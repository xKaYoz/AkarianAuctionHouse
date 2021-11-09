package net.akarian.auctionhouse.comparators;

import net.akarian.auctionhouse.listings.Listing;

import java.util.Comparator;

public class AmountComparatorLG implements Comparator<Listing> {
    @Override
    public int compare(Listing listing1, Listing listing2) {
        return listing1.getItemStack().getAmount() - listing2.getItemStack().getAmount();
    }
}
