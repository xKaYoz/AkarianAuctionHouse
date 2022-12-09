package net.akarian.auctionhouse.utils;

import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

/**
 * Created by KaYoz on 8/7/2017.
 * Subscribe to me on Youtube:
 * http://www.youtube.com/c/KaYozMC/
 */

public class ItemBuilder {

    public static ItemStack build(Material material, int amount, String name, List<String> lore, String... settings) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(AuctionHouse.getInstance().getChat().formatList(lore));
        meta.setDisplayName(AuctionHouse.getInstance().getChat().format(name));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        for(String s : settings) {
            if(s.equalsIgnoreCase("shine")) {
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            } else if(s.contains("uuid_")) {
                NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "builder-uuid");
                meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(key, new UUIDDataType(), UUID.fromString(s.split("_")[1]));
                item.setItemMeta(meta);
            }
        }
        return item;
    }

}
