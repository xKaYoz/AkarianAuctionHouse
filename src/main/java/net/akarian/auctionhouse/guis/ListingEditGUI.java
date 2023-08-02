package net.akarian.auctionhouse.guis;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class ListingEditGUI implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, ListingEditGUI> priceMap = new HashMap<>();
    @Getter
    private static final HashMap<UUID, ListingEditGUI> amountMap = new HashMap<>();
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    @Getter
    private final Listing listing;
    @Getter
    private Inventory inv;
    private final AuctionHouseGUI auctionHouseGUI;

    /**
     * Edit listing GUI
     *
     * @param player          Player editing listing
     * @param listing         Listing to edit
     * @param auctionHouseGUI Instance of AuctionHouseGUI
     */
    public ListingEditGUI(Player player, Listing listing, AuctionHouseGUI auctionHouseGUI) {
        this.listing = listing;
        this.player = player;
        this.auctionHouseGUI = auctionHouseGUI;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {
        switch (slot) {
            case 8:
                player.openInventory(auctionHouseGUI.getInventory());
                break;
            case 11:
                chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_LISTINGEDIT_PRICE_MESSAGE));
                priceMap.put(player.getUniqueId(), this);
                player.closeInventory();
                break;
            case 15:
                chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_LISTINGEDIT_AMOUNT_MESSAGE));
                amountMap.put(player.getUniqueId(), this);
                player.closeInventory();
                break;
        }
    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format(AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_LISTINGEDIT_TITLE)));
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));

        inv.setItem(11, ItemBuilder.build(Material.PAPER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_LISTINGEDIT_PRICE_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_LISTINGEDIT_PRICE_LORE)));
        inv.setItem(13, listing.createActiveListing(player));
        inv.setItem(15, ItemBuilder.build(Material.ANVIL, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_LISTINGEDIT_AMOUNT_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_LISTINGEDIT_AMOUNT_LORE)));

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        return inv;
    }

    public void updateInventory() {
        inv.setItem(13, listing.createActiveListing(player));
    }

}
