package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateJoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (AuctionHouse.getInstance().isUpdate() && (e.getPlayer().hasPermission("auctionhouse.admin.update") || e.getPlayer().isOp())) {
            switch (AuctionHouse.getInstance().getUpdateManager().isUpdate()) {
                case 2:
                    AuctionHouse.getInstance().getChat().sendMessage(e.getPlayer(), "&eAn update has been found.");
            }
        }
    }

}
