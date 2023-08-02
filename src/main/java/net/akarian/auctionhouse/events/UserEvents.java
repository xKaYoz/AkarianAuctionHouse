package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.users.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();
        UserManager um = AuctionHouse.getInstance().getUserManager();

        Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            User user = um.loadUser(p.getUniqueId(), p.getName());
        });


    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player p = e.getPlayer();
        UserManager um = AuctionHouse.getInstance().getUserManager();
        User user = um.getUser(p);

        Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> um.unloadUser(user));
    }

}
