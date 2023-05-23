package net.akarian.auctionhouse.comparators;

import net.akarian.auctionhouse.listings.Listing;

import java.util.Comparator;

public class TimeExpiredComparatorGL implements Comparator<Listing> {
    @Override
    public int compare(Listing listing1, Listing listing2) {
        return Long.compare(listing2.getEnd(), listing1.getEnd());
    }
}
