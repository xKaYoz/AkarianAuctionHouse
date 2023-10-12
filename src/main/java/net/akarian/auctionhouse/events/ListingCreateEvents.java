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

import java.awt.*;
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

    private void sendWebhook(String playerName, Listing listing) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            String webhookUrl = AuctionHouse.getInstance().getConfigFile().getDiscordWebhookURL();
            if (webhookUrl.trim().isEmpty() || webhookUrl == null || webhookUrl.equals("")) {
                return;
            }
            String username = ChatColor.stripColor(playerName);
            String itemName = ChatColor.stripColor(chat.formatItem(listing.getItemStack()));
            String price = chat.formatMoney(listing.getPrice());
            chat.log("Listing Discord Webhook: Username:" + username + " Item:" + itemName + " Price:" + price, AuctionHouse.getInstance().isDebug());
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
            webhook.setUsername("Auction House Notifications");
            DiscordWebhook.EmbedObject object = new DiscordWebhook.EmbedObject();
            object.setColor(Color.CYAN);
            object.setDescription("**" + username + "** has created a listing of **" + itemName + "** in the AuctionHouse for **"
                    + price + "**");
            object.setTitle(username + " has created an Auction!");
            webhook.addEmbed(object);
            try {
                webhook.execute(); // Handle exception
            } catch (Exception e) {
                getLogger().log(Level.WARNING, e.toString());
            }
        });
    }

}
