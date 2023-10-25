package net.akarian.auctionhouse.guis.admin.settings;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class MainSettingsGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;

    public MainSettingsGUI() {

    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {
        switch (slot) {
            case 8:
                p.closeInventory();
                break;
            case 11:
                p.openInventory(new DefaultPlayerSettingsGUI().getInventory());
                break;
            case 15:
                p.openInventory(new ServerSettingsGUI().getInventory());
                break;

        }
    }

    @Override
    public void updateInventory() {

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format("&6&lAuctionHouse Admin Settings"));

        for (int i = 0; i <= 8; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_CLOSE_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_CLOSE_LORE)));

        inv.setItem(11, ItemBuilder.build(Material.DIAMOND_SWORD, 1, "&eDefault Player Settings", Collections.singletonList("&7Click to edit default player settings.")));
        inv.setItem(15, ItemBuilder.build(Material.REDSTONE, 1, "&eServer Settings", Collections.singletonList("&7Click to edit server settings.")));

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        return inv;
    }
}
