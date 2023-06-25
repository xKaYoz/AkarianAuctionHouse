package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.sounds.SoundSelectGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SoundSelectGUIEvents implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String input = e.getMessage();
        if (!SoundSelectGUI.getSearchMap().containsKey(player.getUniqueId())) return;
        e.setCancelled(true);
        if (SoundSelectGUI.getSearchMap().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                player.openInventory(SoundSelectGUI.getSearchMap().get(player.getUniqueId()).search(input).getInventory());
                SoundSelectGUI.getSearchMap().remove(player.getUniqueId());
            });
        }

    }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        if (SoundSelectGUI.getSearchMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);

            p.openInventory(SoundSelectGUI.getSearchMap().get(p.getUniqueId()).search("").getInventory());
            SoundSelectGUI.getSearchMap().remove(p.getUniqueId());
        }

    }

}
