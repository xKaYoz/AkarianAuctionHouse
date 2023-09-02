package net.akarian.auctionhouse.guis.admin.database.expired;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.database.MainDatabaseGUI;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.UUIDDataType;
import net.akarian.auctionhouse.utils.messages.MessageType;
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
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ExpiredListingsGUI implements AkarianInventory {

    private final Chat chat;
    private int page;
    private final Player player;
    private Inventory inv;
    private List<UUID> users;
    private HashMap<UUID, ItemStack> skullData;
    private boolean skullsLoaded;
    private boolean skullsDisplayed;
    private int tick = 0;

    public ExpiredListingsGUI(Player player, int page) {
        this.player = player;
        this.page = page;
        this.chat = AuctionHouse.getInstance().getChat();
        this.skullData = new HashMap<>();
        this.skullsLoaded = false;
        this.skullsDisplayed = false;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                p.openInventory(new MainDatabaseGUI(player).getInventory());
                return;
            case 45:
                if (page == 1) break;
                page--;
                skullsDisplayed = false;
                updateInventory();
                return;
            case 53:
                if (!(users.size() > 36 * page)) break;
                page++;
                skullsDisplayed = false;
                updateInventory();
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
                p.openInventory(new PlayerExpiredListings(player, container.get(key, new UUIDDataType()), 1, this).getInventory());
                skullsDisplayed = false;
            }
        }

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format("&c&lExpired Listings"));
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

        return inv;
    }

    public void updateInventory() {

        List<UUID> oldUsers = users;

        users = usersWithListings();

        if (oldUsers != null && oldUsers.equals(users) && skullsLoaded && skullsDisplayed) {
            return;
        }

        if (tick == 0) {
            skullData = loadSkulls();
            tick++;
        } else {
            if (tick == 30)
                tick = 0;
            tick++;
        }

        if (!skullsLoaded) {
            inv.setItem(22, ItemBuilder.build(Material.LIGHT_GRAY_WOOL, 1, "&e&lLoading...", Collections.singletonList("&7Currently loading listings...")));
            return;
        }

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
            inv.setItem(slot, skullData.get(uuid));
            slot++;
            t++;
        }

        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_PPAGE_NAME, "%previous%;" + (page - 1), "%max%;" + (users.size() % 36 == 0 ? String.valueOf(users.size() / 36) : String.valueOf((users.size() / 36) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_PPAGE_LORE));
            inv.setItem(45, previous);
        }

        //Next Page
        if (users.size() > 36 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_NPAGE_NAME, "%next%;" + (page + 1), "%max%;" + (users.size() % 36 == 0 ? String.valueOf(users.size() / 36) : String.valueOf((users.size() / 36) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_NPAGE_LORE));
            inv.setItem(53, next);
        }

        skullsDisplayed = true;

    }

    public HashMap<UUID, ItemStack> loadSkulls() {
        HashMap<UUID, ItemStack> metaMap = new HashMap<>();
        skullsLoaded = false;
        skullsDisplayed = false;

        Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            List<UUID> check = users;
            for (UUID uuid : check) {
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
                meta.setDisplayName(chat.format("&c&l" + AuctionHouse.getInstance().getNameManager().getName(uuid) + " &e&l(" + AuctionHouse.getInstance().getListingManager().getExpired(uuid).size() + ")"));
                List<String> lore = new ArrayList<>();
                lore.add("&7Click to view all &e" + AuctionHouse.getInstance().getListingManager().getExpired(uuid).size() + "&7 listings. &e" + AuctionHouse.getInstance().getListingManager().getUnclaimedExpired(uuid).size() + "&7 are unclaimed listings.");
                meta.setLore(chat.formatList(lore));
                itemStack.setItemMeta(meta);
                metaMap.put(uuid, itemStack);
            }
            skullsLoaded = true;
            updateInventory();
        });

        return metaMap;
    }

    public List<UUID> usersWithListings() {
        List<UUID> list = new ArrayList<>();

        for (Listing l : AuctionHouse.getInstance().getListingManager().getExpired()) {
            if (!list.contains(l.getCreator())) list.add(l.getCreator());
        }

        return list;
    }

}
