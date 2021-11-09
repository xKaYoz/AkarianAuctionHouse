package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.ListingEditGUI;
import net.akarian.auctionhouse.guis.admin.AuctionHouseAdminGUI;
import net.akarian.auctionhouse.guis.admin.ListingEditAdminGUI;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class AuctionHouseGUIEvents implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String input = e.getMessage();
        Chat chat = AuctionHouse.getInstance().getChat();

        //Auction House GUI Search
        if (AuctionHouseGUI.getSearchMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            if(input.startsWith("seller:")) {
                String playerName = input.split(":")[1];
                UUID playerUUID = Bukkit.getPlayerUniqueId(playerName);
                if(playerUUID == null) {
                    chat.sendMessage(p, "&cCould not find a player with the name of " + playerName + ".");
                    return;
                }
            }
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(AuctionHouseGUI.getSearchMap().get(p.getUniqueId()).search(input).getInventory());
                AuctionHouseGUI.getSearchMap().remove(p.getUniqueId());
            });
        }
        //Auction House Admin GUI Search
        else if (AuctionHouseAdminGUI.getSearchMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(AuctionHouseAdminGUI.getSearchMap().get(p.getUniqueId()).search(input).getInventory());
                AuctionHouseAdminGUI.getSearchMap().remove(p.getUniqueId());
            });
        }
        //Listing Edit Price
        else if (ListingEditGUI.getPriceMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            Listing listing = ListingEditGUI.getPriceMap().get(p.getUniqueId()).getListing();

            double price;

            try {
                price = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&cYou must provide a compatible price.");
                return;
            }

            if (price <= 0) {
                chat.sendMessage(p, "&cThe price must be above $0.");
                return;
            }

            AuctionHouse.getInstance().getListingManager().setPrice(listing, price);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ListingEditGUI.getPriceMap().get(p.getUniqueId()).getInventory());
                ListingEditGUI.getPriceMap().remove(p.getUniqueId());
            });
        }
        //Listing Admin Edit Price
        else if (ListingEditAdminGUI.getPriceMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            Listing listing = ListingEditAdminGUI.getPriceMap().get(p.getUniqueId()).getListing();

            double price;

            try {
                price = Double.parseDouble(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&cYou must provide a compatible price.");
                return;
            }

            if (price <= 0) {
                chat.sendMessage(p, "&cThe price must be above $0.");
                return;
            }

            AuctionHouse.getInstance().getListingManager().setPrice(listing, price);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ListingEditAdminGUI.getPriceMap().get(p.getUniqueId()).getInventory());
                ListingEditAdminGUI.getPriceMap().remove(p.getUniqueId());
            });
        }
        //Listing Edit Amount
        else if (ListingEditGUI.getAmountMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            Listing listing = ListingEditGUI.getAmountMap().get(p.getUniqueId()).getListing();

            int amount;

            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&cYou must provide a compatible amount.");
                return;
            }

            if (amount <= 0) {
                chat.sendMessage(p, "&cThe amount must be greater than 0.");
                return;
            }

            if (amount > listing.getItemStack().getMaxStackSize()) {
                chat.sendMessage(p, "&cThe amount must be less than the max stack size of the item.");
                return;
            }

            switch (AuctionHouse.getInstance().getListingManager().setAmount(listing, amount, p, false)) {
                case -2:
                    chat.sendMessage(p, "&cYou cannot carry the returned items. Please clear space and try again.");
                    return;
                case -1:
                    chat.sendMessage(p, "&cYou do not have that many items to add to this auction.");
                    return;
            }
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ListingEditGUI.getAmountMap().get(p.getUniqueId()).getInventory());
                ListingEditGUI.getAmountMap().remove(p.getUniqueId());
            });
        }
        //Listing Admin Edit Amount
        else if (ListingEditAdminGUI.getAmountMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            Listing listing = ListingEditAdminGUI.getAmountMap().get(p.getUniqueId()).getListing();

            int amount;

            try {
                amount = Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                chat.sendMessage(p, "&cYou must provide a compatible amount.");
                return;
            }

            if (amount <= 0) {
                chat.sendMessage(p, "&cThe amount must be greater than 0.");
                return;
            }

            if (amount > listing.getItemStack().getMaxStackSize()) {
                chat.sendMessage(p, "&cThe amount must be less than the max stack size of the item.");
                return;
            }

            AuctionHouse.getInstance().getListingManager().setAmount(listing, amount, p, true);
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(ListingEditAdminGUI.getAmountMap().get(p.getUniqueId()).getInventory());
                ListingEditAdminGUI.getAmountMap().remove(p.getUniqueId());
            });
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        //Auction House GUI Search
        if (AuctionHouseGUI.getSearchMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);

            p.openInventory(AuctionHouseGUI.getSearchMap().get(p.getUniqueId()).search("").getInventory());
            AuctionHouseGUI.getSearchMap().remove(p.getUniqueId());
        }
        //Auction House Admin GUI Search
        if (AuctionHouseAdminGUI.getSearchMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);

            p.openInventory(AuctionHouseAdminGUI.getSearchMap().get(p.getUniqueId()).search("").getInventory());
            AuctionHouseAdminGUI.getSearchMap().remove(p.getUniqueId());
        }
        //Listing Edit Price
        else if (ListingEditGUI.getPriceMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ListingEditGUI.getPriceMap().get(p.getUniqueId()).getInventory());
            ListingEditGUI.getPriceMap().remove(p.getUniqueId());
        }
        //Listing Admin Edit Price
        else if (ListingEditAdminGUI.getPriceMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ListingEditAdminGUI.getPriceMap().get(p.getUniqueId()).getInventory());
            ListingEditAdminGUI.getPriceMap().remove(p.getUniqueId());
        }
        //Listing Edit Amount
        else if (ListingEditGUI.getAmountMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ListingEditGUI.getAmountMap().get(p.getUniqueId()).getInventory());
            ListingEditGUI.getAmountMap().remove(p.getUniqueId());
        }
        //Listing Admin Edit Amount
        else if (ListingEditAdminGUI.getAmountMap().containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            p.openInventory(ListingEditAdminGUI.getAmountMap().get(p.getUniqueId()).getInventory());
            ListingEditAdminGUI.getAmountMap().remove(p.getUniqueId());
        }

    }

}
