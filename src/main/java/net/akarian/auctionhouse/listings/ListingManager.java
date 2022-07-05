package net.akarian.auctionhouse.listings;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.UUIDDataType;
import net.akarian.auctionhouse.guis.*;
import net.akarian.auctionhouse.guis.admin.AuctionHouseAdminGUI;
import net.akarian.auctionhouse.guis.admin.ListingEditAdminGUI;
import net.akarian.auctionhouse.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ListingManager {

    private final MySQL mySQL;
    private final Chat chat;
    @Getter
    private final List<Listing> listings;
    private final DatabaseType databaseType;
    private final FileManager fm;
    private int expireTimer;
    private int refreshTimer;

    public ListingManager() {
        this.mySQL = AuctionHouse.getInstance().getMySQL();
        this.chat = AuctionHouse.getInstance().getChat();
        this.databaseType = AuctionHouse.getInstance().getDatabaseType();
        this.fm = AuctionHouse.getInstance().getFileManager();
        this.listings = new ArrayList<>();
        checkDatabaseTransfer();
        loadListings();
        startExpireCheck();
        startAuctionHouseRefresh();
        startCacheCheck();
    }

    private void checkDatabaseTransfer() {
        switch (databaseType) {
            case FILE2MYSQL: {
                if (AuctionHouse.getInstance().getMySQL().getConnection() == null) break;
                chat.alert("&eStarting transfer from file to MySQL. You may experience some lag.");
                int lt = 0;
                int et = 0;
                int ct = 0;
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                Set<String> listingsKeySet = listingsFile.getValues(false).keySet();
                YamlConfiguration expiredFile = fm.getConfig("/database/expired");
                Set<String> expireKeySet = expiredFile.getValues(false).keySet();
                YamlConfiguration completedFile = fm.getConfig("/database/completed");
                Set<String> completedKeySet = completedFile.getValues(false).keySet();
                for (String s : listingsKeySet) {
                    String id = s;
                    String itemstack = listingsFile.getString(s + ".ItemStack");
                    double price = listingsFile.getDouble(s + ".Price");
                    String creator = listingsFile.getString(s + ".Creator");
                    long start = listingsFile.getLong(s + ".Start");
                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getListingsTable() + " (ID,ITEM_STACK,PRICE,CREATOR,START,END,BUYER) VALUES (?,?,?,?,?,?,?)");

                        statement.setString(1, id);
                        statement.setString(2, itemstack);
                        statement.setDouble(3, price);
                        statement.setString(4, creator);
                        statement.setLong(5, start);
                        statement.setLong(6, 0);
                        statement.setString(7, null);

                        statement.executeUpdate();
                        statement.close();

                        lt++;

                        listingsFile.set(id, null);

                        chat.log("Transferred listing " + id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                chat.alert("Transferred " + lt + " listings to MySQL.");
                for (String s : expireKeySet) {
                    String id = s;
                    String creator = expiredFile.getString(s + ".Creator");
                    String itemstack = expiredFile.getString(s + ".ItemStack");

                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getExpiredTable() + " (ID,ITEM_STACK,CREATOR) VALUES (?,?,?)");

                        statement.setString(1, id);
                        statement.setString(2, itemstack);
                        statement.setString(3, creator);

                        statement.executeUpdate();
                        statement.close();
                        et++;

                        expiredFile.set(s, null);

                        chat.log("Transferred expired listing " + id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                chat.alert("Transferred " + et + " expired listings to MySQL.");
                for (String s : completedKeySet) {

                    String id = s;
                    String itemstack = completedFile.getString(s + ".ItemStack");
                    double price = completedFile.getDouble(s + ".Price");
                    String creator = completedFile.getString(s + ".Creator");
                    long start = completedFile.getLong(s + ".Start");
                    long end = completedFile.getLong(s + ".End");
                    String buyer = completedFile.getString(s + ".Buyer");

                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getCompletedTable() + " (ID,ITEM_STACK,PRICE,CREATOR,START,END,BUYER) VALUES (?,?,?,?,?,?,?)");

                        statement.setString(1, id);
                        statement.setString(2, itemstack);
                        statement.setDouble(3, price);
                        statement.setString(4, creator);
                        statement.setLong(5, start);
                        statement.setLong(6, end);
                        statement.setString(7, buyer);

                        statement.executeUpdate();
                        statement.closeOnCompletion();

                        completedFile.set(s, null);

                        chat.log("Transferred complete listing " + id);
                        ct++;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                chat.alert("Transferred " + ct + " completed listings to MySQL.");

                fm.saveFile(listingsFile, "/database/listings");
                fm.saveFile(expiredFile, "/database/expired");
                fm.saveFile(completedFile, "/database/completed");
                AuctionHouse.getInstance().getConfigFile().setDatabaseType(DatabaseType.MYSQL);
                chat.alert("Transfer complete. Transferred " + (et + lt + ct) + " total listings.");
                break;
            }
            case MYSQL2FILE: {
                chat.alert("&eStarting transfer from MySQL to File. You may experience some lag.");
                int lt = 0;
                int et = 0;
                int ct = 0;
                chat.alert("Creating Files");
                if (!fm.getFile("/database/listings").exists()) {
                    fm.createFile("/database/listings");
                }
                if (!fm.getFile("/database/expired").exists()) {
                    fm.createFile("/database/expired");
                }
                if (!fm.getFile("/database/completed").exists()) {
                    fm.createFile("/database/completed");
                }
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                YamlConfiguration expiredFile = fm.getConfig("/database/expired");
                YamlConfiguration completedFile = fm.getConfig("/database/completed");
                chat.alert("Files created... Starting Transferring Process.");
                try {
                    PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getListingsTable());

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {

                        String id = rs.getString(1);

                        listingsFile.set(id + ".ItemStack", rs.getString(2));
                        listingsFile.set(id + ".Price", rs.getDouble(3));
                        listingsFile.set(id + ".Creator", rs.getString(4));
                        listingsFile.set(id + ".Start", rs.getLong(5));

                        lt++;

                        PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getListingsTable() + " WHERE ID=?");
                        delete.setString(1, id);
                        delete.executeUpdate();
                        delete.closeOnCompletion();

                    }
                    fm.saveFile(listingsFile, "/database/listings");
                    chat.alert("Transferred " + lt + " active listings from MySQL.");
                    statement.closeOnCompletion();

                } catch (Exception e) {
                    e.printStackTrace();
                    chat.alert("Error while transferring active listings.");
                }

                try {
                    PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getExpiredTable());

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {

                        String id = rs.getString(1);

                        expiredFile.set(id + ".ItemStack", rs.getString(2));
                        expiredFile.set(id + ".Creator", rs.getString(3));

                        et++;

                        PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getExpiredTable() + " WHERE ID=?");
                        delete.setString(1, id);
                        delete.executeUpdate();
                        delete.closeOnCompletion();

                    }
                    fm.saveFile(expiredFile, "/database/expired");
                    chat.alert("Transferred " + et + " expired listings from MySQL.");
                    statement.closeOnCompletion();

                } catch (Exception e) {
                    e.printStackTrace();
                    chat.alert("Error while transferring expired listings.");
                }

                try {
                    PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getCompletedTable());

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {

                        String id = rs.getString(1);

                        completedFile.set(id + ".ItemStack", rs.getString(2));
                        completedFile.set(id + ".Price", rs.getDouble(3));
                        completedFile.set(id + ".Creator", rs.getString(4));
                        completedFile.set(id + ".Start", rs.getLong(5));
                        completedFile.set(id + ".End", rs.getLong(6));
                        completedFile.set(id + ".Buyer", rs.getString(7));

                        ct++;

                        PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getCompletedTable() + " WHERE ID=?");
                        delete.setString(1, id);
                        delete.executeUpdate();
                        delete.closeOnCompletion();

                    }
                    fm.saveFile(completedFile, "/database/completed");
                    chat.alert("Transferred " + ct + " completed listings from MySQL.");
                    statement.closeOnCompletion();

                } catch (Exception e) {
                    e.printStackTrace();
                    chat.alert("Error while transferring completed listings.");
                }
                AuctionHouse.getInstance().getConfigFile().setDatabaseType(DatabaseType.FILE);
                chat.alert("Transfer complete. Transferred " + (et + lt + ct) + " total listings.");
                break;
            }
        }
    }

    /**
     * Remove a listing and return the ItemStack to the Listing Creator
     *
     * @param listing Listing to save remove
     * @return -1: Error while removing 0: Not Active 1: Successfully Removed 2: Successfully Removed and Item given
     */
    public int safeRemove(Listing listing) {

        if (!listings.contains(listing)) return 0;

        remove(listing);

        Player creator = Bukkit.getPlayer(listing.getCreator());

        if (creator == null) {
            switch (databaseType) {
                case MYSQL:
                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getExpiredTable() + " (ID,ITEM_STACK,CREATOR) VALUES (?,?,?)");

                        statement.setString(1, listing.getId().toString());
                        statement.setString(2, AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                        statement.setString(3, listing.getCreator().toString());

                        statement.executeUpdate();
                        statement.close();
                        return 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return -1;
                    }
                case FILE:
                    YamlConfiguration expiredFile = fm.getConfig("/database/expired");

                    expiredFile.set(listing.getId().toString() + ".ItemStack", AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                    expiredFile.set(listing.getId().toString() + ".Creator", listing.getCreator().toString());

                    fm.saveFile(expiredFile, "/database/expired");
                    return 1;
            }
        }

        InventoryHandler.addItem(creator, listing.getItemStack());
        assert creator != null;
        chat.sendMessage(creator, AuctionHouse.getInstance().getMessages().getListingRemoved().replace("%item%", chat.formatItem(listing.getItemStack())));
        chat.log("Safe removed " + chat.formatItem(listing.getItemStack()) + "(" + listing.getId().toString() + ")");
        return 2;
    }

    /**
     * Remove a listing and NOT return the listing item
     *
     * @param listing Listing to remove
     * @return Whether the listing was successfully removed
     */
    public boolean remove(Listing listing) {

        switch (databaseType) {
            case MYSQL:
                try {

                    PreparedStatement statement = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getListingsTable() + " WHERE ID=?");

                    statement.setString(1, listing.getId().toString());

                    statement.executeUpdate();
                    statement.closeOnCompletion();

                    chat.log("Removed listing " + chat.formatItem(listing.getItemStack()) + " " + listing.getId().toString());

                    listings.remove(listing);

                    return true;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                listingsFile.set(listing.getId().toString(), null);
                fm.saveFile(listingsFile, "/database/listings");
                chat.log("Removed listing " + chat.formatItem(listing.getItemStack()) + " " + listing.getId().toString());

                listings.remove(listing);
                return true;
        }

        return false;
    }

    public List<Listing> getListings(UUID uuid) {
        List<Listing> personal = new ArrayList<>();
        for (Listing listing : listings) {
            if (listing.getCreator().toString().equalsIgnoreCase(uuid.toString())) personal.add(listing);
        }
        return personal;
    }

    /**
     * Create a new Listing
     *
     * @param creator   Creator of the Auction
     * @param itemStack Listing item
     * @param price     Listing price
     * @return New Listing object
     */
    public Listing create(UUID creator, ItemStack itemStack, Double price) {

        switch (databaseType) {
            case MYSQL:
                try {

                    PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getListingsTable() + " (ID,ITEM_STACK,PRICE,CREATOR,START,END,BUYER) VALUES (?,?,?,?,?,?,?)");

                    UUID id = UUID.randomUUID();
                    long start = System.currentTimeMillis();

                    statement.setString(1, id.toString());
                    statement.setString(2, AuctionHouse.getInstance().encode(itemStack, false));
                    statement.setDouble(3, price);
                    statement.setString(4, creator.toString());
                    statement.setLong(5, start);
                    statement.setLong(6, 0);
                    statement.setString(7, null);

                    Listing listing = new Listing(id, creator, itemStack, price, start);

                    listings.add(listing);

                    statement.executeUpdate();

                    statement.close();

                    chat.log("Created listing " + chat.formatItem(listing.getItemStack()) + " " + id);

                    return listing;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                UUID id = UUID.randomUUID();
                long start = System.currentTimeMillis();
                Listing listing = new Listing(id, creator, itemStack, price, start);
                listings.add(listing);

                listingsFile.set(id + ".ItemStack", AuctionHouse.getInstance().encode(itemStack, false));
                listingsFile.set(id + ".Price", price);
                listingsFile.set(id + ".Creator", creator.toString());
                listingsFile.set(id + ".Start", start);

                fm.saveFile(listingsFile, "/database/listings");

                chat.log("Created listing " + chat.formatItem(listing.getItemStack()) + " " + id);

                return listing;
        }
        return null;
    }

    /**
     * Listing gets bought
     *
     * @param listing Listing bought
     * @param buyer   Player buyer
     * @return -2: MySQL Error -1: Listing not active 0: Buyer too poor 1: Successfully bought 2: Successfully bought and creator online
     */
    public int buy(Listing listing, Player buyer) {

        if (!listings.contains(listing)) return -1;

        Player creator = Bukkit.getPlayer(listing.getCreator());

        if (AuctionHouse.getInstance().getEcon().getBalance(buyer) < listing.getPrice()) return 0;

        AuctionHouse.getInstance().getEcon().withdrawPlayer(buyer, listing.getPrice());
        AuctionHouse.getInstance().getEcon().depositPlayer(Bukkit.getOfflinePlayer(listing.getCreator()), listing.getPrice());

        long end = System.currentTimeMillis();

        switch (databaseType) {
            case MYSQL:
                try {
                    remove(listing);

                    PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getCompletedTable() + " (ID,ITEM_STACK,PRICE,CREATOR,START,END,BUYER) VALUES (?,?,?,?,?,?,?)");

                    statement.setString(1, listing.getId().toString());
                    statement.setString(2, AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                    statement.setDouble(3, listing.getPrice());
                    statement.setString(4, listing.getCreator().toString());
                    statement.setLong(5, listing.getStart());
                    statement.setLong(6, end);
                    statement.setString(7, buyer.getUniqueId().toString());

                    statement.executeUpdate();
                    statement.closeOnCompletion();

                } catch (Exception e) {
                    e.printStackTrace();
                    return -2;
                }
                break;
            case FILE:
                remove(listing);
                YamlConfiguration completedFile = fm.getConfig("/database/completed");

                completedFile.set(listing.getId().toString() + ".ItemStack", AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                completedFile.set(listing.getId().toString() + ".Price", listing.getPrice());
                completedFile.set(listing.getId().toString() + ".Creator", listing.getCreator().toString());
                completedFile.set(listing.getId().toString() + ".Start", listing.getStart());
                completedFile.set(listing.getId().toString() + ".End", end);
                completedFile.set(listing.getId().toString() + ".Buyer", buyer.getUniqueId().toString());

                fm.saveFile(completedFile, "/database/completed");
                break;
        }

        InventoryHandler.addItem(buyer, listing.getItemStack());

        listing.setEnd(end);
        listing.setBuyer(buyer.getUniqueId());

        chat.sendMessage(buyer, AuctionHouse.getInstance().getMessages().getListingBoughtBuyer().replace("%item%", chat.formatItem(listing.getItemStack())).replace("%price%", chat.formatMoney(listing.getPrice())));

        if (creator != null) {
            chat.sendMessage(creator, AuctionHouse.getInstance().getMessages().getListingBoughtCreator().replace("%item%", chat.formatItem(listing.getItemStack())).replace("%price%", chat.formatMoney(listing.getPrice())).replace("%buyer%", buyer.getName()));
            return 2;
        }
        chat.log("Auction " + listing.getId().toString() + " has been bought by " + creator.getUniqueId() + " for " + listing.getPrice() + ".");
        return 1;
    }

    /**
     * @param listing  Listing to edit
     * @param newPrice New listing price
     * @return -1: MySQL Error 0: Listing not edited 1: New price set
     */
    public int setPrice(Listing listing, double newPrice) {
        switch (databaseType) {
            case MYSQL:
                try {

                    PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getListingsTable() + " SET PRICE=? WHERE ID=?");

                    statement.setDouble(1, newPrice);
                    statement.setString(2, listing.getId().toString());

                    statement.executeUpdate();
                    statement.closeOnCompletion();

                    listing.setPrice(newPrice);

                    return 1;

                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                listingsFile.set(listing.getId() + ".Price", newPrice);
                listing.setPrice(newPrice);
                fm.saveFile(listingsFile, "/database/listings");
                return 1;
        }

        return 0;
    }

    /**
     * Set the amount of the listing
     *
     * @param listing     Listing to edit
     * @param newAmount   New amount of the item
     * @param player      Player setting items
     * @param ignoreCheck Whether to check if they have enough items in their inv or not.
     * @return -3: MySQL Error -2: Player can't carry returned items -1: Player does not have enough items to add 0: Through no change 1: Listing amount set
     */
    public int setAmount(Listing listing, int newAmount, Player player, boolean ignoreCheck) {

        if (!ignoreCheck) {
            if (newAmount > listing.getItemStack().getAmount()) {
                int dif = newAmount - listing.getItemStack().getAmount();
                if (InventoryHandler.getPlayersItemAmount(player, listing.getItemStack(), true) < dif) {
                    return -1;
                }
                InventoryHandler.removeItemFromPlayer(player, listing.getItemStack(), dif, true);
            } else if (newAmount < listing.getItemStack().getAmount()) {
                int dif = listing.getItemStack().getAmount() - newAmount;
                ItemStack returnItem = listing.getItemStack().clone();
                returnItem.setAmount(dif);
                if (!InventoryHandler.canCarryItem(player, returnItem, true)) {
                    return -2;
                }
                InventoryHandler.addItem(player, returnItem);
            }
        }

        switch (databaseType) {
            case MYSQL:
                try {

                    PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getListingsTable() + " SET ITEM_STACK=? WHERE ID=?");

                    statement.setString(1, AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                    statement.setString(2, listing.getId().toString());

                    statement.executeUpdate();
                    statement.closeOnCompletion();

                    listing.getItemStack().setAmount(newAmount);

                    return 1;

                } catch (Exception e) {
                    e.printStackTrace();
                    return -3;
                }
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                listingsFile.set(listing.getId().toString() + ".ItemStack", AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                listing.getItemStack().setAmount(newAmount);
                fm.saveFile(listingsFile, "/database/listings");
                return 1;
        }

        return 0;
    }

    /**
     * Get the listing by the Display Item
     *
     * @param displayItem Display Item of the Listing
     * @return Listing Object
     */
    public Listing get(ItemStack displayItem) {
        if (!displayItem.hasItemMeta()) return null;
        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = displayItem.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key, new UUIDDataType())) {
            return get(container.get(key, new UUIDDataType()));
        }
        return null;
    }

    /**
     * Get the listing by its ID
     *
     * @param id ID of listing
     * @return Listing Object
     */
    public Listing get(UUID id) {
        for (Listing listing : listings) {
            if (listing.getId().toString().equals(id.toString())) return listing;
        }
        return null;
    }

    /**
     * Expire a listing
     *
     * @param listing - Listing set to expire.
     * @return -2: MySQL Error -1: Not ready to expire 0: Through no change 1: Player was online 2: Stored in Database
     */
    public int expire(Listing listing) {

        long now = System.currentTimeMillis() / 1000;
        long end = (listing.getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) / 1000;

        if (!(now > end)) return -1;

        Player creator = Bukkit.getPlayer(listing.getCreator());

        if (creator != null) {
            chat.sendMessage(creator, "&fYour listing for &e" + chat.formatItem(listing.getItemStack()) + "&f has expired.");
            InventoryHandler.addItem(creator, listing.getItemStack());
            remove(listing);
            return 1;
        }

        switch (databaseType) {
            case MYSQL:
                try {
                    remove(listing);

                    PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getExpiredTable() + " (ID,ITEM_STACK,CREATOR) VALUES (?,?,?)");

                    statement.setString(1, listing.getId().toString());
                    statement.setString(2, AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                    statement.setString(3, listing.getCreator().toString());

                    statement.executeUpdate();
                    statement.close();
                    return 2;
                } catch (Exception e) {
                    e.printStackTrace();
                    return -2;
                }
            case FILE:
                YamlConfiguration expiredFile = fm.getConfig("/database/expired");

                expiredFile.set(listing.getId().toString() + ".ItemStack", AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                expiredFile.set(listing.getId().toString() + ".Creator", listing.getCreator().toString());

                fm.saveFile(expiredFile, "/database/expired");
                remove(listing);
                return 2;
        }
        return 0;
    }

    /**
     * Load all listings
     */
    public void loadListings() {

        switch (databaseType) {
            case MYSQL:
                try {

                    PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getListingsTable() + " WHERE END=?");

                    statement.setLong(1, 0);

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {

                        UUID id = UUID.fromString(rs.getString(1));
                        ItemStack item = AuctionHouse.getInstance().decode(rs.getString(2));
                        double price = rs.getDouble(3);
                        UUID creator = UUID.fromString(rs.getString(4));
                        long start = rs.getLong(5);

                        Listing l = new Listing(id, creator, item, price, start);

                        listings.add(l);

                        chat.log("Loaded listing " + chat.formatItem(l.getItemStack()));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                Map<String, Object> map = listingsFile.getValues(false);
                for (String str : map.keySet()) {
                    UUID id = UUID.fromString(str);
                    ItemStack item = AuctionHouse.getInstance().decode(Objects.requireNonNull(listingsFile.getString(str + ".ItemStack")));
                    double price = listingsFile.getDouble(str + ".Price");
                    UUID creator = UUID.fromString(Objects.requireNonNull(listingsFile.getString(str + ".Creator")));
                    long start = listingsFile.getLong(str + ".Start");
                    Listing l = new Listing(id, creator, item, price, start);

                    listings.add(l);

                    chat.log("Loaded listing " + chat.formatItem(l.getItemStack()));
                }
                break;
        }
    }

    /**
     * Get the ID of an Expired listing a user is reclaiming.
     *
     * @param itemStack Expired listing ItemStack
     * @return UUID ID
     */
    public UUID getIDofExpired(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return null;
        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (container.has(key, new UUIDDataType())) {
            return container.get(key, new UUIDDataType());
        }
        return null;
    }

    /**
     * Remove a player's expired listing ofter reclaiming
     *
     * @param uuid ID of Listing
     */
    public void removeExpire(UUID uuid, Player player, boolean returnItem) {

        if (returnItem) {
            ItemStack itemStack = null;
            switch (databaseType) {
                case MYSQL:
                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getExpiredTable() + " WHERE CREATOR=?");

                        statement.setString(1, uuid.toString());

                        ResultSet rs = statement.executeQuery();

                        while (rs.next()) {
                            itemStack = AuctionHouse.getInstance().decode(rs.getString(2));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case FILE:
                    YamlConfiguration expiredFile = fm.getConfig("/database/expired");
                    if (!expiredFile.contains(uuid.toString())) return;
                    itemStack = AuctionHouse.getInstance().decode(Objects.requireNonNull(expiredFile.getString(uuid + ".ItemStack")));
            }
            assert itemStack != null;
            if (!InventoryHandler.canCarryItem(player, itemStack, true)) {
                chat.sendMessage(player, "&cYou do not have enough space in your inventory to hold this item.");
                return;
            }
            InventoryHandler.addItem(player, itemStack);
        }

        switch (databaseType) {
            case MYSQL:
                try {
                    PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getExpiredTable() + " WHERE ID=?");

                    delete.setString(1, uuid.toString());

                    delete.executeUpdate();
                    delete.closeOnCompletion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case FILE:
                YamlConfiguration expiredFile = fm.getConfig("/database/expired");
                expiredFile.set(uuid.toString(), null);
                fm.saveFile(expiredFile, "/database/expired");
                break;
        }
    }

    /**
     * Get a List of ItemStacks of expired listings
     *
     * @param uuid   UUID of player
     * @param remove Whether to remove the listing from expired or not
     * @return List of ItemStack
     */
    public List<ItemStack> getExpired(UUID uuid, boolean remove) {
        List<ItemStack> items = new ArrayList<>();

        switch (databaseType) {
            case MYSQL:
                try {

                    PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getExpiredTable() + " WHERE CREATOR=?");

                    statement.setString(1, uuid.toString());

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        ItemStack itemStack = AuctionHouse.getInstance().decode(rs.getString(2));
                        if (remove) {
                            PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getExpiredTable() + " WHERE ID=?");

                            delete.setString(1, rs.getString(1));

                            delete.executeUpdate();
                            delete.closeOnCompletion();
                        } else {
                            NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
                            ItemMeta itemMeta = itemStack.getItemMeta();

                            itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), UUID.fromString(rs.getString(1)));
                            itemStack.setItemMeta(itemMeta);
                        }
                        items.add(itemStack);
                    }

                    statement.closeOnCompletion();
                    return items;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            case FILE:
                YamlConfiguration expiredFile = fm.getConfig("/database/expired");
                Map<String, Object> map = expiredFile.getValues(false);
                for(String str : map.keySet()) {
                    if(Objects.requireNonNull(expiredFile.getString(str + ".Creator")).equalsIgnoreCase(uuid.toString())) {
                        ItemStack itemStack = AuctionHouse.getInstance().decode(Objects.requireNonNull(expiredFile.getString(str + ".ItemStack")));
                        if (remove) {
                            expiredFile.set(str, null);
                        } else {
                            NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
                            ItemMeta itemMeta = itemStack.getItemMeta();

                            itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), UUID.fromString(str));
                            itemStack.setItemMeta(itemMeta);
                        }
                        items.add(itemStack);
                    }
                }
                if(remove) {
                    fm.saveFile(expiredFile, "/database/expired");
                }
        }

        return items;
    }

    private void startCacheCheck() {
        //TODO Implement with the cache system for players searching previous auctions and stuff to save shit and shit.
    }

    /**
     * Start the Expire Check timer
     */
    private void startExpireCheck() {

        expireTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), () -> {
            if (!listings.isEmpty()) {
                List<Listing> copy = new ArrayList<>(listings);
                for (Listing listing : copy) {
                    long now = System.currentTimeMillis() / 1000;
                    long end = (listing.getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) / 1000;

                    ItemStack item = listing.getItemStack();

                    if (now > end) {
                        switch (expire(listing)) {
                            case -1:
                                chat.log("!! Error while saving " + chat.formatItem(item) + ".");
                                break;
                            case 1:
                                chat.log("Listing " + chat.formatItem(item) + " has expired with user online.");
                                break;
                            case 2:
                                chat.log("Listing " + chat.formatItem(item) + " has expired. Item saved in database.");
                                break;
                        }
                    }
                }
            }
        }, 0, 20);
    }

    /**
     * Start the AuctionHouse Refresh Timer
     */
    private void startAuctionHouseRefresh() {
        refreshTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), this::refreshAuctionHouse, 0, 10);
    }

    /**
     * Refresh AuctionHouse Inventories
     */
    public void refreshAuctionHouse() {
        ConcurrentHashMap<String, AkarianInventory> map = AuctionHouse.getInstance().getGuiManager().getGui();
        Set<String> keySet = map.keySet();
        for (String str : keySet) {
            AkarianInventory inv = map.get(str);

            //Auction House GUI
            if (inv instanceof AuctionHouseGUI) {
                AuctionHouseGUI gui = (AuctionHouseGUI) inv;
                Player p = Bukkit.getPlayer(UUID.fromString(str));
                if (p == null)
                    chat.alert("Player found is null " + AuctionHouse.getInstance().getNameManager().getName(str));
                else {
                    gui.updateInventory();
                    p.updateInventory();
                }
            } else //Expired Reclaim GUI
                if (inv instanceof ExpireReclaimGUI) {
                    ExpireReclaimGUI gui = (ExpireReclaimGUI) inv;
                    Player p = Bukkit.getPlayer(UUID.fromString(str));
                    if (p == null)
                        chat.alert("Player found is null " + AuctionHouse.getInstance().getNameManager().getName(str));
                    else {
                        gui.updateInventory();
                        p.updateInventory();
                    }
                }
                //Listing Edit GUI
                else if (inv instanceof ListingEditGUI) {
                    ListingEditGUI gui = (ListingEditGUI) inv;
                    Player p = Bukkit.getPlayer(UUID.fromString(str));
                    if (p == null)
                        chat.alert("Player found is null " + AuctionHouse.getInstance().getNameManager().getName(str));
                    else {
                        gui.updateInventory();
                        p.updateInventory();
                }
            }
            //Shulker View GUI
            else if (inv instanceof ShulkerViewGUI) {
                ShulkerViewGUI gui = (ShulkerViewGUI) inv;
                Player p = Bukkit.getPlayer(UUID.fromString(str));
                if (p == null)
                    chat.alert("Player found is null " + AuctionHouse.getInstance().getNameManager().getName(str));
                else {
                    gui.updateInventory();
                    p.updateInventory();
                }
            }
            //Confirm Buy GUI
            else if (inv instanceof ConfirmBuyGUI) {
                ConfirmBuyGUI gui = (ConfirmBuyGUI) inv;
                Player p = Bukkit.getPlayer(UUID.fromString(str));
                if (p == null)
                    chat.alert("Player found is null " + AuctionHouse.getInstance().getNameManager().getName(str));
                else {
                    gui.updateInventory();
                    p.updateInventory();
                }
            }
            //Auction House Admin GUI
            else if (inv instanceof AuctionHouseAdminGUI) {
                AuctionHouseAdminGUI gui = (AuctionHouseAdminGUI) inv;
                Player p = Bukkit.getPlayer(UUID.fromString(str));
                if (p == null)
                    chat.alert("Player found is null " + AuctionHouse.getInstance().getNameManager().getName(str));
                else {
                    gui.updateInventory();
                    p.updateInventory();
                }

            }
            //Listing Edit Admin GUI
            else if (inv instanceof ListingEditAdminGUI) {
                ListingEditAdminGUI gui = (ListingEditAdminGUI) inv;
                Player p = Bukkit.getPlayer(UUID.fromString(str));
                if (p == null)
                    chat.alert("Player found is null " + AuctionHouse.getInstance().getNameManager().getName(str));
                else {
                    gui.updateInventory();
                    p.updateInventory();
                }
            }
        }
    }

    public void cancelExpireTimer() {
        Bukkit.getScheduler().cancelTask(expireTimer);
    }

    public void cancelRefreshTimer() {
        Bukkit.getScheduler().cancelTask(refreshTimer);
    }
}
