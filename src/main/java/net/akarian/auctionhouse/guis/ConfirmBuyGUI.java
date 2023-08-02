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

public class ConfirmBuyGUI implements AkarianInventory {

    @Getter
    private final Listing listing;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;
    private final Player player;
    private final AuctionHouseGUI auctionHouseGUI;

    /**
     * Confirm you want to buy the listing
     *
     * @param player          Player buying the listing
     * @param listing         Listing item
     * @param auctionHouseGUI Instance of AuctionHouseGUI
     */
    public ConfirmBuyGUI(Player player, Listing listing, AuctionHouseGUI auctionHouseGUI) {
        this.player = player;
        this.listing = listing;
        this.auctionHouseGUI = auctionHouseGUI;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int i, ItemStack itemStack, ClickType clickType) {

        switch (itemStack.getType()) {

            case LIME_STAINED_GLASS_PANE:
                player.closeInventory();

                switch (AuctionHouse.getInstance().getListingManager().buy(listing, player)) {
                    case -1:
                        chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_LISTINGNOTFOUND));
                        break;
                    case 0:
                        chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_POOR));
                        break;
                    case 1:
                        chat.log("Listing " + chat.formatItem(listing.getItemStack()) + " has been bought by " + player.getName() + ". Creator offline.", AuctionHouse.getInstance().isDebug());
                        break;
                    case 2:
                        chat.log("Listing " + chat.formatItem(listing.getItemStack()) + " has been bought by " + player.getName() + ". Creator online.", AuctionHouse.getInstance().isDebug());
                        break;
                }
                break;
            case RED_STAINED_GLASS_PANE:
                player.openInventory(auctionHouseGUI.getInventory());
                break;
        }

    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 9, chat.format(AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_CONFIRMBUY_TITLE)));

        inv.setItem(0, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_CONFIRM_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_CONFIRM_LORE)));
        inv.setItem(1, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_CONFIRM_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_CONFIRM_LORE)));
        inv.setItem(2, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_CONFIRM_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_CONFIRM_LORE)));
        inv.setItem(3, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_CONFIRM_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_CONFIRM_LORE)));

        inv.setItem(4, listing.createActiveListing(player));

        inv.setItem(5, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_DENY_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_DENY_LORE)));
        inv.setItem(6, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_DENY_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_DENY_LORE)));
        inv.setItem(7, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_DENY_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_DENY_LORE)));
        inv.setItem(8, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_DENY_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_DENY_LORE)));


        return inv;
    }

    public void updateInventory() {
        inv.setItem(4, listing.createActiveListing(player));
    }
}
