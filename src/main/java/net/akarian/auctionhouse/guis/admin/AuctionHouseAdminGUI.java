package net.akarian.auctionhouse.guis.admin;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.guis.admin.database.MainDatabaseGUI;
import net.akarian.auctionhouse.guis.admin.database.transfer.DatabaseTransferStatusGUI;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class AuctionHouseAdminGUI implements AkarianInventory {


    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {
        switch (slot) {
            case 8:
                player.closeInventory();
                return;
            case 19:
                return;
            case 21:
                if (AuctionHouse.getInstance().getMySQL().getTransferring() == null) {
                    player.openInventory(new MainDatabaseGUI(player).getInventory());
                    return;
                }
                if (AuctionHouse.getInstance().getMySQL().getTransferring().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                    player.openInventory(new DatabaseTransferStatusGUI(player).getInventory());
                } else {
                    chat.sendMessage(player, "&cThe database transfer has been initialized by " + Bukkit.getOfflinePlayer(AuctionHouse.getInstance().getMySQL().getTransferring()).getName() + ".");
                }
                return;
            case 23:
                player.openInventory(new AuctionHouseGUI(player, SortType.TIME_LEFT, true, 1).adminMode().getInventory());
                return;
            case 25:
                if (clickType.isRightClick() && clickType.isShiftClick()) {
                    player.closeInventory();
                    chat.sendMessage(player, "&7Reloading...");
                    AuctionHouse.getInstance().getConfigFile().reloadConfig();
                    AuctionHouse.getInstance().getMessages().reloadMessages();
                    chat.setPrefix(AuctionHouse.getInstance().getConfigFile().getPrefix());
                    chat.sendMessage(player, "&aReload complete.");
                }
                return;
            case 31:
                return;
        }
    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format(AuctionHouse.getInstance().getMessages().getGui_aha_title()));

        //Top Lining
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        //Close Button
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_cn(), AuctionHouse.getInstance().getMessages().getGui_ah_cd()));

        inv.setItem(19, ItemBuilder.build(Material.WRITABLE_BOOK, 1, "&6Messages", Collections.singletonList("&7Click to edit the plugin's messages file.")));
        inv.setItem(21, ItemBuilder.build(Material.BOOKSHELF, 1, "&6Database", Collections.singletonList("&7Click to manage and view the plugin's database.")));
        inv.setItem(23, ItemBuilder.build(Material.GOLD_NUGGET, 1, "&6Manage Auction House", Collections.singletonList("&7Click to open the Auction House in Admin Mode.")));
        inv.setItem(25, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&4Reload Plugin Files", Collections.singletonList("&7Shift + Right Click to reload plugin files.")));
        inv.setItem(31, ItemBuilder.build(Material.PLAYER_HEAD, 1, "&6Manage NPCs", Collections.singletonList("&7Click to manage all AuctionHouse NPCs.")));


        //Bottom Lining
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        return inv;
    }
}
