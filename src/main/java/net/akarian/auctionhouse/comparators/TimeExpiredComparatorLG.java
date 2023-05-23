package net.akarian.auctionhouse.comparators;

import net.akarian.auctionhouse.listings.Listing;

import java.util.Comparator;

public class TimeExpiredComparatorLG implements Comparator<Listing> {
    @Override
    public int compare(Listing listing1, Listing listing2) {
        return Long.compare(listing1.getEnd(), listing2.getEnd());
    }

}
