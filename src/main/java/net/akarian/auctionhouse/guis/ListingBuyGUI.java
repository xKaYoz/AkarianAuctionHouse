package net.akarian.auctionhouse.guis;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class ListingBuyGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    @Getter
    private final Listing listing;
    private final AuctionHouseGUI auctionHouseGUI;
    @Getter
    private Inventory inv;
    @Getter
    private final HashMap<UUID, ListingBuyGUI> biddingMap;

    public ListingBuyGUI(Player player, Listing listing, AuctionHouseGUI gui) {
        this.player = player;
        this.listing = listing;
        this.auctionHouseGUI = gui;
        biddingMap = new HashMap<>();
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                player.openInventory(auctionHouseGUI.getInventory());
                break;
            case 28:
                if (!getListing().isBiddable()) { //Custom Buy Now Offer
                    //TODO send custom buy now offer
                }
                break;
            case 30:
                if (getListing().isBiddable()) { //Buy Now
                    p.openInventory(new ConfirmBuyGUI(player, listing, this).getInventory());
                } else { //Custom Buy Now Offer
                    //TODO send custom buy now offer
                }
                break;
            case 32: //Bid Now
                if (getListing().isBiddable()) {
                    if (listing.getCurrentBidder() != null && listing.getCurrentBidder().equals(player.getUniqueId())) { //Can't make bid higher
                        return;
                    }
                    switch (AuctionHouse.getInstance().getListingManager().bid(listing, player.getUniqueId(), listing.getCurrentBid() + listing.getMinimumIncrement())) {
                        case -1:
                            chat.sendMessage(player, "&cThe listing is no longer active.");
                            break;
                        case 1:
                            chat.sendMessage(player, "&cYou are too poor!");
                            break;
                        case 2:
                            chat.log("complete", true);
                    }
                } else { //Buy Now
                    p.openInventory(new ConfirmBuyGUI(player, listing, this).getInventory());
                }
                break;
            case 34:
                if (getListing().isBiddable()) { //Custom Bid
                    if (listing.getCurrentBidder() != null && listing.getCurrentBidder().equals(player.getUniqueId())) { //Can't make bid higher
                        return;
                    }
                    //TODO Add to lang
                    chat.sendMessage(player, "&eEnter your bid...");
                    biddingMap.put(player.getUniqueId(), this);
                }
                break;
        }

    }

    @Override
    public void updateInventory() {

        if (!listing.isActive()) {
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> player.openInventory(auctionHouseGUI.getInventory()));
            //TODO Config Chat Message
            chat.sendMessage(player, "&eThe auction you were viewing was bought or has expired.");
            return;
        }

        inv.setItem(13, listing.createActiveListing(player));

        if (getListing().isBiddable()) {
            inv.setItem(30, ItemBuilder.build(Material.GOLD_INGOT, 1, "&6Buy Now", Collections.singletonList("&7Buy now for &2" + chat.formatMoney(listing.getBuyNowPrice()) + "&7.")));
            if (getListing().getCurrentBidder() != null) {
                if (!getListing().getCurrentBidder().equals(player.getUniqueId())) {
                    inv.setItem(32, ItemBuilder.build(Material.IRON_NUGGET, 1, "&6Bid Now", Collections.singletonList("&7Make a bid of &2" + chat.formatMoney(listing.getCurrentBid() + listing.getMinimumIncrement()) + "&7.")));
                } else {
                    inv.setItem(32, ItemBuilder.build(Material.BARRIER, 1, "&cYou already have to highest bid", Collections.singletonList("&7You already have the highest bid!")));
                    inv.setItem(34, ItemBuilder.build(Material.BARRIER, 1, "&cYou already have to highest bid", Collections.singletonList("&7You already have the highest bid!")));
                }
            } else {
                inv.setItem(32, ItemBuilder.build(Material.IRON_NUGGET, 1, "&6Bid Now", Collections.singletonList("&7Make a bid of &2" + chat.formatMoney(listing.getCurrentBid() + listing.getMinimumIncrement()) + "&7.")));
            }
        } else {
            inv.setItem(32, ItemBuilder.build(Material.GOLD_INGOT, 1, "&6Buy Now", Collections.singletonList("&7Buy now for &2" + chat.formatMoney(listing.getBuyNowPrice()) + "&7.")));
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {

        inv = Bukkit.createInventory(this, 45, chat.format("&6&lPurchase Listing"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));
        for (int i = 36; i <= 44; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        if (listing.isBiddable()) {

            inv.setItem(28, ItemBuilder.build(Material.GOLD_NUGGET, 1, "&6Make a Custom Buy Now Offer", Collections.singletonList("&7Make a custom offer to the owner to buy this at a lower price.")));
            inv.setItem(34, ItemBuilder.build(Material.IRON_INGOT, 1, "&6Make a Custom Bid", Collections.singletonList("&7Make a custom bid for this auction.")));

        } else {
            inv.setItem(30, ItemBuilder.build(Material.GOLD_NUGGET, 1, "&6Make a Custom Buy Now Offer", Collections.singletonList("&7Make a custom offer to the owner to buy this at a lower price.")));
        }

        updateInventory();

        return inv;
    }
}
