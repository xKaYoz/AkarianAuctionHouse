package net.akarian.auctionhouse.comparators;

import net.akarian.auctionhouse.listings.Listing;

import java.util.Comparator;

public class PriceComparatorLG implements Comparator<Listing> {
    @Override
    public int compare(Listing listing1, Listing listing2) {
        return ((int) (listing1.getPrice() * 100)) - ((int) (listing2.getPrice() * 100));
    }
}
