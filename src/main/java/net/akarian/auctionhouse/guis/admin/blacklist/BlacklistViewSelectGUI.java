package net.akarian.auctionhouse.guis.admin.blacklist;

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

public class BlacklistViewSelectGUI implements AkarianInventory {

    //TODO lang message entire file

    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                p.openInventory(new BlacklistMainGUI(p).getInventory());
                break;
            case 11:
                p.openInventory(new BlacklistViewNameGUI(1).getInventory());
                break;
            case 13:
                p.openInventory(new BlacklistViewMaterialGUI(1).getInventory());
                break;
            case 15:
                p.openInventory(new BlacklistViewItemsGUI(1).getInventory());
                break;
        }

    }

    @Override
    public void updateInventory() {

        inv.setItem(11, ItemBuilder.build(Material.WRITABLE_BOOK, 1, "&6View Blacklisted Names", Collections.singletonList("&7Click to view blacklisted names.")));
        inv.setItem(13, ItemBuilder.build(Material.STONE, 1, "&6View Blacklisted Materials", Collections.singletonList("&7Click to view blacklisted materials.")));
        inv.setItem(15, ItemBuilder.build(Material.CHEST, 1, "&6View Blacklisted Items", Collections.singletonList("&7Click to view blacklisted items.")));

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format("&cAuction House Blacklist"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));
        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

        return inv;
    }
}
