package net.akarian.auctionhouse.guis.admin.database;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class MainDatabaseGUI implements AkarianInventory {

    @Getter
    private final Player player;
    private final Chat chat = AuctionHouse.getInstance().getChat();

    public MainDatabaseGUI(Player player) {
        this.player = player;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {
        switch (slot) {
            case 8:
                p.closeInventory();
            case 13:
                if (type.isRightClick() && type.isShiftClick())
                    p.openInventory(new ConfirmDatabaseTransfer(p).getInventory());
                break;
            case 38:
                //TODO Open active listings
                break;
            case 40:
                //TODO Open expired listings
                break;
            case 42:
                //TODO Open completed auctions
                break;
        }

    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 45, chat.format("&6&lDatabase Menu"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_cn(), AuctionHouse.getInstance().getMessages().getGui_ah_cd()));

        inv.setItem(13, ItemBuilder.build(Material.ENCHANTED_BOOK, 1, "&6Database Information", Arrays.asList(
                "&eDatabase Type: " + AuctionHouse.getInstance().getDatabaseType().getStr(),
                "",
                "&cShift+Right click to start transfer process")));

        inv.setItem(29, ItemBuilder.build(Material.LIME_WOOL, 1, "&a&lActive Listings &e&l(" + AuctionHouse.getInstance().getListingManager().getActive().size() + ")", Collections.singletonList("&7Click to view a list of all active listings.")));

        inv.setItem(31, ItemBuilder.build(Material.RED_WOOL, 1, "&c&lExpired Listings &e&l(" + AuctionHouse.getInstance().getListingManager().getExpired().size() + ")", Arrays.asList("&7There are &e" + AuctionHouse.getInstance().getListingManager().getUnclaimed().size() + "&7 unclaimed expired listings.", "&7Click to see all expired auctions.")));

        inv.setItem(33, ItemBuilder.build(Material.GREEN_WOOL, 1, "&6&lCompleted Auctions &e(" + AuctionHouse.getInstance().getListingManager().getCompleted().size() + ")", Collections.singletonList("&7Click to see all completed auctions.")));

        for (int i = 36; i <= 44; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        return inv;
    }
}
