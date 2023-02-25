package net.akarian.auctionhouse.guis;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Collections;

public class ShulkerViewGUI implements AkarianInventory {

    private final Listing listing;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    private final AuctionHouseGUI auctionHouseGUI;
    @Getter
    private Inventory inv;

    /**
     * View shulker box listing
     *
     * @param player          Player viewing listing
     * @param listing         Listing viewing
     * @param auctionHouseGUI Instance of AuctionHouseGUI
     */
    public ShulkerViewGUI(Player player, Listing listing, AuctionHouseGUI auctionHouseGUI) {
        this.player = player;
        this.listing = listing;
        this.auctionHouseGUI = auctionHouseGUI;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {

        if(slot <= 27) {
            return;
        }

        switch (itemStack.getType()) {

            case LIME_STAINED_GLASS_PANE:
                player.closeInventory();

                switch (AuctionHouse.getInstance().getListingManager().buy(listing, player)) {
                    case -1:
                        chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getError_deleted());
                        break;
                    case 0:
                        chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getError_poor());
                        break;
                    case 1:
                        chat.log("Listing " + chat.formatItem(listing.getItemStack()) + " has been bought by " + player.getName() + ". Creator online.", AuctionHouse.getInstance().isDebug());
                        break;
                    case 2:
                        chat.log("Listing " + chat.formatItem(listing.getItemStack()) + " has been bought by " + player.getName() + ". Creator offline.", AuctionHouse.getInstance().isDebug());
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

        inv = Bukkit.createInventory(this, 45, chat.format(AuctionHouse.getInstance().getMessages().getGui_sv_title()));

        if (listing.getItemStack().getItemMeta() instanceof BlockStateMeta) {
            BlockStateMeta im = (BlockStateMeta) listing.getItemStack().getItemMeta();
            if (im.getBlockState() instanceof ShulkerBox) {
                ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                for (ItemStack itemStack : shulker.getInventory().getContents()) {
                    if (itemStack != null) {
                        inv.addItem(itemStack);
                    }
                }
            }
        }

        for (int i = 27; i <= 35; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        //Bottom Row
        inv.setItem(36, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_cn(), AuctionHouse.getInstance().getMessages().getGui_buttons_cd()));
        inv.setItem(37, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_cn(), AuctionHouse.getInstance().getMessages().getGui_buttons_cd()));
        inv.setItem(38, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_cn(), AuctionHouse.getInstance().getMessages().getGui_buttons_cd()));
        inv.setItem(39, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_cn(), AuctionHouse.getInstance().getMessages().getGui_buttons_cd()));

        inv.setItem(40, listing.createActiveListing(player));

        inv.setItem(41, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_dn(), AuctionHouse.getInstance().getMessages().getGui_buttons_dd()));
        inv.setItem(42, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_dn(), AuctionHouse.getInstance().getMessages().getGui_buttons_dd()));
        inv.setItem(43, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_dn(), AuctionHouse.getInstance().getMessages().getGui_buttons_dd()));
        inv.setItem(44, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_dn(), AuctionHouse.getInstance().getMessages().getGui_buttons_dd()));

        return inv;
    }

    public void updateInventory() {
        inv.setItem(40, listing.createActiveListing(player));
    }

}
