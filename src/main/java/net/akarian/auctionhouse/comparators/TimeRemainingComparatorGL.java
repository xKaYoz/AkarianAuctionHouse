package net.akarian.auctionhouse.comparators;

import net.akarian.auctionhouse.listings.Listing;

import java.util.Comparator;

public class TimeRemainingComparatorGL implements Comparator<Listing> {
    @Override
    public int compare(Listing listing1, Listing listing2) {
        return ((int) ((listing2.getStart() + (86400 * 1000)) - System.currentTimeMillis()) / 1000) - ((int) ((listing1.getStart() + (86400 * 1000)) - System.currentTimeMillis()) / 1000);
    }
}
