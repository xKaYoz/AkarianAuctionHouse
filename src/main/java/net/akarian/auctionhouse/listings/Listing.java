package net.akarian.auctionhouse.listings;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.UUIDDataType;
import net.akarian.auctionhouse.utils.Chat;
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
    private ItemStack display;
    @Getter
    @Setter
    private double price;
    @Getter
    @Setter
    private long end;
    @Getter
    @Setter
    private UUID buyer;

    public Listing(UUID id, UUID creator, ItemStack itemStack, Double price, Long start) {
        plugin = AuctionHouse.getInstance();
        chat = plugin.getChat();
        this.id = id;
        this.creator = creator;
        this.itemStack = itemStack;
        this.price = price;
        this.start = start;
    }

    public ItemStack createAdminListing() {
        ItemStack itemStack = getItemStack().clone();
        List<String> lore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            lore = new ArrayList<>();
        }

        assert lore != null;

        long seconds = ((getStart() + (86400 * 1000)) - System.currentTimeMillis()) / 1000;

        lore.add("&8&m&l---------------------------");
        lore.add("");
        lore.add("  &fTime Left &8&m&l-&e " + chat.formatTime(seconds));
        lore.add("  &fCreator &8&m&l-&e " + plugin.getNameManager().getName(getCreator()));
        lore.add("  &fPrice &8&m&l-&2 $" + chat.formatMoney(getPrice()));
        lore.add("");
        if (itemStack.getType() == Material.SHULKER_BOX) {
            if (itemStack.getItemMeta() instanceof BlockStateMeta) {
                BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                if (im.getBlockState() instanceof ShulkerBox) {
                    ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                    int amount = 0;
                    for (ItemStack si : shulker.getInventory().getContents()) {
                        if (si != null) {
                            amount += si.getAmount();
                        }
                    }
                    lore.add(" &fThere are &e" + amount + "&f items in this box.");
                    lore.add("");
                    lore.add(" &cShift + Left Click to view contents");
                    lore.add("");
                }
            }
        }
        lore.add("  &cLeft click to edit listing");
        lore.add("  &cShift + Right Click to Safely Remove");
        lore.add("");
        lore.add("&8&m&l---------------------------");

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), getId());

        itemMeta.setLore(chat.formatList(lore));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void setupDisplay(Player p) {
        setDisplay(itemStack.clone());

        List<String> lore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            lore = new ArrayList<>();
        }

        assert lore != null;

        long seconds = ((start + (86400 * 1000)) - System.currentTimeMillis()) / 1000;

        lore.add("&8&m&l---------------------------");
        lore.add("");
        lore.add("  &fTime Left &8&m&l-&e " + chat.formatTime(seconds));
        lore.add("  &fCreator &8&m&l-&e " + plugin.getNameManager().getName(creator));
        lore.add("  &fPrice &8&m&l-&2 $" + chat.formatMoney(price));
        lore.add("");
        if (itemStack.getType() == Material.SHULKER_BOX) {
            if (itemStack.getItemMeta() instanceof BlockStateMeta) {
                BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                if (im.getBlockState() instanceof ShulkerBox) {
                    ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                    int amount = 0;
                    for (ItemStack si : shulker.getInventory().getContents()) {
                        if (si != null) {
                            amount += si.getAmount();
                        }
                    }
                    lore.add(" &fThere are &e" + amount + "&f items in this box.");
                    lore.add("");
                    if (getCreator().toString().equals(p.getUniqueId().toString())) {
                        lore.add("  &c&oLeft Click to edit");
                        lore.add("  &c&oShift + Right Click to remove");
                    } else
                        lore.add("  &7&oClick to view contents and purchase");
                }
            }
        } else {
            if (getCreator().toString().equals(p.getUniqueId().toString())) {
                lore.add("  &c&oLeft Click to edit");
                lore.add("  &c&oShift + Right Click to remove");
            } else
                lore.add("  &7&oClick to purchase");
        }
        lore.add("");
        lore.add("&8&m&l---------------------------");

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), id);

        itemMeta.setLore(chat.formatList(lore));
        itemMeta.setDisplayName(chat.formatItem(display));

        display.setItemMeta(itemMeta);

    }

}
