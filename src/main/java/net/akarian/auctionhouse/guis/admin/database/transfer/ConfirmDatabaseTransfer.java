package net.akarian.auctionhouse.guis.admin.database.transfer;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.database.MainDatabaseGUI;
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
import java.util.HashMap;
import java.util.UUID;

public class ConfirmDatabaseTransfer implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, ConfirmDatabaseTransfer> hostMap = new HashMap<>();
    @Getter
    private static final HashMap<UUID, ConfirmDatabaseTransfer> transferringMap = new HashMap<>();
    @Getter
    private final Player player;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;

    public ConfirmDatabaseTransfer(Player player) {
        this.player = player;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        if (slot >= 10 && slot <= 12) {
            p.closeInventory();
            switch (AuctionHouse.getInstance().getDatabaseType()) {
                case FILE:
                    player.sendTitle(chat.format("&6Database Setup"), chat.format("&eLeft click to keep current"), 1, 40, 1);

                    Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(AuctionHouse.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            player.sendTitle(chat.format("&6Enter Hostname"), chat.format("&7Current: " + AuctionHouse.getInstance().getConfigFile().getDb_host()), 1, 600, 1);
                            hostMap.put(player.getUniqueId(), ConfirmDatabaseTransfer.this);
                        }
                    }, 40);
                    break;
                case MYSQL:
                    p.openInventory(new DatabaseTransferStatusGUI(p).getInventory());
            }

        } else if (slot >= 14 && slot <= 16) {
            p.openInventory(new MainDatabaseGUI(p).getInventory());
        }
    }

    @Override
    public void updateInventory() {

    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format("&6&lConfirm Transfer"));
        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(10, ItemBuilder.build(Material.EMERALD_BLOCK, 1, "&a&lConfirm", Collections.singletonList("&7Click to confirm...")));
        inv.setItem(11, ItemBuilder.build(Material.EMERALD_BLOCK, 1, "&a&lConfirm", Collections.singletonList("&7Click to confirm...")));
        inv.setItem(12, ItemBuilder.build(Material.EMERALD_BLOCK, 1, "&a&lConfirm", Collections.singletonList("&7Click to confirm...")));

        inv.setItem(13, ItemBuilder.build(Material.ENCHANTED_BOOK, 1, "&6&lAre you sure??", Arrays.asList("&7Are you sure you want to start", "&7the database transfer process???")));

        inv.setItem(14, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&4&lCancel", Collections.singletonList("&7Click to return back to the previous page...")));
        inv.setItem(15, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&4&lCancel", Collections.singletonList("&7Click to return back to the previous page...")));
        inv.setItem(16, ItemBuilder.build(Material.REDSTONE_BLOCK, 1, "&4&lCancel", Collections.singletonList("&7Click to return back to the previous page...")));

        for (int i = 17; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        return inv;
    }

    public void testConnection() {
        inv = Bukkit.createInventory(this, 27, chat.format("&6&lAttempting to connect..."));

        for (int i = 10; i <= 16; i++) {
            inv.setItem(i, ItemBuilder.build(Material.LIGHT_GRAY_WOOL, 1, "&6Loading...", Collections.singletonList("&eYou can close this menu. It will reopen when updated.")));
        }

        player.openInventory(inv);

        AuctionHouse.getInstance().getMySQL().setTransferring(player.getUniqueId());
        transferringMap.put(player.getUniqueId(), this);
        AuctionHouse.getInstance().getMySQL().setup();
    }
}
