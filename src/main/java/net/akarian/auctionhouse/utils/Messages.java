package net.akarian.auctionhouse.utils;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Messages {

    private final FileManager fm;
    @Getter
    private String createListing, expiredJoinMessage, expiredReclaimTitle, auctionHouseTitle, auctionHouseAdminTitle, listingRemoved, listingBoughtBuyer, listingBoughtCreator;
    @Getter
    private List<String> listingLore, adminListingLore, shulker, selfInfoCreator, selfInfoBuyer;
    @Getter
    private YamlConfiguration messagesFile;

    public Messages() {
        fm = AuctionHouse.getInstance().getFileManager();
        if (!fm.getFile("messages").exists()) {
            fm.createFile("messages");
        }
        reloadMessages();
    }

    public void reloadMessages() {
        messagesFile = fm.getConfig("messages");

        if (!messagesFile.contains("Create Listing")) {
            messagesFile.set("Create Listing", "&fYou have created an auction for &e%item%&f with the price of &2$%price%&f.");
        }
        createListing = messagesFile.getString("Create Listing");

        if (!messagesFile.contains("Auction House Title")) {
            messagesFile.set("Auction House Title", "&6&lAuction&f&lHouse");
        }
        auctionHouseTitle = messagesFile.getString("Auction House Title");

        if (!messagesFile.contains("Expired Join Message")) {
            messagesFile.set("Expired Join Message", "&fYou have &e%amount%x Expired Listings&f waiting for you to reclaim. Use &7&o/ah expired&f to reclaim.");
        }
        expiredJoinMessage = messagesFile.getString("Expired Join Message");

        if (!messagesFile.contains("Expired Reclaim Title")) {
            messagesFile.set("Expired Reclaim Title", "&c&lExpired Item &f&lReclaim");
        }
        expiredReclaimTitle = messagesFile.getString("Expired Reclaim Title");

        if (!messagesFile.contains("Auction House Admin Title")) {
            messagesFile.set("Auction House Admin Title", "&6&lAuction&f&lHouse &4Admin Menu");
        }
        auctionHouseAdminTitle = messagesFile.getString("Auction House Admin Title");

        if (!messagesFile.contains("Listing Removed")) {
            messagesFile.set("Listing Removed", "&fYour listing for &e%item%&f has been removed.");
        }
        listingRemoved = messagesFile.getString("Listing Removed");

        if (!messagesFile.contains("Listing Bought.Buyer")) {
            messagesFile.set("Listing Bought.Buyer", "&fYou have bought &e%item%&f for &2$%price%&f.");
        }
        listingBoughtBuyer = messagesFile.getString("Listing Bought.Buyer");

        if (!messagesFile.contains("Listing Bought.Creator")) {
            messagesFile.set("Listing Bought.Creator", "&c%buyer%&f has bought &e%item%&f for &2$%price%&f.");
        }
        listingBoughtCreator = messagesFile.getString("Listing Bought.Creator");

        if (!messagesFile.contains("Auction House Listing")) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m&l---------------------------");
            lore.add("");
            lore.add("  &fTime Left &8&m&l-&e %time%");
            lore.add("  &fCreator &8&m&l-&e %creator%");
            lore.add("  &fPrice &8&m&l-&2 $%price%");
            lore.add("");
            lore.add("%shulker%");
            lore.add("%self_info%");
            lore.add("");
            lore.add("&8&m&l---------------------------");
            messagesFile.set("Auction House Listing", lore);
        }
        listingLore = messagesFile.getStringList("Auction House Listing");

        if (!messagesFile.contains("Auction House Admin Menu Listing")) {
            List<String> lore = new ArrayList<>();
            lore.add("&8&m&l---------------------------");
            lore.add("");
            lore.add("  &fTime Left &8&m&l-&e %time%");
            lore.add("  &fCreator &8&m&l-&e %creator%");
            lore.add("  &fPrice &8&m&l-&2 $%price%");
            lore.add("");
            lore.add("%shulker%");
            lore.add("  &cLeft click to edit listing");
            lore.add("  &cShift + Right Click to Safely Remove");
            lore.add("");
            lore.add("&8&m&l---------------------------");
            messagesFile.set("Auction House Admin Menu Listing", lore);
        }
        adminListingLore = messagesFile.getStringList("Auction House Admin Menu Listing");

        if (!messagesFile.contains("Shulker")) {
            List<String> input = new ArrayList<>();
            input.add(" &fThere are &e%amount%&f items in this box.");
            input.add("");
            messagesFile.set("Shulker", input);
        }
        shulker = messagesFile.getStringList("Shulker");

        if (!messagesFile.contains("Self Info.Creator")) {
            List<String> input = new ArrayList<>();
            input.add("  &c&oLeft Click to edit");
            input.add("  &c&oShift + Right Click to remove");
            messagesFile.set("Self Info.Creator", input);
        }
        selfInfoCreator = messagesFile.getStringList("Self Info.Creator");

        if (!messagesFile.contains("Self Info.Buyer")) {
            List<String> input = new ArrayList<>();
            input.add("  &7&oClick to purchase");
            messagesFile.set("Self Info.Buyer", input);
        }
        selfInfoBuyer = messagesFile.getStringList("Self Info.Buyer");

        fm.saveFile(messagesFile, "messages");

    }

}
