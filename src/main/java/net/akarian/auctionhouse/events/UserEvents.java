package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.users.UserManager;
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

        um.loadUser(p.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        Player p = e.getPlayer();
        UserManager um = AuctionHouse.getInstance().getUserManager();
        User user = um.getUser(p);

        um.unloadUser(user);
    }

}
