package net.akarian.auctionhouse.guis.admin.database.transfer;

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
    private Inventory inv;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private boolean finished;
    private boolean started;
    private boolean failed;
    private int active;
    private int complete;
    private int expired;
    private int users;

    public DatabaseTransferStatusGUI(Player player) {
        this.player = player;
        this.finished = false;
        this.started = false;
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
    public void updateInventory() {
        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        for (int i = 17; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        if (finished) {
            for (int i = 10; i <= 16; i++) {
                inv.setItem(i, ItemBuilder.build(Material.EMERALD_BLOCK, 1, "&a&lTransfer Complete!", Arrays.asList("&7Transferred &e" + complete + "&7 completed auction listings.", "&7Transferred &e" + active + "&7 active auction listings.", "&7Transferred &e" + expired + "&7 expired listings.", "&7Transferred &e" + users + "&7 users.")));
            }
        } else if (failed) {
            for (int i = 10; i <= 16; i++) {
                inv.setItem(i, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&c&lClick to restart connection", Arrays.asList("&eLeft click to restart database prompts.", "&eRight click to retry connection.", "", "&6For more detailed info check console.")));
            }
        } else {
            for (int i = 10; i <= 16; i++) {
                inv.setItem(i, ItemBuilder.build(Material.EMERALD_BLOCK, 1, "&a&lClick to start transfer", Arrays.asList("&eClick to start database transfer!", "&7You can close and use", "&7\"/aha database\" to reopen.")));
            }
        }
    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format("&a&lBegin Data Transfer"));

        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        for (int i = 10; i <= 16; i++) {
            inv.setItem(i, ItemBuilder.build(Material.EMERALD_BLOCK, 1, "&a&lClick to start transfer", Arrays.asList("&eClick to start database transfer!", "&7You can close and use", "&7\"/aha database\" to reopen.")));
        }
        for (int i = 17; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        player.openInventory(inv);
        return inv;
    }

    public Inventory connectionDisapproved() {
        inv = Bukkit.createInventory(this, 27, chat.format("&4&lConnection Unsuccessful!"));
        failed = true;

        for (int i = 10; i <= 16; i++) {
            inv.setItem(i, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&c&lClick to restart connection", Arrays.asList("&eLeft click to restart database prompts.", "&eRight click to retry connection.", "", "&6For more detailed info check console.")));
        }

        return inv;
    }

    public Inventory transferComplete(int active, int complete, int expired, int users) {
        inv = Bukkit.createInventory(this, 27, chat.format("&a&lTransfer Complete"));

        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        for (int i = 10; i <= 16; i++) {
            inv.setItem(i, ItemBuilder.build(Material.EMERALD_BLOCK, 1, "&a&lTransfer Complete!", Arrays.asList("&7Transferred &e" + complete + "&7 completed auction listings.", "&7Transferred &e" + active + "&7 active auction listings.", "&7Transferred &e" + expired + "&7 expired listings.", "&7Transferred &e" + users + "&7 users.")));
        }
        for (int i = 17; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        finished = true;
        this.active = active;
        this.complete = complete;
        this.expired = expired;
        this.users = users;

        return inv;
    }
}
