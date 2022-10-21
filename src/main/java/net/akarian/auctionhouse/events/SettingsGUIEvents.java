package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.SettingsGUI;
import net.akarian.auctionhouse.guis.admin.settings.DefaultPlayerSettingsGUI;
import net.akarian.auctionhouse.guis.admin.settings.ServerSettingsGUI;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SettingsGUIEvents implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String input = e.getMessage();
        Chat chat = AuctionHouse.getInstance().getChat();
        User user = AuctionHouse.getInstance().getUserManager().getUser(p);

        if(SettingsGUI.getTimeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            try {
                Long.parseLong(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&cYou must provide a valid number.");
                return;
            }
            long sec = Long.parseLong(input);
            user.getUserSettings().setAlertNearExpireTime(sec);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(SettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
                SettingsGUI.getTimeMap().remove(p.getUniqueId());
            });
        } else if(DefaultPlayerSettingsGUI.getTimeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            try {
                Long.parseLong(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&cYou must provide a valid number.");
                return;
            }
            long sec = Long.parseLong(input);
            AuctionHouse.getInstance().getConfigFile().setDps_expireTime(sec);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(DefaultPlayerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
                DefaultPlayerSettingsGUI.getTimeMap().remove(p.getUniqueId());
            });
        } else if(ServerSettingsGUI.getTimeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&cYou must provide a valid number.");
                return;
            }
            int sec = Integer.parseInt(input);
            AuctionHouse.getInstance().getConfigFile().setListingTime(sec);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ServerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
                ServerSettingsGUI.getTimeMap().remove(p.getUniqueId());
            });
        } else if(ServerSettingsGUI.getFeeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            String dupe = input;
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                if(input.contains("%")) {
                    try {
                        Integer.parseInt(dupe.replace("%", ""));
                    } catch (NumberFormatException ex2) {
                        chat.sendMessage(p, "&cYou must provide a valid percentage.");
                        return;
                    }
                } else {
                    chat.sendMessage(p, "&cYou must provide a valid percentage.");
                    return;
                }
            }
            AuctionHouse.getInstance().getConfigFile().setListingFee(input);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ServerSettingsGUI.getFeeMap().get(p.getUniqueId()).getInventory());
                ServerSettingsGUI.getFeeMap().remove(p.getUniqueId());
            });
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        Chat chat = AuctionHouse.getInstance().getChat();

        if(SettingsGUI.getTimeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(SettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
            SettingsGUI.getTimeMap().remove(p.getUniqueId());
        } else if(DefaultPlayerSettingsGUI.getTimeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(DefaultPlayerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
            DefaultPlayerSettingsGUI.getTimeMap().remove(p.getUniqueId());
        } else if(ServerSettingsGUI.getFeeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ServerSettingsGUI.getFeeMap().get(p.getUniqueId()).getInventory());
            ServerSettingsGUI.getFeeMap().remove(p.getUniqueId());
        } else if(ServerSettingsGUI.getTimeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ServerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
            ServerSettingsGUI.getTimeMap().remove(p.getUniqueId());
        }
    }
}
