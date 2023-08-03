package net.akarian.auctionhouse.guis.admin;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.guis.admin.database.MainDatabaseGUI;
import net.akarian.auctionhouse.guis.admin.database.transfer.DatabaseTransferStatusGUI;
import net.akarian.auctionhouse.guis.admin.edit.LayoutSelectGUI;
import net.akarian.auctionhouse.guis.admin.settings.MainSettingsGUI;
import net.akarian.auctionhouse.guis.admin.sounds.MainSoundGUI;
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

public class AuctionHouseAdminGUI implements AkarianInventory {


    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;

    @Override
    public void onGUIClick(Inventory inventory, Player player, int slot, ItemStack itemStack, ClickType clickType) {
        switch (slot) {
            case 8:
                player.closeInventory();
                break;
            case 19:
                if(player.hasPermission("auctionhouse.admin.npc")) {
                    player.openInventory(new NPCAdminGUI(player, 1).getInventory());
                }
                break;

            case 21:
                if(player.hasPermission("auctionhouse.admin.database")) {
                    if (AuctionHouse.getInstance().getMySQL().getTransferring() == null) {
                        player.openInventory(new MainDatabaseGUI(player).getInventory());
                        break;
                    }
                    if (AuctionHouse.getInstance().getMySQL().getTransferring().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                        player.openInventory(new DatabaseTransferStatusGUI(player).getInventory());
                    } else {
                        chat.sendMessage(player, "&cThe database transfer has been initialized by " + Bukkit.getOfflinePlayer(AuctionHouse.getInstance().getMySQL().getTransferring()).getName() + ".");
                    }
                }
                break;
            case 23:
                if(player.hasPermission("auctionhouse.admin.manage")) {
                    player.openInventory(new AuctionHouseGUI(player, SortType.TIME_LEFT, true, 1).adminMode().getInventory());
                }
                break;
            case 25:
                if(player.hasPermission("auctionhouse.admin.reload")) {
                    if (clickType.isRightClick() && clickType.isShiftClick()) {
                        player.closeInventory();
                        player.performCommand("aha reload");
                    }
                }
                break;
            case 29:
                if (player.hasPermission("auctionhouse.admin.settings")) {
                    player.openInventory(new MainSettingsGUI().getInventory());
                }
                break;
            case 31:
                if (player.hasPermission("auctionhouse.admin.sounds")) {
                    player.openInventory(new MainSoundGUI(player).getInventory());
                }
                break;
            case 33:
                if (player.hasPermission("auctionhouse.admin.edit")) {
                    player.openInventory(new LayoutSelectGUI(player, 1).getInventory());
                }
                break;
        }
    }

    @Override
    public void updateInventory() {

    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format(AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAINADMIN_TITLE)));

        //Top Lining
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        //Close Button
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_CLOSE_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_CLOSE_LORE)));

        //inv.setItem(19, ItemBuilder.build(Material.WRITABLE_BOOK, 1, "&6Messages", Collections.singletonList("&7Click to edit the plugin's messages file.")));
        inv.setItem(19, ItemBuilder.build(Material.PLAYER_HEAD, 1, "&6Manage NPCs", Collections.singletonList("&7Click to manage all AuctionHouse NPCs.")));
        inv.setItem(21, ItemBuilder.build(Material.BOOKSHELF, 1, "&6Database", Collections.singletonList("&7Click to manage and view the plugin's database.")));
        inv.setItem(23, ItemBuilder.build(Material.GOLD_NUGGET, 1, "&6Manage Auction House", Collections.singletonList("&7Click to open the Auction House in Admin Mode.")));
        inv.setItem(25, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&4Reload Plugin Files", Collections.singletonList("&7Shift + Right Click to reload plugin files.")));
        inv.setItem(29, ItemBuilder.build(Material.BOOK, 1, "&6Admin Settings", Collections.singletonList("&7Click to manage the server and default player settings.")));
        inv.setItem(31, ItemBuilder.build(Material.MUSIC_DISC_BLOCKS, 1, "&6Edit Sounds", Collections.singletonList("&7Click to manage server sounds.")));
        inv.setItem(33, ItemBuilder.build(Material.ANVIL, 1, "&6Edit Auction House", Collections.singletonList("&7Click to edit the layout of the auction house.")));


        //Bottom Lining
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        return inv;
    }
}
