package net.akarian.auctionhouse.listings;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.database.transfer.DatabaseTransferStatusGUI;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.*;
import net.akarian.auctionhouse.utils.events.ListingBoughtEvent;
import net.akarian.auctionhouse.utils.events.ListingCreateEvent;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class ListingManager {

    private final MySQL mySQL;
    private final Chat chat;
    @Getter
    private final List<Listing> active;
    @Getter
    private final List<Listing> unclaimed;
    @Getter
    private final List<Listing> expired;
    @Getter
    private final List<Listing> completed;
    private DatabaseType databaseType;
    private final FileManager fm;
    private int expireTimer;
    private int refreshTimer;
    private boolean transferring;

    public ListingManager() {
        this.mySQL = AuctionHouse.getInstance().getMySQL();
        this.chat = AuctionHouse.getInstance().getChat();
        this.databaseType = AuctionHouse.getInstance().getDatabaseType();
        this.fm = AuctionHouse.getInstance().getFileManager();
        this.active = new ArrayList<>();
        this.unclaimed = new ArrayList<>();
        this.expired = new ArrayList<>();
        this.completed = new ArrayList<>();
        this.transferring = false;
        loadListings();
        loadExpired();
        loadCompleted();
    }

    /**
     * Transfer database to File
     *
     * @param player Player initializing transfer
     * @param inv    Database Transfer Status GUI
     * @return Whether or the transfer was successful or not
     */
    public boolean transferToFile(Player player, DatabaseTransferStatusGUI inv) {
        transferring = true;
        chat.log("&eStarting transfer from MySQL to File initiated by " + player.getName() + ".", false);
        chat.log("Creating Files...", false);
        chat.alert("&eStarting transfer from MySQL to File initiated by " + player.getName() + ".");
        chat.alert("Creating Files...");
        if (!fm.getFile("/database/listings").exists()) {
            fm.createFile("/database/listings");
        }
        if (!fm.getFile("/database/expired").exists()) {
            fm.createFile("/database/expired");
        }
        if (!fm.getFile("/database/completed").exists()) {
            fm.createFile("/database/completed");
        }
        chat.log("Files created. Starting Transferring Process...", false);
        chat.alert("Files created. Starting Transferring Process...");
        long startTime = System.currentTimeMillis();
        AtomicInteger lt = new AtomicInteger(0);
        AtomicInteger et = new AtomicInteger(0);
        AtomicInteger ct = new AtomicInteger(0);
        Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            YamlConfiguration listingsFile = fm.getConfig("/database/listings");
            YamlConfiguration expiredFile = fm.getConfig("/database/expired");
            YamlConfiguration completedFile = fm.getConfig("/database/completed");
            try {
                PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getListingsTable());

                ResultSet rs = statement.executeQuery();

                while (rs.next()) {

                    String id = rs.getString(1);

                    listingsFile.set(id + ".ItemStack", rs.getString(2));
                    listingsFile.set(id + ".Price", rs.getDouble(3));
                    listingsFile.set(id + ".Creator", rs.getString(4));
                    listingsFile.set(id + ".Start", rs.getLong(5));

                    lt.getAndIncrement();

                    PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getListingsTable() + " WHERE ID=?");
                    delete.setString(1, id);
                    delete.executeUpdate();
                    delete.closeOnCompletion();

                }
                fm.saveFile(listingsFile, "/database/listings");
                chat.log("Transferred " + lt + " active listings from MySQL.", false);
                chat.alert("Transferred " + lt + " active listings from MySQL.");
                statement.closeOnCompletion();

            } catch (Exception e) {
                e.printStackTrace();
                chat.log("Error while transferring active listings.", false);
                chat.alert("Error while transferring active listings.");
            }

            try {
                PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getExpiredTable());

                ResultSet rs = statement.executeQuery();

                while (rs.next()) {

                    String id = rs.getString(1);

                    expiredFile.set(id + ".ItemStack", rs.getString(2));
                    expiredFile.set(id + ".Price", rs.getDouble(3));
                    expiredFile.set(id + ".Creator", rs.getString(4));
                    expiredFile.set(id + ".Start", rs.getLong(5));
                    expiredFile.set(id + ".End", rs.getLong(6));
                    expiredFile.set(id + ".Reason", rs.getString(7));
                    expiredFile.set(id + ".Reclaimed", rs.getBoolean(8));

                    et.getAndIncrement();

                    PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getExpiredTable() + " WHERE ID=?");
                    delete.setString(1, id);
                    delete.executeUpdate();
                    delete.closeOnCompletion();

                }
                fm.saveFile(expiredFile, "/database/expired");
                chat.log("Transferred " + et + " expired listings from MySQL.", false);
                chat.alert("Transferred " + et + " expired listings from MySQL.");
                statement.closeOnCompletion();

            } catch (Exception e) {
                e.printStackTrace();
                chat.log("Error while transferring expired listings.", false);
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

                    ct.getAndIncrement();

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
            databaseType = DatabaseType.FILE;
            chat.alert("Transfer complete. Transferred " + (et.get() + lt.get() + ct.get()) + " total listings. (" + (System.currentTimeMillis() - startTime) + "ms)");
            loadListings();
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (player != null)
                        player.openInventory(inv.transferComplete(lt.get(), ct.get(), et.get()));
                }
            });
            mySQL.setTransferring(null);
            mySQL.shutdown();
            transferring = false;
        });
        return true;
    }

    /**
     * Transfer database to MySQL
     *
     * @param player Player initializing transfer
     * @param inv    Database Transfer Status GUI
     * @return Whether the transfer was successful or not
     */
    public boolean transferToMySQL(Player player, DatabaseTransferStatusGUI inv) {
        transferring = true;
        if (AuctionHouse.getInstance().getMySQL().getConnection() == null) return false;
        chat.alert("&eStarting transfer from File to MySQL.");
        long startTime = System.currentTimeMillis();
        AtomicInteger lt = new AtomicInteger(0);
        AtomicInteger et = new AtomicInteger(0);
        AtomicInteger ct = new AtomicInteger(0);
        Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            YamlConfiguration listingsFile = fm.getConfig("/database/listings");
            Set<String> listingsKeySet = listingsFile.getValues(false).keySet();
            YamlConfiguration expiredFile = fm.getConfig("/database/expired");
            Set<String> expireKeySet = expiredFile.getValues(false).keySet();
            YamlConfiguration completedFile = fm.getConfig("/database/completed");
            Set<String> completedKeySet = completedFile.getValues(false).keySet();

            for (String s : listingsKeySet) {
                String itemstack = listingsFile.getString(s + ".ItemStack");
                double price = listingsFile.getDouble(s + ".Price");
                String creator = listingsFile.getString(s + ".Creator");
                long start = listingsFile.getLong(s + ".Start");
                try {
                    PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getListingsTable() + " (ID,ITEM_STACK,PRICE,CREATOR,START,END,BUYER) VALUES (?,?,?,?,?,?,?)");

                    statement.setString(1, s);
                    statement.setString(2, itemstack);
                    statement.setDouble(3, price);
                    statement.setString(4, creator);
                    statement.setLong(5, start);
                    statement.setLong(6, 0);
                    statement.setString(7, null);

                    statement.executeUpdate();
                    statement.close();

                    lt.getAndIncrement();

                    listingsFile.set(s, null);

                    chat.log("Transferred listing " + s, AuctionHouse.getInstance().isDebug());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            chat.alert("Transferred " + lt + " listings to MySQL.");
            for (String s : expireKeySet) {
                String creator = expiredFile.getString(s + ".Creator");
                Double price = expiredFile.getDouble(s + ".Price");
                String itemStack = expiredFile.getString(s + ".ItemStack");
                long start = expiredFile.getLong(s + ".Start");
                long end = expiredFile.getLong(s + ".End");
                String reason = expiredFile.getString(s + ".Reason");
                boolean reclaimed = expiredFile.getBoolean(s + ".Reclaimed");

                try {
                    PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getExpiredTable() + " (ID,ITEM_STACK,PRICE,CREATOR,START,END,REASON,RECLAIMED) VALUES (?,?,?,?,?,?,?,?)");

                    statement.setString(1, s);
                    statement.setString(2, itemStack);
                    statement.setDouble(3, price);
                    statement.setString(4, creator);
                    statement.setLong(5, start);
                    statement.setLong(6, end);
                    statement.setString(7, reason);
                    statement.setBoolean(8, reclaimed);

                    statement.executeUpdate();
                    statement.close();
                    et.getAndIncrement();

                    expiredFile.set(s, null);

                    chat.log("Transferred expired listing " + s, AuctionHouse.getInstance().isDebug());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            chat.alert("Transferred " + et + " expired listings to MySQL.");
            for (String s : completedKeySet) {

                String itemstack = completedFile.getString(s + ".ItemStack");
                double price = completedFile.getDouble(s + ".Price");
                String creator = completedFile.getString(s + ".Creator");
                long start = completedFile.getLong(s + ".Start");
                long end = completedFile.getLong(s + ".End");
                String buyer = completedFile.getString(s + ".Buyer");

                try {
                    PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getCompletedTable() + " (ID,ITEM_STACK,PRICE,CREATOR,START,END,BUYER) VALUES (?,?,?,?,?,?,?)");

                    statement.setString(1, s);
                    statement.setString(2, itemstack);
                    statement.setDouble(3, price);
                    statement.setString(4, creator);
                    statement.setLong(5, start);
                    statement.setLong(6, end);
                    statement.setString(7, buyer);

                    statement.executeUpdate();
                    statement.closeOnCompletion();

                    completedFile.set(s, null);

                    chat.log("Transferred complete listing " + s, AuctionHouse.getInstance().isDebug());
                    ct.getAndIncrement();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            chat.alert("Transferred " + ct + " completed listings to MySQL.");

            fm.saveFile(listingsFile, "/database/listings");
            fm.saveFile(expiredFile, "/database/expired");
            fm.saveFile(completedFile, "/database/completed");
            AuctionHouse.getInstance().getConfigFile().setDatabaseType(DatabaseType.MYSQL);
            databaseType = DatabaseType.MYSQL;
            chat.alert("Transfer complete. Transferred " + (et.get() + lt.get() + ct.get()) + " total listings. (" + (System.currentTimeMillis() - startTime) + "ms).");
            loadListings();
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (player != null)
                        player.openInventory(inv.transferComplete(lt.get(), ct.get(), et.get()));
                }
            });
            mySQL.setTransferring(null);
            transferring = false;
        });
        return true;
    }

    /**
     * Remove/Expire a Listing
     *
     * @param remover Player removing listing.
     * @param listing Listing to save remove
     * @return -2: MySQL Error 0: Error while removing 1: Successfully Removed
     */
    public int safeRemove(String remover, Listing listing) {

        if (!active.contains(listing)) return 0;

        remove(listing);

        Player creator = Bukkit.getPlayer(listing.getCreator());

        int expire = expire(listing, false, true, remover);
        if (expire == 1) {
            if (creator != null)
                chat.sendMessage(creator, AuctionHouse.getInstance().getMessages().getListingRemoved().replace("%item%", chat.formatItem(listing.getItemStack())));
            chat.log("Safe Removed " + chat.formatItem(listing.getItemStack()) + " by " + remover + ". ID: " + listing.getId().toString(), AuctionHouse.getInstance().isDebug());
            return 1;
        }
        return expire;
    }

    public boolean removeExpired(Listing listing) {
        switch (databaseType) {
            case MYSQL:
                AtomicBoolean ret = new AtomicBoolean(false);
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getExpiredTable() + " WHERE ID=?");

                        statement.setString(1, listing.getId().toString());

                        statement.executeUpdate();
                        statement.closeOnCompletion();

                        chat.log("Removed expired listing " + chat.formatItem(listing.getItemStack()) + " " + listing.getId().toString(), AuctionHouse.getInstance().isDebug());

                        expired.remove(listing);
                        unclaimed.remove(listing);

                        ret.set(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                        ret.set(false);
                    }
                });
                return ret.get();
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/expired");
                listingsFile.set(listing.getId().toString(), null);
                fm.saveFile(listingsFile, "/database/expired");
                chat.log("Removed expired listing " + chat.formatItem(listing.getItemStack()) + " " + listing.getId().toString(), AuctionHouse.getInstance().isDebug());

                expired.remove(listing);
                unclaimed.remove(listing);
                return true;
        }
        return false;
    }

    public boolean removeCompleted(Listing listing) {
        AuctionHouse.getInstance().getEcon().depositPlayer(Bukkit.getOfflinePlayer(listing.getBuyer()), listing.getPrice());
        switch (databaseType) {
            case MYSQL:
                AtomicBoolean ret = new AtomicBoolean(false);
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getCompletedTable() + " WHERE ID=?");

                        statement.setString(1, listing.getId().toString());

                        statement.executeUpdate();
                        statement.closeOnCompletion();

                        chat.log("Removed completed listing " + chat.formatItem(listing.getItemStack()) + " " + listing.getId().toString(), AuctionHouse.getInstance().isDebug());

                        completed.remove(listing);

                        ret.set(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                        ret.set(false);
                    }
                });
                return ret.get();
            case FILE:
                YamlConfiguration completedFile = fm.getConfig("/database/completed");
                completedFile.set(listing.getId().toString(), null);
                fm.saveFile(completedFile, "/database/completed");
                chat.log("Removed completed listing " + chat.formatItem(listing.getItemStack()) + " " + listing.getId().toString(), AuctionHouse.getInstance().isDebug());

                completed.remove(listing);
                return true;
        }
        return false;
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
                AtomicBoolean ret = new AtomicBoolean(false);
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getListingsTable() + " WHERE ID=?");

                        statement.setString(1, listing.getId().toString());

                        statement.executeUpdate();
                        statement.closeOnCompletion();

                        chat.log("Removed listing " + chat.formatItem(listing.getItemStack()) + " " + listing.getId().toString(), AuctionHouse.getInstance().isDebug());

                        active.remove(listing);

                        ret.set(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                        ret.set(false);
                    }
                });
                return ret.get();
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                listingsFile.set(listing.getId().toString(), null);
                fm.saveFile(listingsFile, "/database/listings");
                chat.log("Removed listing " + chat.formatItem(listing.getItemStack()) + " " + listing.getId().toString(), AuctionHouse.getInstance().isDebug());

                active.remove(listing);
                return true;
        }

        return false;
    }

    public List<Listing> getActive(UUID uuid) {
        List<Listing> personal = new ArrayList<>();
        for (Listing listing : active) {
            if (listing.getCreator().toString().equalsIgnoreCase(uuid.toString())) personal.add(listing);
        }
        return personal;
    }

    public List<Listing> getExpired(UUID uuid) {
        List<Listing> personal = new ArrayList<>();
        for (Listing listing : expired) {
            if (listing.getCreator().toString().equalsIgnoreCase(uuid.toString())) personal.add(listing);
        }
        return personal;
    }

    public List<Listing> getCompleted(UUID uuid) {
        List<Listing> personal = new ArrayList<>();
        for (Listing listing : completed) {
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
        Player p = Bukkit.getPlayer(creator);

        AuctionHouse.getInstance().getEcon().withdrawPlayer(Bukkit.getOfflinePlayer(creator), AuctionHouse.getInstance().getConfigFile().calculateListingFee(price));

        UUID id = UUID.randomUUID();
        long start = System.currentTimeMillis();

        Listing listing = new Listing(id, creator, itemStack, price, start);

        switch (databaseType) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getListingsTable() + " (ID,ITEM_STACK,PRICE,CREATOR,START,END,BUYER) VALUES (?,?,?,?,?,?,?)");


                        statement.setString(1, id.toString());
                        statement.setString(2, AuctionHouse.getInstance().encode(itemStack, false));
                        statement.setDouble(3, price);
                        statement.setString(4, creator.toString());
                        statement.setLong(5, start);
                        statement.setLong(6, 0);
                        statement.setString(7, null);

                        active.add(listing);

                        statement.executeUpdate();

                        statement.close();

                        chat.log("Created listing " + chat.formatItem(listing.getItemStack()) + " " + id, AuctionHouse.getInstance().isDebug());

                        chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getCreateListing()
                                .replace("%item%", chat.formatItem(listing.getItemStack())).replace("%price%", chat.formatMoney(listing.getPrice())));

                        p.getInventory().removeItem(itemStack);
                        AuctionHouse.getInstance().getCooldownManager().setCooldown(p);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return listing;
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                active.add(listing);

                listingsFile.set(id + ".ItemStack", AuctionHouse.getInstance().encode(itemStack, false));
                listingsFile.set(id + ".Price", price);
                listingsFile.set(id + ".Creator", creator.toString());
                listingsFile.set(id + ".Start", start);

                fm.saveFile(listingsFile, "/database/listings");

                chat.log("Created listing " + chat.formatItem(listing.getItemStack()) + " " + id, AuctionHouse.getInstance().isDebug());

                chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getCreateListing()
                        .replace("%item%", chat.formatItem(listing.getItemStack())).replace("%price%", chat.formatMoney(listing.getPrice())));


                p.getInventory().removeItem(itemStack);
                AuctionHouse.getInstance().getCooldownManager().setCooldown(p);

                Bukkit.getServer().getPluginManager().callEvent(new ListingCreateEvent(listing));

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

        if (!active.contains(listing)) return -1;

        Player creator = Bukkit.getPlayer(listing.getCreator());

        if (AuctionHouse.getInstance().getEcon().getBalance(buyer) < listing.getPrice()) return 0;

        AuctionHouse.getInstance().getEcon().withdrawPlayer(buyer, listing.getPrice());
        AuctionHouse.getInstance().getEcon().depositPlayer(Bukkit.getOfflinePlayer(listing.getCreator()), listing.getPrice());

        long end = System.currentTimeMillis();

        AtomicBoolean ret = new AtomicBoolean(false);

        switch (databaseType) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
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
                        ret.set(true);
                    }
                });
                if (ret.get()) {
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

        Bukkit.getServer().getPluginManager().callEvent(new ListingBoughtEvent(listing));
        chat.log("Auction " + listing.getId().toString() + " has been bought by " + listing.getCreator().toString() + " for " + listing.getPrice() + ".", AuctionHouse.getInstance().isDebug());
        if (creator != null) {
            chat.sendMessage(creator, AuctionHouse.getInstance().getMessages().getListingBoughtCreator().replace("%item%", chat.formatItem(listing.getItemStack())).replace("%price%", chat.formatMoney(listing.getPrice())).replace("%buyer%", buyer.getName()));
            return 2;
        }
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
                AtomicInteger ret = new AtomicInteger(-1);
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getListingsTable() + " SET PRICE=? WHERE ID=?");

                        statement.setDouble(1, newPrice);
                        statement.setString(2, listing.getId().toString());

                        statement.executeUpdate();
                        statement.closeOnCompletion();

                        listing.setPrice(newPrice);

                        ret.set(1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return ret.get();
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
                AtomicInteger ret = new AtomicInteger(-3);
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getListingsTable() + " SET ITEM_STACK=? WHERE ID=?");

                        statement.setString(1, AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                        statement.setString(2, listing.getId().toString());

                        statement.executeUpdate();
                        statement.closeOnCompletion();

                        listing.getItemStack().setAmount(newAmount);

                        ret.set(1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return ret.get();
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
        assert itemMeta != null;
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
        for (Listing listing : active) {
            if (listing.getId().toString().equals(id.toString())) return listing;
        }
        for (Listing listing : unclaimed) {
            if (listing.getId().toString().equals(id.toString())) return listing;
        }
        for (Listing listing : expired) {
            if (listing.getId().toString().equals(id.toString())) return listing;
        }
        for (Listing listing : completed) {
            if (listing.getId().toString().equals(id.toString())) return listing;
        }
        return null;
    }

    /**
     * Expire a listing
     *
     * @param listing - Listing set to expire.
     * @return -3: Already expired -2: MySQL Error -1: Not ready to expire 0: Through no change 1: Stored in Database
     */
    public int expire(Listing listing, boolean notify, boolean ignoreTime, String reason) {

        long now = System.currentTimeMillis() / 1000;
        long end = (listing.getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) / 1000;

        if (!(now > end) && !ignoreTime) return -1;
        if (listing.getEnd() != 0) return -3;

        Player creator = Bukkit.getPlayer(listing.getCreator());

        if (creator != null && notify) {
            chat.sendMessage(creator, "&fYour listing for &e" + chat.formatItem(listing.getItemStack()) + "&f has expired.");
        }

        switch (databaseType) {
            case MYSQL:
                AtomicInteger ret = new AtomicInteger(-2);
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        remove(listing);

                        long newEnd = System.currentTimeMillis();
                        listing.setEnd(newEnd);

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getExpiredTable() + " (ID,ITEM_STACK,PRICE,CREATOR,START,END,REASON,RECLAIMED) VALUES (?,?,?,?,?,?,?,?)");

                        statement.setString(1, listing.getId().toString());
                        statement.setString(2, AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                        statement.setDouble(3, listing.getPrice());
                        statement.setString(4, listing.getCreator().toString());
                        statement.setLong(5, listing.getStart());
                        statement.setLong(6, listing.getEnd());
                        statement.setString(7, reason);
                        statement.setBoolean(8, false);

                        statement.executeUpdate();
                        statement.close();
                        unclaimed.add(listing);
                        expired.add(listing);
                        ret.set(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return ret.get();
            case FILE:
                long newEnd = System.currentTimeMillis();
                YamlConfiguration expiredFile = fm.getConfig("/database/expired");
                listing.setEnd(newEnd);
                expiredFile.set(listing.getId().toString() + ".ItemStack", AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                expiredFile.set(listing.getId().toString() + ".Price", listing.getPrice());
                expiredFile.set(listing.getId().toString() + ".Creator", listing.getCreator().toString());
                expiredFile.set(listing.getId().toString() + ".Start", listing.getStart());
                expiredFile.set(listing.getId().toString() + ".End", listing.getEnd());
                expiredFile.set(listing.getId().toString() + ".Reason", reason);
                expiredFile.set(listing.getId().toString() + ".Reclaimed", false);

                unclaimed.add(listing);
                expired.add(listing);

                fm.saveFile(expiredFile, "/database/expired");
                remove(listing);
                return 1;
        }
        return 0;
    }

    /**
     * Load all active listings
     */
    public void loadListings() {
        active.clear();
        chat.log("Loading listings...", AuctionHouse.getInstance().isDebug());
        AtomicInteger num = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();
        switch (databaseType) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
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

                            active.add(l);
                            num.getAndIncrement();

                            chat.log("Loaded listing " + chat.formatItem(l.getItemStack()), AuctionHouse.getInstance().isDebug());

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                break;
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                Map<String, Object> map = listingsFile.getValues(false);
                for (String str : map.keySet()) {
                    UUID id = UUID.fromString(str);
                    if(listingsFile.getString(str + ".ItemStack") == null) {
                         chat.log("Error while loading auction with ID " + id.toString() + ". Skipping...", AuctionHouse.getInstance().isDebug());
                         errors.incrementAndGet();
                        continue;
                    }
                    ItemStack item = AuctionHouse.getInstance().decode(Objects.requireNonNull(listingsFile.getString(str + ".ItemStack")));
                    if(listingsFile.contains(str + ".Price")) {
                        chat.log("Error while loading auction with price " + id.toString() + ". Skipping...", AuctionHouse.getInstance().isDebug());
                        errors.incrementAndGet();
                        continue;
                    }
                    double price = listingsFile.getDouble(str + ".Price");
                    if(listingsFile.contains(str + ".Creator")) {
                        chat.log("Error while loading auction with creator " + id.toString() + ". Skipping...", AuctionHouse.getInstance().isDebug());
                        errors.incrementAndGet();
                        continue;
                    }
                    UUID creator = UUID.fromString(Objects.requireNonNull(listingsFile.getString(str + ".Creator")));
                    if(listingsFile.contains(str + ".Start")) {
                        chat.log("Error while loading auction with Start " + id.toString() + ". Skipping...", AuctionHouse.getInstance().isDebug());
                        errors.incrementAndGet();
                        continue;
                    }
                    long start = listingsFile.getLong(str + ".Start");
                    Listing l = new Listing(id, creator, item, price, start);

                    active.add(l);
                    num.getAndIncrement();

                    chat.log("Loaded listing " + chat.formatItem(l.getItemStack()), AuctionHouse.getInstance().isDebug());
                }
                break;
        }
        startExpireCheck();
        startAuctionHouseRefresh();
        chat.log("Loaded " + num.get() + " active listings.", AuctionHouse.getInstance().isDebug());
        if(errors.get() > 0) {
            AuctionHouse.getInstance().getLogger().log(Level.SEVERE, "There was an error loading " + errors.get() + " auctions. Please review console to see which.");
            chat.log("There was an error loading " + errors.get() + " auctions. Please review console to see which.", AuctionHouse.getInstance().isDebug());
        }
    }

    /**
     * Load all expired listings
     */
    public void loadExpired() {
        chat.log("Loading Expired listings...", AuctionHouse.getInstance().isDebug());
        AtomicInteger errors = new AtomicInteger();
        switch (databaseType) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getExpiredTable());

                        ResultSet rs = statement.executeQuery();

                        while (rs.next()) {

                            UUID id = UUID.fromString(rs.getString(1));
                            ItemStack item = AuctionHouse.getInstance().decode(rs.getString(2));
                            double price = rs.getDouble(3);
                            UUID creator = UUID.fromString(rs.getString(4));
                            long start = rs.getLong(5);
                            long end = rs.getLong(6);
                            String reason = rs.getString(7);
                            boolean reclaimed = rs.getBoolean(8);

                            Listing l = new Listing(id, creator, item, price, start);

                            l.setEnd(end);
                            l.setEnd(end);
                            l.setEndReason(reason);
                            l.setReclaimed(reclaimed);

                            if (!reclaimed)
                                unclaimed.add(l);
                            expired.add(l);

                            chat.log("Loaded expired listing " + chat.formatItem(l.getItemStack()), AuctionHouse.getInstance().isDebug());

                        }
                        chat.log("Loaded " + expired.size() + " expired listings and " + unclaimed.size() + " unclaimed expired listings.", AuctionHouse.getInstance().isDebug());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                break;
            case FILE:

                YamlConfiguration listingsFile = fm.getConfig("/database/expired");
                Map<String, Object> map = listingsFile.getValues(false);

                for (String str : map.keySet()) {

                    UUID id = UUID.fromString(str);
                    if(listingsFile.getString(str + ".ItemStack") == null) {
                        chat.log("Error while loading auction with ID " + id.toString() + ". Skipping...", AuctionHouse.getInstance().isDebug());
                        errors.incrementAndGet();
                        continue;
                    }
                    ItemStack item = AuctionHouse.getInstance().decode(Objects.requireNonNull(listingsFile.getString(str + ".ItemStack")));
                    double price = listingsFile.getDouble(str + ".Price");
                    UUID creator = UUID.fromString(Objects.requireNonNull(listingsFile.getString(str + ".Creator")));
                    long start = listingsFile.getLong(str + ".Start");
                    long end = listingsFile.getLong(str + ".End");
                    String reason = listingsFile.getString(str + ".Reason");
                    boolean reclaimed = listingsFile.getBoolean(str + ".Reclaimed");

                    Listing l = new Listing(id, creator, item, price, start);

                    l.setEnd(end);
                    l.setEndReason(reason);
                    l.setReclaimed(false);
                    l.setReclaimed(reclaimed);

                    if (!reclaimed)
                        unclaimed.add(l);
                    expired.add(l);

                    chat.log("Loaded expired listing " + chat.formatItem(l.getItemStack()), AuctionHouse.getInstance().isDebug());

                }
                chat.log("Loaded " + expired.size() + " expired listings and " + unclaimed.size() + " unclaimed expired listings.", AuctionHouse.getInstance().isDebug());
                if(errors.get() > 0) {
                    AuctionHouse.getInstance().getLogger().log(Level.SEVERE, "There was an error loading " + errors.get() + " expired auctions. Please review console to see which.");
                    chat.log("There was an error loading " + errors.get() + " expired auctions. Please review console to see which.", AuctionHouse.getInstance().isDebug());
                }
                break;
        }
    }

    public void loadCompleted() {
        chat.log("Loading Completed listings...", AuctionHouse.getInstance().isDebug());
        AtomicInteger errors = new AtomicInteger();
        switch (databaseType) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getCompletedTable());

                        ResultSet rs = statement.executeQuery();

                        while (rs.next()) {

                            UUID id = UUID.fromString(rs.getString(1));
                            ItemStack item = AuctionHouse.getInstance().decode(rs.getString(2));
                            double price = rs.getDouble(3);
                            UUID creator = UUID.fromString(rs.getString(4));
                            long start = rs.getLong(5);
                            long end = rs.getLong(6);
                            UUID buyer = UUID.fromString(rs.getString(7));

                            Listing l = new Listing(id, creator, item, price, start);

                            l.setEnd(end);
                            l.setBuyer(buyer);

                            completed.add(l);

                            chat.log("Loaded completed listing " + chat.formatItem(l.getItemStack()), AuctionHouse.getInstance().isDebug());

                        }
                        chat.log("Loaded " + completed.size() + " completed listings.", AuctionHouse.getInstance().isDebug());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                break;
            case FILE:
                YamlConfiguration completedFile = fm.getConfig("/database/completed");
                Map<String, Object> map = completedFile.getValues(false);

                for (String str : map.keySet()) {

                    UUID id = UUID.fromString(str);
                    if(completedFile.getString(str + ".ItemStack") == null) {
                        chat.log("Error while loading auction with ID " + id.toString() + ". Skipping...", AuctionHouse.getInstance().isDebug());
                        errors.incrementAndGet();
                        continue;
                    }
                    ItemStack item = AuctionHouse.getInstance().decode(Objects.requireNonNull(completedFile.getString(str + ".ItemStack")));
                    double price = completedFile.getDouble(str + ".Price");
                    UUID creator = UUID.fromString(Objects.requireNonNull(completedFile.getString(str + ".Creator")));
                    long start = completedFile.getLong(str + ".Start");
                    long end = completedFile.getLong(str + ".End");
                    UUID buyer = UUID.fromString(Objects.requireNonNull(completedFile.getString(str + ".Buyer")));

                    Listing l = new Listing(id, creator, item, price, start);

                    l.setEnd(end);
                    l.setBuyer(buyer);

                    completed.add(l);

                    chat.log("Loaded completed listing " + chat.formatItem(l.getItemStack()), AuctionHouse.getInstance().isDebug());

                }
                chat.log("Loaded " + completed.size() + " completed listings.", AuctionHouse.getInstance().isDebug());
                if(errors.get() > 0) {
                    AuctionHouse.getInstance().getLogger().log(Level.SEVERE, "There was an error loading " + errors.get() + " completed auctions. Please review console to see which.");
                    chat.log("There was an error loading " + errors.get() + " completed auctions. Please review console to see which.", AuctionHouse.getInstance().isDebug());
                }
                break;
        }
    }

    /**
     * Remove a player's expired listing ofter reclaiming
     *
     * @param listing Listing to expire
     * @return -3: MySQL Error -2: Already Reclaimed -1: Player Can't Hold Item 0: Through no change 1: Item Reclaimed
     */
    public int reclaimExpire(Listing listing, Player player, boolean returnItem) {
        AtomicInteger ret = new AtomicInteger(0);
        if (listing.isReclaimed()) return -2;
        if (returnItem) {
            final ItemStack[] itemStack = {null};
            switch (databaseType) {
                case MYSQL:
                    Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                        try {
                            PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getExpiredTable() + " WHERE ID=?");

                            statement.setString(1, listing.getId().toString());

                            ResultSet rs = statement.executeQuery();

                            while (rs.next()) {
                                if (rs.getBoolean(8)) ret.set(-2);
                                else {
                                    itemStack[0] = AuctionHouse.getInstance().decode(rs.getString(2));
                                    assert itemStack[0] != null;
                                    if (!InventoryHandler.canCarryItem(player, itemStack[0], true)) {
                                        chat.sendMessage(player, "&cYou do not have enough space in your inventory to hold this item.");
                                        ret.set(-1);
                                    } else {
                                        InventoryHandler.addItem(player, itemStack[0]);
                                        chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getExpiredReclaim().replace("%item%", chat.formatItem(listing.getItemStack())));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    if (ret.get() != 0)
                        return ret.get();
                    break;
                case FILE:
                    YamlConfiguration expiredFile = fm.getConfig("/database/expired");
                    if (expiredFile.getBoolean(listing.getId() + ".Reclaimed")) return -2;
                    itemStack[0] = AuctionHouse.getInstance().decode(Objects.requireNonNull(expiredFile.getString(listing.getId() + ".ItemStack")));
                    assert itemStack[0] != null;
                    if (!InventoryHandler.canCarryItem(player, itemStack[0], true)) {
                        chat.sendMessage(player, "&cYou do not have enough space in your inventory to hold this item.");
                        return -1;
                    }
                    InventoryHandler.addItem(player, itemStack[0]);
                    chat.sendMessage(player, "&eYou have reclaimed your listing for " + chat.formatItem(listing.getItemStack()));
            }
        }

        switch (databaseType) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        PreparedStatement update = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getExpiredTable() + " SET RECLAIMED=? WHERE ID=?");

                        update.setBoolean(1, true);
                        update.setString(2, listing.getId().toString());

                        update.executeUpdate();
                        update.closeOnCompletion();
                        unclaimed.remove(listing);
                        listing.setReclaimed(true);
                        ret.set(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ret.set(-3);
                    }
                });
                return ret.get();
            case FILE:
                YamlConfiguration expiredFile = fm.getConfig("/database/expired");
                expiredFile.set(listing.getId().toString() + ".Reclaimed", true);
                fm.saveFile(expiredFile, "/database/expired");
                unclaimed.remove(listing);
                listing.setReclaimed(true);
                return 1;
        }
        return 0;
    }

    /**
     * Get a List of unclaimed Expired Listings
     *
     * @param uuid UUID of player
     * @return List of ItemStack
     */
    public List<Listing> getUnclaimedExpired(UUID uuid) {
        List<Listing> a = new ArrayList<>();

        for (Listing listing : unclaimed) {
            if (!listing.getCreator().toString().equalsIgnoreCase(uuid.toString())) continue;
            if (listing.isReclaimed()) continue;

            a.add(listing);
        }
        return a;
    }

    /**
     * Start the Expire Check timer
     */
    private void startExpireCheck() {

        expireTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), () -> {
            if (!active.isEmpty()) {
                List<Listing> copy = new ArrayList<>(active);
                for (Listing listing : copy) {
                    long now = System.currentTimeMillis() / 1000;
                    long end = (listing.getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) / 1000;

                    ItemStack item = listing.getItemStack();

                    for(User user : AuctionHouse.getInstance().getUserManager().getUsers()) {
                        if(!user.getUserSettings().getNotified().contains(listing) && end - now < user.getUserSettings().getAlertNearExpireTime() && Bukkit.getPlayer(user.getUuid()) != null) {
                            user.getUserSettings().getNotified().add(listing);
                            chat.sendMessage(Objects.requireNonNull(Bukkit.getPlayer(user.getUuid())), AuctionHouse.getInstance().getMessages().getSt_expire_message().replace("%listing%", chat.formatItem(listing.getItemStack())).replace("%time%", chat.formatTime(end - now)).replace("%seller%", Objects.requireNonNull(Bukkit.getOfflinePlayer(listing.getCreator()).getName())));
                        }
                    }

                    if (now > end) {
                        switch (expire(listing, true, false, "TIME")) {
                            case -1:
                                chat.log("!! Error while saving " + chat.formatItem(item) + ".", AuctionHouse.getInstance().isDebug());
                                break;
                            case 1:
                                chat.log("Listing " + chat.formatItem(item) + " has expired with user online.", AuctionHouse.getInstance().isDebug());
                                break;
                            case 2:
                                chat.log("Listing " + chat.formatItem(item) + " has expired. Item saved in database.", AuctionHouse.getInstance().isDebug());
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
            Player p = Bukkit.getPlayer(UUID.fromString(str));
            inv.updateInventory();
            assert p != null;
            p.updateInventory();
        }
    }

    public void cancelExpireTimer() {
        Bukkit.getScheduler().cancelTask(expireTimer);
    }

    public void cancelRefreshTimer() {
        Bukkit.getScheduler().cancelTask(refreshTimer);
    }
}
