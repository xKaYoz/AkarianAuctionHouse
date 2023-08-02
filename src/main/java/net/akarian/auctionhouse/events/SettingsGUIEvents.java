package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.SettingsGUI;
import net.akarian.auctionhouse.guis.admin.settings.DefaultPlayerSettingsGUI;
import net.akarian.auctionhouse.guis.admin.settings.ServerSettingsGUI;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public class SettingsGUIEvents implements Listener {

    HashMap<UUID, Integer> willExpireCheck = new HashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String input = e.getMessage();
        Chat chat = AuctionHouse.getInstance().getChat();
        User user = AuctionHouse.getInstance().getUserManager().getUser(p);

        if(SettingsGUI.getTimeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            if (input.equalsIgnoreCase("cancel")) {
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    p.openInventory(ServerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
                    ServerSettingsGUI.getTimeMap().remove(p.getUniqueId());
                });
                return;
            }
            try {
                Long.parseLong(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_NUMBER));
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
            if (input.equalsIgnoreCase("cancel")) {
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    p.openInventory(DefaultPlayerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
                    DefaultPlayerSettingsGUI.getTimeMap().remove(p.getUniqueId());
                });
                return;
            }
            try {
                Long.parseLong(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_NUMBER));
                return;
            }
            long sec = Long.parseLong(input);
            AuctionHouse.getInstance().getConfigFile().setDps_expireTime(sec);
            AuctionHouse.getInstance().getConfigFile().saveConfig();
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(DefaultPlayerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
                DefaultPlayerSettingsGUI.getTimeMap().remove(p.getUniqueId());
            });
        } else if(willExpireCheck.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            if(input.equalsIgnoreCase("Y")) {
                AuctionHouse.getInstance().getConfigFile().setListingTime(willExpireCheck.get(p.getUniqueId()));
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    p.openInventory(ServerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
                    ServerSettingsGUI.getTimeMap().remove(p.getUniqueId());
                });
            } else {
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    p.openInventory(ServerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
                    ServerSettingsGUI.getTimeMap().remove(p.getUniqueId());
                });
            }
            willExpireCheck.remove(p.getUniqueId());
        }else if(ServerSettingsGUI.getTimeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            if (input.equalsIgnoreCase("cancel")) {
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    p.openInventory(ServerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
                    ServerSettingsGUI.getTimeMap().remove(p.getUniqueId());
                });
                return;
            }
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_NUMBER));
                return;
            }
            int sec = Integer.parseInt(input);
            long now = System.currentTimeMillis() / 1000;
            int willExpire = 0;
            for (Listing listing : AuctionHouse.getInstance().getListingManager().getActive()) {
                long end = (listing.getStart() + (sec * 1000L)) / 1000;
                if(now > end)
                    willExpire++;
            }
            if(willExpire != 0) {
                chat.sendMessage(p, "&e&l! &c&lCAUTION &fChanging the the listing time to &e" + chat.formatTime(sec) + "&f will cause &e" + willExpire + "&f auction(s) to expire.");
                chat.sendMessage(p, "&fType Y to continue or N to cancel.");
                willExpireCheck.put(p.getUniqueId(), sec);
                return;
            }
            AuctionHouse.getInstance().getConfigFile().setListingTime(sec);
            AuctionHouse.getInstance().getConfigFile().saveConfig();
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ServerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
                ServerSettingsGUI.getTimeMap().remove(p.getUniqueId());
            });
        } else if(ServerSettingsGUI.getFeeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            if (input.equalsIgnoreCase("cancel")) {
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    p.openInventory(ServerSettingsGUI.getFeeMap().get(p.getUniqueId()).getInventory());
                    ServerSettingsGUI.getFeeMap().remove(p.getUniqueId());
                });
                return;
            }
            String dupe = input;
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                if (input.contains("%")) {
                    try {
                        Integer.parseInt(dupe.replace("%", ""));
                    } catch (NumberFormatException ex2) {
                        chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PERCENTAGE));
                        return;
                    }
                } else {
                    chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PERCENTAGE));
                    return;
                }
            }
            AuctionHouse.getInstance().getConfigFile().setListingFee(input);
            AuctionHouse.getInstance().getConfigFile().saveConfig();
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ServerSettingsGUI.getFeeMap().get(p.getUniqueId()).getInventory());
                ServerSettingsGUI.getFeeMap().remove(p.getUniqueId());
            });
        } else if (ServerSettingsGUI.getTaxMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            if (input.equalsIgnoreCase("cancel")) {
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    p.openInventory(ServerSettingsGUI.getTaxMap().get(p.getUniqueId()).getInventory());
                    ServerSettingsGUI.getTaxMap().remove(p.getUniqueId());
                });
                return;
            }
            String dupe = input;
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                if (input.contains("%")) {
                    try {
                        Integer.parseInt(dupe.replace("%", ""));
                    } catch (NumberFormatException ex2) {
                        chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PERCENTAGE));
                        return;
                    }
                } else {
                    chat.sendMessage(p, "&cYou must provide a valid tax.");
                    return;
                }
            }
            AuctionHouse.getInstance().getConfigFile().setListingTax(input);
            AuctionHouse.getInstance().getConfigFile().saveConfig();
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ServerSettingsGUI.getTaxMap().get(p.getUniqueId()).getInventory());
                ServerSettingsGUI.getTaxMap().remove(p.getUniqueId());
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
        } else if (ServerSettingsGUI.getFeeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ServerSettingsGUI.getFeeMap().get(p.getUniqueId()).getInventory());
            ServerSettingsGUI.getFeeMap().remove(p.getUniqueId());
        } else if (ServerSettingsGUI.getTimeMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ServerSettingsGUI.getTimeMap().get(p.getUniqueId()).getInventory());
            ServerSettingsGUI.getTimeMap().remove(p.getUniqueId());
        } else if (ServerSettingsGUI.getTaxMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ServerSettingsGUI.getTaxMap().get(p.getUniqueId()).getInventory());
            ServerSettingsGUI.getTaxMap().remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if (e.getInventory().getHolder() instanceof SettingsGUI) {
            SettingsGUI gui = (SettingsGUI) e.getInventory().getHolder();
            if (gui.isEdited()) {
                AuctionHouse.getInstance().getUserManager().getUser(p).getUserSettings().save();
            }
        }
    }
}
