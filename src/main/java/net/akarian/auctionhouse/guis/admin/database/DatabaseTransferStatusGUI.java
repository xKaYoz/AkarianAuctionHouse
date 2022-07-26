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

public class DatabaseTransferStatusGUI implements AkarianInventory {

    @Getter
    private final Player player;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private boolean finished;
    private boolean started;

    public DatabaseTransferStatusGUI(Player player) {
        this.player = player;
        this.finished = false;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {
        if (!finished && !started) {
            switch (AuctionHouse.getInstance().getDatabaseType()) {
                case FILE:
                    if (slot >= 10 && slot <= 16) {
                        if (item.getType() == Material.EMERALD_BLOCK) {

                            p.closeInventory();
                            started = true;
                            AuctionHouse.getInstance().getListingManager().transferToMySQL(p, this);

                        } else if (item.getType() == Material.REDSTONE_BLOCK) {
                            ConfirmDatabaseTransfer cdb = new ConfirmDatabaseTransfer(player);
                            if (type.isLeftClick()) {
                                p.closeInventory();
                                player.sendTitle(chat.format("&6Database Setup"), chat.format("&eLeft click to keep current"), 1, 40, 1);
                                chat.sendMessage(p, "&eLeft click to keep the current selection.");

                                Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), () -> {
                                    player.sendTitle(chat.format("&6Enter Hostname"), chat.format("&7Current: " + AuctionHouse.getInstance().getConfigFile().getDb_host()), 1, 600, 1);
                                    ConfirmDatabaseTransfer.getHostMap().put(player.getUniqueId(), cdb);
                                }, 40);
                            } else if (type.isRightClick()) {
                                p.closeInventory();
                                cdb.testConnection();
                            }
                        }
                    }
                    break;
                case MYSQL:
                    p.closeInventory();
                    started = true;
                    AuctionHouse.getInstance().getListingManager().transferToFile(p, this);
                    break;
            }
        } else {
            p.closeInventory();
        }
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, chat.format("&a&lBegin Data Transfer"));

        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        for (int i = 10; i <= 16; i++) {
            inv.setItem(i, ItemBuilder.build(Material.EMERALD_BLOCK, 1, "&a&lClick to start transfer", Arrays.asList("&eClick to start database transfer!", "&7You can close and use", "\"/ah admin database\" to reopen.")));
        }
        for (int i = 17; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        player.openInventory(inv);
        return inv;
    }

    public Inventory connectionDisapproved() {
        Inventory inv = Bukkit.createInventory(this, 27, chat.format("&4&lConnection Unsuccessful!"));

        for (int i = 10; i <= 16; i++) {
            inv.setItem(i, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&c&lClick to restart connection", Arrays.asList("&eLeft click to restart database prompts.", "&eRight click to retry connection.", "", "&6For more detailed info check console.")));
        }

        return inv;
    }

    public Inventory transferComplete(int active, int complete, int expired) {
        Inventory inv = Bukkit.createInventory(this, 27, chat.format("&a&lTransfer Complete"));

        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        for (int i = 10; i <= 16; i++) {
            inv.setItem(i, ItemBuilder.build(Material.EMERALD_BLOCK, 1, "&a&lTransfer Complete!", Arrays.asList("&eTransferred " + complete + " completed auction listings.", "&eTransferred " + active + " active auction listings.", "&eTransferred " + expired + " expired listings.")));
        }
        for (int i = 17; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        finished = true;

        return inv;
    }
}
