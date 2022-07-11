package net.akarian.auctionhouse.guis.admin;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.SortType;
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

public class ShulkerViewAdminGUI implements AkarianInventory {

    private final Listing listing;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final int mainPage;
    private final SortType sortType;
    private final boolean sortBool;
    private final String search;

    public ShulkerViewAdminGUI(Listing listing, SortType sortType, boolean sortBool, int mainPage, String search) {
        this.listing = listing;
        this.sortType = sortType;
        this.sortBool = sortBool;
        this.mainPage = mainPage;
        this.search = search;
    }

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {

        if (slot == 8) {
            player.openInventory(new AuctionHouseAdminGUI(player, sortType, sortBool, mainPage).search(search).getInventory());
        } else {
            return;
        }

    }

    @Override
    public Inventory getInventory() {

        Inventory inv = Bukkit.createInventory(this, 36, chat.format("&4Admin Shulker View"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, "&c&lReturn", Collections.singletonList("&7&oReturn the AuctionHouse.")));

        int start = 9;

        if (listing.getItemStack().getItemMeta() instanceof BlockStateMeta) {
            BlockStateMeta im = (BlockStateMeta) listing.getItemStack().getItemMeta();
            if (im.getBlockState() instanceof ShulkerBox) {
                ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                for (ItemStack itemStack : shulker.getInventory().getContents()) {
                    if (itemStack != null) {
                        inv.setItem(start, itemStack);
                        start++;
                    }
                }
            }
        }
        return inv;
    }
}
