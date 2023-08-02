package net.akarian.auctionhouse.guis.admin.blacklist;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class BlacklistAdminGUI implements AkarianInventory {

    //TODO Lang message entire file

    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;
    private final ItemStack itemStack;

    public BlacklistAdminGUI(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {
        String itemName = chat.formatItem(itemStack);
        String[] split = itemName.split("x ", 2);
        itemName = split[1];
        switch (slot) {
            case 29:
                AuctionHouse.getInstance().getListingManager().addNameBlacklist(itemName);
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_BLACKLIST_ADD, "%item%;" + itemName));
                p.closeInventory();
                break;
            case 31:
                AuctionHouse.getInstance().getListingManager().addMaterialBlacklist(itemStack.getType());
                chat.sendMessage(p, "You have blacklisted the material " + itemStack.getType().name() + "&7.");
                p.closeInventory();
                break;
            case 33:
                AuctionHouse.getInstance().getListingManager().addItemStackBlacklist(itemStack);
                chat.sendMessage(p, "You have blacklisted the item " + itemName + "&7.");
                p.closeInventory();
                break;

        }

    }

    @Override
    public void updateInventory() {

        inv.setItem(13, itemStack);

        String itemName = chat.formatItem(itemStack);
        String[] split = itemName.split("x ", 2);
        itemName = split[1];

        ArrayList<String> itemLore = new ArrayList<>();
        itemLore.add("&7Blacklist items from being listed that are the same exact items as");
        itemLore.add("&r" + itemName);
        if (itemStack.hasItemMeta() && Objects.requireNonNull(itemStack.getItemMeta()).hasLore()) {
            for (String s : itemStack.getItemMeta().getLore()) {
                itemLore.add(chat.format(s));
            }
        }


        inv.setItem(29, ItemBuilder.build(Material.WRITABLE_BOOK, 1, "&6Blacklist by Name", Arrays.asList("&7Blacklist items from being listed with the name of", "&7" + itemName)));
        inv.setItem(31, ItemBuilder.build(itemStack.getType(), 1, "&6Blacklist by Type", Arrays.asList("&7Blacklist items from being listed that are the material of", "&7" + itemStack.getType().name())));
        inv.setItem(33, ItemBuilder.build(Material.CHEST, 1, "&6Blacklist Exact Item", itemLore));

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 45, chat.format("&cAuction House Blacklist"));

        updateInventory();

        return inv;
    }
}
