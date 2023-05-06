package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.database.transfer.ConfirmDatabaseTransfer;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public class DatabaseTransferEvents implements Listener {

    private final HashMap<UUID, ConfirmDatabaseTransfer> portMap = new HashMap<>();
    private final HashMap<UUID, ConfirmDatabaseTransfer> usernameMap = new HashMap<>();
    private final HashMap<UUID, ConfirmDatabaseTransfer> passwordMap = new HashMap<>();
    private final HashMap<UUID, ConfirmDatabaseTransfer> databaseMap = new HashMap<>();


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();
        String input = e.getMessage();
        Chat chat = AuctionHouse.getInstance().getChat();

        //Confirm Database Transfer HOST
        if (ConfirmDatabaseTransfer.getHostMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            AuctionHouse.getInstance().getConfigFile().setDb_host(input);
            p.resetTitle();
            p.sendTitle(chat.format("&6Enter Port"), chat.format("&7Current: " + AuctionHouse.getInstance().getConfigFile().getDb_port()), 1, 600, 1);
            portMap.put(p.getUniqueId(), ConfirmDatabaseTransfer.getHostMap().get(p.getUniqueId()));
            ConfirmDatabaseTransfer.getHostMap().remove(p.getUniqueId());
        }
        //Confirm Database Transfer PORT
        else if (portMap.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            int i;
            try {
                i = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&e" + input + " is not a valid port. Please input a valid port.");
                return;
            }
            AuctionHouse.getInstance().getConfigFile().setDb_port(i);
            usernameMap.put(p.getUniqueId(), portMap.get(p.getUniqueId()));
            portMap.remove(p.getUniqueId());
            p.resetTitle();
            p.sendTitle(chat.format("&6Enter Username"), chat.format("&7Current: " + AuctionHouse.getInstance().getConfigFile().getDb_username()), 1, 600, 1);

        }
        //Confirm Database Transfer USERNAME
        else if (usernameMap.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            AuctionHouse.getInstance().getConfigFile().setDb_username(input);
            passwordMap.put(p.getUniqueId(), usernameMap.get(p.getUniqueId()));
            usernameMap.remove(p.getUniqueId());
            p.resetTitle();
            p.sendTitle(chat.format("&6Enter Password"), chat.format("&7Current: " + AuctionHouse.getInstance().getConfigFile().getDb_password()), 1, 600, 1);
            chat.sendMessage(p, "&eEnter \"none\" for no password.");
        }
        //Confirm Database Transfer PASSWORD
        else if (passwordMap.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            String password = input;
            if (password.equalsIgnoreCase("none")) {
                password = "";
            }
            AuctionHouse.getInstance().getConfigFile().setDb_password(password);
            databaseMap.put(p.getUniqueId(), passwordMap.get(p.getUniqueId()));
            passwordMap.remove(p.getUniqueId());
            p.resetTitle();
            p.sendTitle(chat.format("&6Enter Database Name"), chat.format("&7Current: " + AuctionHouse.getInstance().getConfigFile().getDb_database()), 1, 600, 1);
        }
        //Confirm Database Transfer DATABASE
        else if (databaseMap.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            AuctionHouse.getInstance().getConfigFile().setDb_database(input);
            p.resetTitle();
            Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                databaseMap.get(p.getUniqueId()).testConnection();
                databaseMap.remove(p.getUniqueId());
            });
            AuctionHouse.getInstance().getConfigFile().saveConfig();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();
        Chat chat = AuctionHouse.getInstance().getChat();

        //Confirm Database Transfer HOST
        if (ConfirmDatabaseTransfer.getHostMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.resetTitle();
            p.sendTitle(chat.format("&6Enter Port"), chat.format("&7Current: " + AuctionHouse.getInstance().getConfigFile().getDb_port()), 1, 600, 1);
            portMap.put(p.getUniqueId(), ConfirmDatabaseTransfer.getHostMap().get(p.getUniqueId()));
            ConfirmDatabaseTransfer.getHostMap().remove(p.getUniqueId());
        }
        //Confirm Database Transfer PORT
        else if (portMap.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.resetTitle();
            p.sendTitle(chat.format("&6Enter Username"), chat.format("&7Current: " + AuctionHouse.getInstance().getConfigFile().getDb_username()), 1, 600, 1);
            usernameMap.put(p.getUniqueId(), portMap.get(p.getUniqueId()));
            portMap.remove(p.getUniqueId());
        }
        //Confirm Database Transfer USERNAME
        else if (usernameMap.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.resetTitle();
            p.sendTitle(chat.format("&6Enter Password"), chat.format("&7Current: " + AuctionHouse.getInstance().getConfigFile().getDb_password()), 1, 600, 1);
            chat.sendMessage(p, "&eEnter \"none\" for no password.");
            passwordMap.put(p.getUniqueId(), usernameMap.get(p.getUniqueId()));
            usernameMap.remove(p.getUniqueId());
        }
        //Confirm Database Transfer PASSWORD
        else if (passwordMap.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.resetTitle();
            p.sendTitle(chat.format("&6Enter Database Name"), chat.format("&7Current: " + AuctionHouse.getInstance().getConfigFile().getDb_database()), 1, 600, 1);
            databaseMap.put(p.getUniqueId(), passwordMap.get(p.getUniqueId()));
            passwordMap.remove(p.getUniqueId());
        }
        //Confirm Database Transfer DATABASE
        else if (databaseMap.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.resetTitle();
            databaseMap.get(p.getUniqueId()).testConnection();
            databaseMap.remove(p.getUniqueId());
            AuctionHouse.getInstance().getConfigFile().saveConfig();
        }
    }

}
