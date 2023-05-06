package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.ListingEditGUI;
import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.guis.admin.ListingEditAdminGUI;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
            if (input.startsWith("seller:")) {
                String playerName = input.split(":")[1];
                UUID playerUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();
            }
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                p.openInventory(AuctionHouseGUI.getSearchMap().get(p.getUniqueId()).search(input).getInventory());
                AuctionHouseGUI.getSearchMap().remove(p.getUniqueId());
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
                chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getIncompatibleListingPrice());
                return;
            }

            if (price <= AuctionHouse.getInstance().getConfigFile().getMinListing()) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getMinimumListing()
                        .replace("%price%", AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMinListing())));
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

            if (price <= AuctionHouse.getInstance().getConfigFile().getMinListing()) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getMinimumListing()
                        .replace("%price%", AuctionHouse.getInstance().getChat().formatMoney(AuctionHouse.getInstance().getConfigFile().getMinListing())));
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
                chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getError_validNumber());
                return;
            }

            if (amount <= 0) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getError_greaterThanZero());
                return;
            }

            if (amount > listing.getItemStack().getMaxStackSize()) {
                chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getError_tooSmallStackSize());
                return;
            }

            switch (AuctionHouse.getInstance().getListingManager().setAmount(listing, amount, p, false)) {
                case -4:
                    chat.sendMessage(p, "&cThere was an error while editing the amount. The listing is not active.");
                    Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                        p.openInventory(new AuctionHouseGUI(p, SortType.TIME_LEFT, true, 1).getInventory());
                        ListingEditGUI.getAmountMap().remove(p.getUniqueId());
                    });
                    return;
                case -2:
                    chat.sendMessage(p, "&cYou cannot carry the returned items. Please clear space and try again.");
                    return;
                case -1:
                    chat.sendMessage(p, "&cYou do not have that many items to add to this auction.");
                    return;
                case 1:
                    Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                        p.openInventory(ListingEditGUI.getAmountMap().get(p.getUniqueId()).getInventory());
                        ListingEditGUI.getAmountMap().remove(p.getUniqueId());
                    });
            }
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

            switch (AuctionHouse.getInstance().getListingManager().setAmount(listing, amount, p, true)) {
                case -4:
                    chat.sendMessage(p, "&cThere was an error while editing the amount. The listing is not active.");
                    p.openInventory(new AuctionHouseGUI(p, SortType.TIME_LEFT, true, 1).getInventory());
                    ListingEditAdminGUI.getAmountMap().remove(p.getUniqueId());
                    return;
                case 1:
                    Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), () -> {
                        p.openInventory(ListingEditAdminGUI.getAmountMap().get(p.getUniqueId()).getInventory());
                        ListingEditAdminGUI.getAmountMap().remove(p.getUniqueId());
                    });
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        Chat chat = AuctionHouse.getInstance().getChat();

        if (ListingEditGUI.getAmountMap().containsKey(player.getUniqueId()) || ListingEditAdminGUI.getAmountMap().containsKey(player.getUniqueId())) {
            e.setCancelled(true);
            chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getGui_le_ac());
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
