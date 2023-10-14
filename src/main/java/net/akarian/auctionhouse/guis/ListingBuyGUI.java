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

public class ListingBuyGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    @Getter
    private final Listing listing;
    private final AuctionHouseGUI auctionHouseGUI;
    @Getter
    private Inventory inv;

    public ListingBuyGUI(Player player, Listing listing, AuctionHouseGUI gui) {
        this.player = player;
        this.listing = listing;
        this.auctionHouseGUI = gui;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

    }

    @Override
    public void updateInventory() {

        if (!listing.isActive()) {
            player.openInventory(auctionHouseGUI.getInventory());
            //TODO Config Chat Message
            chat.sendMessage(player, "&eThe auction you were viewing was bought or has expired.");
            return;
        }

        inv.setItem(13, listing.createActiveListing(player));

        inv.setItem(30, ItemBuilder.build(Material.GOLD_INGOT, 1, "&6Buy Now", Collections.singletonList("&7Buy now for &2" + chat.formatMoney(listing.getBuyNowPrice()) + "&7.")));
        inv.setItem(32, ItemBuilder.build(Material.IRON_NUGGET, 1, "&6Bid Now", Collections.singletonList("&7Make a bid of &2" + chat.formatMoney(listing.getCurrentBid() + listing.getMinimumIncrement()) + "&7.")));
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

        inv.setItem(28, ItemBuilder.build(Material.GOLD_NUGGET, 1, "&6Make a Custom Buy Now Offer", Collections.singletonList("&7Make a custom offer to the owner to buy this at a lower price.")));
        inv.setItem(34, ItemBuilder.build(Material.IRON_INGOT, 1, "&6Make a Custom Bid", Collections.singletonList("&7Make a custom bid for this auction.")));

        updateInventory();

        return inv;
    }
}
