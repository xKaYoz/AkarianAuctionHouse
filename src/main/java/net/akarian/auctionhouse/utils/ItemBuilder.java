package net.akarian.auctionhouse.utils;

import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Created by KaYoz on 8/7/2017.
 * Subscribe to me on Youtube:
 * http://www.youtube.com/c/KaYozMC/
 */

public class ItemBuilder {

    public static ItemStack build(Material material, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(AuctionHouse.getInstance().getChat().formatList(lore));
        meta.setDisplayName(AuctionHouse.getInstance().getChat().format(name));
        item.setItemMeta(meta);
        return item;
    }

}
