package net.akarian.auctionhouse.events;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.discord.DiscordWebhook;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.MessageType;
import net.akarian.auctionhouse.utils.events.ListingCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class ListingCreateEvents implements Listener {

    Chat chat = AuctionHouse.getInstance().getChat();

    @EventHandler
    public void onCreate(ListingCreateEvent event) {
        Listing listing = event.getListing();

        sendWebhook(Bukkit.getPlayer(listing.getCreator()).getDisplayName(), listing);

        for (User u : AuctionHouse.getInstance().getUserManager().getUsers()) {
            if (u.getUserSettings().isAlertCreateListings() && !u.getUuid().toString().equals(listing.getCreator().toString()))
                chat.sendMessage(Bukkit.getPlayer(u.getUuid()), AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SETTINGS_LISTINGCREATED_MESSAGE, "%seller%;" + Objects.requireNonNull(Bukkit.getPlayer(listing.getCreator())).getName(), "%listing%;" + chat.formatItem(listing.getItemStack()), "%papi%;" + u.getUuid(), "%price%;" + chat.formatMoney(listing.getPrice())));
        }
    }

    private void sendWebhook(String username, Listing listing) {
        username = ChatColor.stripColor(username);
        String itemName = chat.formatItem(listing.getItemStack());
        String price = chat.formatMoney(listing.getPrice());
        chat.log("Listing Discord Webhook: Username:" + username + " Item:" + itemName + " Price:" + price, AuctionHouse.getInstance().isDebug());
        String webhookUrl = AuctionHouse.getInstance().getConfigFile().getDiscordWebhookURL();
        if (webhookUrl.trim().isEmpty() || webhookUrl == null || webhookUrl.equals("")) {
            return;
        }
        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
        webhook.setContent("**" + username + "** has created a listing of **" + itemName + "** in the AuctionHouse for **"
                + price + "**");
        webhook.setUsername(username + "@AuctionHouse");
        try {
            webhook.execute(); // Handle exception
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

}
