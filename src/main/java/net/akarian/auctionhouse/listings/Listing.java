package net.akarian.auctionhouse.listings;

import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.UUIDDataType;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Listing {

    @Getter
    private final UUID id;
    @Getter
    private final UUID creator;
    @Getter
    private final long start;
    private final AuctionHouse plugin;
    private final Chat chat;
    @Getter
    @Setter
    private ItemStack itemStack;
    @Getter
    @Setter
    private double price;
    @Getter
    @Setter
    private long end;
    @Getter
    @Setter
    private UUID buyer;
    @Getter
    @Setter
    private String endReason;
    @Getter
    @Setter
    private boolean reclaimed;

    public Listing(UUID id, UUID creator, ItemStack itemStack, Double price, Long start) {
        plugin = AuctionHouse.getInstance();
        chat = plugin.getChat();
        this.id = id;
        this.creator = creator;
        this.itemStack = itemStack;
        this.price = price;
        this.start = start;
        this.reclaimed = false;
    }

    public ItemStack createAdminActiveListing(Player player) {
        ItemStack itemStack = getItemStack().clone();
        List<String> tlore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            tlore = new ArrayList<>();
        }

        assert tlore != null;

        long seconds = ((getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) - System.currentTimeMillis()) / 1000;

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), getId());

        for (String s : AuctionHouse.getInstance().getMessages().getGui_aha_listing()) {
            if (s.equalsIgnoreCase("%shulker%")) {
                if (itemStack.getType() == Material.SHULKER_BOX) {
                    BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                        int amount = 0;
                        for (ItemStack si : shulker.getInventory().getContents()) {
                            if (si != null) {
                                amount += si.getAmount();
                            }
                        }
                        for (String shulkers : AuctionHouse.getInstance().getMessages().getGui_sv_sh()) {
                            tlore.add(shulkers.replace("%amount%", amount + ""));
                        }
                    }
                }
            } else {
                tlore.add(s.replace("%time%", chat.formatTime(seconds)).replace("%creator%", plugin.getNameManager().getName(creator))
                        .replace("%price%", chat.formatMoney(price)));
            }
        }
        itemMeta.setLore(chat.formatList(tlore
        ));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack createAdminCompleteListing(Player player) {
        ItemStack itemStack = getItemStack().clone();
        List<String> tlore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            tlore = new ArrayList<>();
        }

        assert tlore != null;

        long seconds = ((getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) - System.currentTimeMillis()) / 1000;

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), getId());

        for (String s : AuctionHouse.getInstance().getMessages().getCompletedAdminLore()) {
            if (s.equalsIgnoreCase("%shulker%")) {
                if (itemStack.getType() == Material.SHULKER_BOX) {
                    BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                        int amount = 0;
                        for (ItemStack si : shulker.getInventory().getContents()) {
                            if (si != null) {
                                amount += si.getAmount();
                            }
                        }
                        for (String shulkers : AuctionHouse.getInstance().getMessages().getGui_sv_sh()) {
                            tlore.add(shulkers.replace("%amount%", amount + ""));
                        }
                    }
                }
            } else {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    tlore.add(PlaceholderAPI.setPlaceholders(player, s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd()))
                            .replace("%price%", chat.formatMoney(price))));
                else
                    tlore.add(s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd()))
                            .replace("%price%", chat.formatMoney(price)).replace("%buyer%", AuctionHouse.getInstance().getNameManager().getName(buyer)));
            }
        }
        itemMeta.setLore(chat.formatList(tlore
        ));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack createExpiredListing(Player player) {
        ItemStack itemStack = getItemStack().clone();

        List<String> lore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            lore = new ArrayList<>();
        }

        assert lore != null;

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        assert itemMeta != null;
        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), id);

        for (String s : AuctionHouse.getInstance().getMessages().getExpiredLore()) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                lore.add(PlaceholderAPI.setPlaceholders(player, s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd()))
                        .replace("%price%", chat.formatMoney(price))));
            else
                lore.add(s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd()))
                        .replace("%price%", chat.formatMoney(price)));
        }

        itemMeta.setLore(chat.formatList(lore));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack createAdminExpiredListing(Player player) {
        ItemStack itemStack = getItemStack().clone();

        List<String> lore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            lore = new ArrayList<>();
        }

        assert lore != null;

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        assert itemMeta != null;
        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), id);

        for (String s : AuctionHouse.getInstance().getMessages().getExpiredAdminLore()) {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                lore.add(PlaceholderAPI.setPlaceholders(player, s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd()))
                        .replace("%price%", chat.formatMoney(price)).replace("%reclaimed%", isReclaimed() ? "&aTrue" : "&cFalse")));
            else
                lore.add(s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd()))
                        .replace("%price%", chat.formatMoney(price)).replace("%reclaimed%", isReclaimed() ? "&aTrue" : "&cFalse"));
        }

        itemMeta.setLore(chat.formatList(lore));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack createActiveListing(Player player) {
        ItemStack itemStack = getItemStack().clone();

        List<String> tlore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            tlore = new ArrayList<>();
        }

        assert tlore != null;

        long seconds = ((start + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) - System.currentTimeMillis()) / 1000;

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), id);

        for (String s : AuctionHouse.getInstance().getMessages().getListingLore()) {
            if (s.equalsIgnoreCase("%shulker%")) {
                if (itemStack.getType() == Material.SHULKER_BOX) {
                    BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                        int amount = 0;
                        for (ItemStack si : shulker.getInventory().getContents()) {
                            if (si != null) {
                                amount += si.getAmount();
                            }
                        }
                        for (String shulkers : AuctionHouse.getInstance().getMessages().getGui_sv_sh()) {
                            tlore.add(shulkers.replace("%amount%", amount + ""));
                        }
                    }
                }
            } else if (s.equalsIgnoreCase("%self_info%")) {
                if (getCreator().toString().equals(player.getUniqueId().toString())) {
                    tlore.addAll(AuctionHouse.getInstance().getMessages().getSelfInfoCreator());
                } else
                    tlore.addAll(AuctionHouse.getInstance().getMessages().getSelfInfoBuyer());
            } else {
                tlore.add(s.replace("%time%", chat.formatTime(seconds)).replace("%creator%", plugin.getNameManager().getName(creator))
                        .replace("%price%", chat.formatMoney(price)));
            }
        }

        itemMeta.setLore(chat.formatList(tlore
        ));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
