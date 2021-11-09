package net.akarian.auctionhouse.comparators;

import net.akarian.auctionhouse.listings.Listing;

import java.util.Comparator;

public class CostPerComparatorLG implements Comparator<Listing> {
    @Override
    public int compare(Listing listing1, Listing listing2) {
        return ((int) ((listing1.getPrice() / listing1.getItemStack().getAmount()) * 100)) - ((int) ((listing2.getPrice() / listing2.getItemStack().getAmount()) * 100));
    }
}
