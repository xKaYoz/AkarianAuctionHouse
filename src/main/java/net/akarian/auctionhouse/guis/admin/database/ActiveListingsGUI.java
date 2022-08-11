package net.akarian.auctionhouse.guis.admin.database;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.UUIDDataType;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ActiveListingsGUI implements AkarianInventory {

    private final Chat chat;
    private final int page;
    private final Player player;
    private Inventory inv;
    private List<UUID> users;

    public ActiveListingsGUI(Player player, int page) {
        this.player = player;
        this.page = page;
        this.chat = AuctionHouse.getInstance().getChat();
        this.users = new ArrayList<>();
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 45:
                p.openInventory(new ActiveListingsGUI(player, page - 1).getInventory());
                return;
            case 53:
                p.openInventory(new ActiveListingsGUI(player, page + 1).getInventory());
                return;
        }

        //Is a head
        if (slot >= 8 && slot <= 45 && item != null) {
            if (!item.hasItemMeta()) return;
            NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "uuid");
            ItemMeta itemMeta = item.getItemMeta();
            assert itemMeta != null;
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            if (container.has(key, new UUIDDataType())) {
                p.openInventory(new PlayerActiveListings(player, container.get(key, new UUIDDataType()), 1).getInventory());
            }
        }

    }

    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format("&a&lActive Listings"));
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_cn(), AuctionHouse.getInstance().getMessages().getGui_ah_cd()));
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_ppn(), AuctionHouse.getInstance().getMessages().getGui_buttons_ppd());
            inv.setItem(45, previous);
        }

        //Next Page
        if (users.size() > 36 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_npn(), AuctionHouse.getInstance().getMessages().getGui_buttons_npd());
            inv.setItem(53, next);
        }

        return inv;
    }

    public void updateInventory() {

        users = usersWithListings();

        int end = page * 36;
        int start = end - 36;
        int t = start;
        int slot = 9;

        for (int i = 9; i <= 44; i++) {
            inv.setItem(i, null);
        }

        for (int i = start; i <= end; i++) {
            if (users.size() == t || t >= end) {
                break;
            }
            UUID uuid = users.get(i);
            ItemStack itemStack;

            try {
                itemStack = new ItemStack(Material.valueOf("PLAYER_HEAD"));
            } catch (IllegalArgumentException e) {
                itemStack = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
            }
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "uuid");
            meta.getPersistentDataContainer().set(key, new UUIDDataType(), uuid);
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
            meta.setDisplayName(chat.format("&a&l" + AuctionHouse.getInstance().getNameManager().getName(uuid) + " &e&l(" + AuctionHouse.getInstance().getListingManager().getActive(uuid).size() + ")"));
            List<String> lore = new ArrayList<>();
            lore.add("&7Click to view all &e" + AuctionHouse.getInstance().getListingManager().getActive(uuid).size() + " &7listings.");
            meta.setLore(chat.formatList(lore));
            itemStack.setItemMeta(meta);
            inv.setItem(slot, itemStack);
            slot++;
            t++;
        }

    }

    public List<UUID> usersWithListings() {
        List<UUID> list = new ArrayList<>();

        for (Listing l : AuctionHouse.getInstance().getListingManager().getActive()) {
            if (!list.contains(l.getCreator())) list.add(l.getCreator());
        }

        return list;
    }

}
