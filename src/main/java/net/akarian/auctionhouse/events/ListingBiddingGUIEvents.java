package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.ListingBiddingGUI;
import net.akarian.auctionhouse.guis.ListingMainGUI;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ListingBiddingGUIEvents implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String input = e.getMessage();
        Chat chat = AuctionHouse.getInstance().getChat();
        if (ListingBiddingGUI.getStartingBidMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            if (input.equalsIgnoreCase("cancel")) {
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    p.openInventory(ListingBiddingGUI.getStartingBidMap().get(p.getUniqueId()).getInventory());
                    ListingBiddingGUI.getStartingBidMap().remove(p.getUniqueId());
                });
                return;
            }
            double price;
            try {
                price = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_INCOMPATIBLEPRICE));
                return;
            }

            if (input.equalsIgnoreCase("nan")) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_INCOMPATIBLEPRICE));
                return;
            }

            if (price < AuctionHouse.getInstance().getConfigFile().getMinListing()) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_MINLISTINGPRICE, "%price%;" + AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMinListing())));
                return;
            }

            if (price > AuctionHouse.getInstance().getConfigFile().getMaxListing()) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_MAXLISTINGPRICE, "%price%;" + AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMaxListing())));
                return;
            }

            ListingBiddingGUI.getStartingBidMap().get(p.getUniqueId()).setStartingBid(price);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ListingBiddingGUI.getStartingBidMap().get(p.getUniqueId()).getInventory());
                ListingBiddingGUI.getStartingBidMap().remove(p.getUniqueId());
            });
        } else if (ListingBiddingGUI.getMinIncMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            if (input.equalsIgnoreCase("cancel")) {
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    p.openInventory(ListingBiddingGUI.getMinIncMap().get(p.getUniqueId()).getInventory());
                    ListingBiddingGUI.getMinIncMap().remove(p.getUniqueId());
                });
                return;
            }
            double price;
            try {
                price = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_INCOMPATIBLEPRICE));
                return;
            }

            if (input.equalsIgnoreCase("nan")) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_INCOMPATIBLEPRICE));
                return;
            }

            if (price < AuctionHouse.getInstance().getConfigFile().getMinListing()) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_MINLISTINGPRICE, "%price%;" + AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMinListing())));
                return;
            }

            if (price > AuctionHouse.getInstance().getConfigFile().getMaxListing()) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_MAXLISTINGPRICE, "%price%;" + AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMaxListing())));
                return;
            }

            ListingBiddingGUI.getMinIncMap().get(p.getUniqueId()).setMinimumIncrement(price);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ListingBiddingGUI.getMinIncMap().get(p.getUniqueId()).getInventory());
                ListingBiddingGUI.getMinIncMap().remove(p.getUniqueId());
            });
        } else if (ListingMainGUI.getBuyNowMap().containsKey(p.getUniqueId())) {

            e.setCancelled(true);
            if (input.equalsIgnoreCase("cancel")) {
                Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                    p.openInventory(ListingMainGUI.getBuyNowMap().get(p.getUniqueId()).getInventory());
                    ListingMainGUI.getBuyNowMap().remove(p.getUniqueId());
                });
                return;
            }
            double price;
            try {
                price = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_INCOMPATIBLEPRICE));
                return;
            }

            if (input.equalsIgnoreCase("nan")) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_INCOMPATIBLEPRICE));
                return;
            }

            if (price < AuctionHouse.getInstance().getConfigFile().getMinListing()) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_MINLISTINGPRICE, "%price%;" + AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMinListing())));
                return;
            }

            if (price > AuctionHouse.getInstance().getConfigFile().getMaxListing()) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_MAXLISTINGPRICE, "%price%;" + AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMaxListing())));
                return;
            }

            ListingMainGUI.getBuyNowMap().get(p.getUniqueId()).setBuyNowPrice(price);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ListingMainGUI.getBuyNowMap().get(p.getUniqueId()).getInventory());
                ListingMainGUI.getBuyNowMap().remove(p.getUniqueId());
            });

        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        if (ListingBiddingGUI.getMinIncMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ListingBiddingGUI.getMinIncMap().get(p.getUniqueId()).getInventory());
            ListingBiddingGUI.getMinIncMap().remove(p.getUniqueId());
        } else if (ListingBiddingGUI.getStartingBidMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ListingBiddingGUI.getStartingBidMap().get(p.getUniqueId()).getInventory());
            ListingBiddingGUI.getStartingBidMap().remove(p.getUniqueId());
        } else if (ListingMainGUI.getBuyNowMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ListingMainGUI.getBuyNowMap().get(p.getUniqueId()).getInventory());
            ListingMainGUI.getBuyNowMap().remove(p.getUniqueId());
        }

    }

}
