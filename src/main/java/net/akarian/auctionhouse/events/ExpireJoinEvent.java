package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.InventoryHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ExpireJoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        List<ItemStack> expired = AuctionHouse.getInstance().getListingManager().getExpired(p.getUniqueId(), true);
        Chat chat = AuctionHouse.getInstance().getChat();
        StringBuilder auctions = new StringBuilder();

        for(ItemStack itemStack : expired) {
            auctions.append(chat.formatItem(itemStack)).append("&e ");
            InventoryHandler.addItem(p, itemStack);
        }

        if(expired.size() > 0) {
            chat.sendMessage(p, "&fWhile you were offline, your auctions for &e" + auctions + "&fexpired and have been returned to you.");
        }
    }
}
