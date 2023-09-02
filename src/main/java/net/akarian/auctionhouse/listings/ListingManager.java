package net.akarian.auctionhouse.listings;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.ExpireReclaimGUI;
import net.akarian.auctionhouse.guis.admin.database.transfer.DatabaseTransferStatusGUI;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.*;
import net.akarian.auctionhouse.utils.events.ListingBoughtEvent;
import net.akarian.auctionhouse.utils.events.ListingCreateEvent;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitTask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class ListingManager {

    private final MySQL mySQL;
    private final Chat chat;
    @Getter
    private final ArrayList<Listing> active;
    @Getter
    private final ArrayList<Listing> unclaimed;
    @Getter
    private final ArrayList<Listing> expired;
    @Getter
    private final ArrayList<Listing> completed;
    @Getter
    private final ArrayList<String> blacklistedNames;
    @Getter
    private final ArrayList<Material> blacklistedMaterials;
    @Getter
    private final ArrayList<ItemStack> blacklistedItemStacks;
    private final FileManager fm;
    private DatabaseType databaseType;
    private BukkitTask expireTimer;
    private BukkitTask refreshTimer;
    private BukkitTask mysqlSyncTask;
    private long mysqlSyncTime;
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
        this.blacklistedNames = new ArrayList<>();
        this.blacklistedMaterials = new ArrayList<>();
        this.blacklistedItemStacks = new ArrayList<>();
        this.transferring = false;
    }

    public void startup() {
        loadExpired();
        loadListings();
        loadCompleted();
        loadBlacklist();
    }

    public void addNameBlacklist(String name) {
        if (blacklistedNames.contains(name)) return;
        blacklistedNames.add(name);
    }

    public void addMaterialBlacklist(Material material) {
        if (blacklistedMaterials.contains(material)) return;
        blacklistedMaterials.add(material);
    }

    public void addItemStackBlacklist(ItemStack itemStack) {
        itemStack.setAmount(1);
        if (blacklistedItemStacks.contains(itemStack)) return;
        blacklistedItemStacks.add(itemStack);
    }

    public boolean checkBlacklist(ItemStack itemStack) {
        ItemStack clonedItemStack = itemStack.clone();
        clonedItemStack.setAmount(1);
        String itemName = chat.formatItem(itemStack);
        String[] split = itemName.split("x ", 2);
        itemName = split[1];

        if (blacklistedNames.contains(itemName)) return false;
        if (blacklistedMaterials.contains(itemStack.getType())) return false;
        for (ItemStack item : blacklistedItemStacks) {
            if (InventoryHandler.compareItemStacks(clonedItemStack, item, true)) return false;
            String itemName2 = chat.formatItem(item);
            String[] split2 = itemName2.split("x ", 2);
            itemName2 = split2[1];
            if (itemName2.equals(itemName)) return false;
        }
        return true;
    }

    public void saveBlacklist() {

        if (!fm.getFile("blacklist").exists()) {
            fm.createFile("blacklist");
        }
        YamlConfiguration blacklistConfig = fm.getConfig("blacklist");
        List<String> materialList = new ArrayList<>();
        for (Material material : blacklistedMaterials) {
            materialList.add(material.name());
        }
        blacklistConfig.set("Materials", materialList);
        blacklistConfig.set("Names", blacklistedNames);
        List<String> encodedItemStacks = new ArrayList<>();
        for (ItemStack itemStack : blacklistedItemStacks) {
            encodedItemStacks.add(AuctionHouse.getInstance().encode(itemStack, true));
        }
        blacklistConfig.set("ItemStacks", encodedItemStacks);

        fm.saveFile(blacklistConfig, "blacklist");

    }

    public void loadBlacklist() {

        if (!fm.getFile("blacklist").exists()) {
            fm.createFile("blacklist");
            return;
        }
        YamlConfiguration blacklistConfig = fm.getConfig("blacklist");

        List<String> materialList = blacklistConfig.getStringList("Materials");
        List<String> namesList = blacklistConfig.getStringList("Names");
        List<String> itemStackList = blacklistConfig.getStringList("ItemStacks");

        for (String s : materialList) {
            addMaterialBlacklist(Material.getMaterial(s));
        }

        for (String s : namesList) {
            addNameBlacklist(s);
        }

        for (String s : itemStackList) {
            blacklistedItemStacks.add(AuctionHouse.getInstance().decode(s));
        }

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
        if (!fm.getFile("/database/users").exists()) {
            fm.createFile("/database/users");
        }
        chat.log("Files created. Starting Transferring Process...", false);
        chat.alert("Files created. Starting Transferring Process...");
        long startTime = System.currentTimeMillis();
        AtomicInteger lt = new AtomicInteger(0);
        AtomicInteger et = new AtomicInteger(0);
        AtomicInteger ct = new AtomicInteger(0);
        AtomicInteger ut = new AtomicInteger(0);
        Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            YamlConfiguration listingsFile = fm.getConfig("/database/listings");
            YamlConfiguration expiredFile = fm.getConfig("/database/expired");
            YamlConfiguration completedFile = fm.getConfig("/database/completed");
            YamlConfiguration usersFile = fm.getConfig("/database/users");
            try {
                PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getListingsTable());

                ResultSet rs = statement.executeQuery();

                while (rs.next()) {

                    String id = rs.getString(1);

                    listingsFile.set(id + ".ItemStack", rs.getString(2));
                    listingsFile.set(id + ".Price", rs.getDouble(3));
                    listingsFile.set(id + ".Starting Bid", rs.getDouble(4));
                    listingsFile.set(id + ".Minimum Increment", rs.getDouble(5));
                    listingsFile.set(id + ".Bidder", rs.getString(6));
                    listingsFile.set(id + ".Creator", rs.getString(7));
                    listingsFile.set(id + ".Start", rs.getLong(8));

                    lt.getAndIncrement();

                    PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getListingsTable() + " WHERE ID=?");
                    delete.setString(1, id);
                    delete.executeUpdate();
                    delete.closeOnCompletion();

                }
                fm.saveFile(listingsFile, "/database/listings");
                chat.log("Transferred " + lt + " active listings from MySQL.", false);
                chat.alert("Transferred " + lt + " active listings from MySQL.");
                chat.alert("Now loading expired listings...");
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
                    expiredFile.set(id + ".Starting Bid", rs.getDouble(4));
                    expiredFile.set(id + ".Minimum Increment", rs.getDouble(5));
                    expiredFile.set(id + ".Creator", rs.getString(6));
                    expiredFile.set(id + ".Start", rs.getLong(7));
                    expiredFile.set(id + ".End", rs.getLong(8));
                    expiredFile.set(id + ".Reason", rs.getString(9));
                    expiredFile.set(id + ".Reclaimed", rs.getBoolean(10));

                    et.getAndIncrement();

                    PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getExpiredTable() + " WHERE ID=?");
                    delete.setString(1, id);
                    delete.executeUpdate();
                    delete.closeOnCompletion();

                }
                fm.saveFile(expiredFile, "/database/expired");
                chat.log("Transferred " + et + " expired listings from MySQL.", false);
                chat.alert("Transferred " + et + " expired listings from MySQL.");
                chat.alert("Now transferring completed listings...");
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
                    completedFile.set(id + ".Starting Bid", rs.getDouble(4));
                    completedFile.set(id + ".Minimum Increment", rs.getDouble(5));
                    completedFile.set(id + ".Creator", rs.getString(6));
                    completedFile.set(id + ".Start", rs.getLong(7));
                    completedFile.set(id + ".End", rs.getLong(8));
                    completedFile.set(id + ".Buyer", rs.getString(9));
                    completedFile.set(id + ".Reclaimed", rs.getBoolean(10));

                    ct.getAndIncrement();

                    PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getCompletedTable() + " WHERE ID=?");
                    delete.setString(1, id);
                    delete.executeUpdate();
                    delete.closeOnCompletion();

                }
                fm.saveFile(completedFile, "/database/completed");
                chat.alert("Transferred " + ct + " completed listings from MySQL.");
                chat.alert("Now transferring user profiles...");
                statement.closeOnCompletion();

            } catch (Exception e) {
                e.printStackTrace();
                chat.alert("Error while transferring completed listings.");
            }

            try {
                PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getUsersTable());

                ResultSet rs = statement.executeQuery();

                while (rs.next()) {

                    UUID uuid = UUID.fromString(rs.getString(1));
                    usersFile.set(uuid + ".Username", rs.getString(2));
                    usersFile.set(uuid + ".Alert Create Listings", rs.getBoolean(3));
                    usersFile.set(uuid + ".Open Admin Mode", rs.getBoolean(4));
                    usersFile.set(uuid + ".Alert Near Expire.Status", rs.getBoolean(5));
                    usersFile.set(uuid + ".Alert Near Expire.Time", rs.getLong(6));
                    usersFile.set(uuid + ".Alert Listing Bought", rs.getBoolean(7));
                    usersFile.set(uuid + ".Auto Confirm Listing", rs.getBoolean(8));
                    usersFile.set(uuid + ".Sounds", rs.getBoolean(9));

                    ut.getAndIncrement();
                    PreparedStatement delete = mySQL.getConnection().prepareStatement("DELETE FROM " + mySQL.getUsersTable() + " WHERE ID=?");
                    delete.setString(1, uuid.toString());
                    delete.executeUpdate();
                    delete.closeOnCompletion();
                }

                fm.saveFile(usersFile, "/database/users");
                chat.alert("Transferred " + ut + " users from MySQL.");
                statement.closeOnCompletion();

            } catch (Exception e) {
                e.printStackTrace();
                chat.alert("Error while transferring users.");
            }

            AuctionHouse.getInstance().getConfigFile().setDatabaseType(DatabaseType.FILE);
            databaseType = DatabaseType.FILE;
            chat.alert("Transfer complete. Transferred " + (et.get() + lt.get() + ct.get()) + " total listings. (" + (System.currentTimeMillis() - startTime) + "ms)");
            loadListings();
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (player != null)
                        player.openInventory(inv.transferComplete(lt.get(), ct.get(), et.get(), ut.get()));
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
        Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            //Transfer listings and users
            int activeTransferred = mySQL.transferActiveListingsFromFileToMySQL();
            chat.alert("Now transferring expired listings...");
            int expiredTransferred = mySQL.transferExpiredFromFileToMySQL();
            chat.alert("Now transferring completed listings...");
            int completedTransferred = mySQL.transferCompletedFromFileToMySQL();
            chat.alert("Now transferring user profiles...");
            int usersTransferred = mySQL.transferUsersFromFileToMySQL();

            //Set the database type to MySQL
            AuctionHouse.getInstance().getConfigFile().setDatabaseType(DatabaseType.MYSQL);
            databaseType = DatabaseType.MYSQL;

            //Reload listings
            loadListings();

            chat.alert("Transfer complete. Transferred " + (expiredTransferred + activeTransferred + completedTransferred) + " total listings and " + usersTransferred + " users. (" + (System.currentTimeMillis() - startTime) + "ms).");
            Bukkit.getScheduler().runTask(AuctionHouse.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (player != null)
                        player.openInventory(inv.transferComplete(activeTransferred, completedTransferred, expiredTransferred, usersTransferred));
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
                chat.sendMessage(creator, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_LISTINGREMOVED, "%item%;" + chat.formatItem(listing.getItemStack())));
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
        AuctionHouse.getInstance().getEcon().depositPlayer(Bukkit.getOfflinePlayer(listing.getBuyer()), listing.getBuyNowPrice());
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

    public ArrayList<Listing> getActive(UUID uuid) {
        ArrayList<Listing> personal = new ArrayList<>();
        for (Listing listing : active) {
            if (listing.getCreator().toString().equalsIgnoreCase(uuid.toString())) personal.add(listing);
        }
        return personal;
    }

    public ArrayList<Listing> getExpired(UUID uuid) {
        ArrayList<Listing> personal = new ArrayList<>();
        for (Listing listing : expired) {
            if (listing.getCreator().toString().equalsIgnoreCase(uuid.toString())) personal.add(listing);
        }
        return personal;
    }

    public ArrayList<Listing> getCompleted(UUID uuid) {
        ArrayList<Listing> personal = new ArrayList<>();
        for (Listing listing : completed) {
            if (listing.getCreator().toString().equalsIgnoreCase(uuid.toString())) personal.add(listing);
        }
        return personal;
    }

    public ArrayList<Listing> getUnclaimedCompleted(UUID uuid) {
        ArrayList<Listing> unclaimed = new ArrayList<>();
        for (Listing listing : completed) {
            if (listing.getBuyer().toString().equalsIgnoreCase(uuid.toString()) && !listing.isReclaimed())
                unclaimed.add(listing);
        }
        return unclaimed;
    }

    /**
     * @param listing   Listing to update
     * @param newBidder UUID of new bidder
     * @param newBid    Double of new bid
     * @return -2: MySQL Error, -1: Listing not active,
     */
    public int bid(Listing listing, UUID newBidder, Double newBid) {

        if (!active.contains(listing)) return -1;

        Player creator = Bukkit.getPlayer(listing.getCreator());

        //TODO notify creator of bid

        if (AuctionHouse.getInstance().getEcon().getBalance(Bukkit.getPlayer(newBidder)) < newBid) return 0;

        //TODO Create option for enabling/disabling anti-snipe
        if ((listing.getEnd() - System.currentTimeMillis()) <= 10000) {
            chat.alert("Anti-snipe, added 10 seconds");
            listing.setEnd(listing.getEnd() + 10000);
        }

        listing.newCurrentBid(newBidder, newBid);

        switch (databaseType) {
            case MYSQL:
                AtomicInteger ret = new AtomicInteger(-2);
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getListingsTable() + " SET BIDDER=?,UPDATED=? WHERE ID=?");

                            statement.setString(1, listing.formatBidders());
                            statement.setLong(2, System.currentTimeMillis());
                            statement.setString(3, listing.getId().toString());

                            statement.executeUpdate();
                            statement.closeOnCompletion();

                            ret.set(0);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return ret.get();
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");

                listingsFile.set(listing.getId() + ".Bidders", listing.formatBidders());
                fm.saveFile(listingsFile, "/database/listings");

                chat.log("New bid from " + newBidder + " for $" + newBid + " on listing " + listing.getId(), AuctionHouse.getInstance().isDebug());
                break;
        }
        return 0;
    }

    /**
     * Create a new Listing
     *
     * @param creator          Creator of the Auction
     * @param encoded          Listing item
     * @param buyNowPrice      Optional: Listing Buy Now price
     * @param startingBid      Optional: Listing starting bid
     * @param minimumIncrement Optional: Minimum bid increment
     * @return New Listing object
     */
    public Listing create(UUID creator, String encoded, double buyNowPrice, double startingBid, double minimumIncrement) {
        Player p = Bukkit.getPlayer(creator);

        //Take out the listing fee if there is no starting bid
        if (startingBid == 0)
            AuctionHouse.getInstance().getEcon().withdrawPlayer(Bukkit.getOfflinePlayer(creator), AuctionHouse.getInstance().getConfigFile().calculateListingFee(buyNowPrice));

        UUID id = UUID.randomUUID();
        long start = System.currentTimeMillis();

        //Create our new listing
        Listing listing;
        if (startingBid == 0)
            listing = new Listing(id, creator, AuctionHouse.getInstance().decode(encoded), buyNowPrice, start);
        else
            listing = new Listing(id, creator, AuctionHouse.getInstance().decode(encoded), buyNowPrice, startingBid, minimumIncrement, start);


        chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_LISTINGCREATED, "%item%;" + chat.formatItem(listing.getItemStack()), "%price%;" + chat.formatMoney(listing.getBuyNowPrice())));

        switch (databaseType) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getListingsTable() + " (ID,ITEM_STACK,PRICE,STARTING_BID,MINIMUM_INCREMENT,BIDDER,CREATOR,START,END,BUYER,UPDATED) VALUES (?,?,?,?,?,?,?,?,?,?,?)");


                        statement.setString(1, id.toString());
                        statement.setString(2, encoded);
                        statement.setDouble(3, buyNowPrice);
                        statement.setDouble(4, startingBid);
                        statement.setDouble(5, minimumIncrement);
                        statement.setString(6, null);
                        statement.setString(7, creator.toString());
                        statement.setLong(8, start);
                        statement.setLong(9, 0);
                        statement.setString(10, null);
                        statement.setLong(11, start);

                        active.add(listing);

                        statement.executeUpdate();

                        statement.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                chat.log(p.getUniqueId() + " created listing " + chat.formatItem(listing.getItemStack()) + " " + id, AuctionHouse.getInstance().isDebug());

                AuctionHouse.getInstance().getCooldownManager().setCooldown(p);

                Bukkit.getServer().getPluginManager().callEvent(new ListingCreateEvent(listing));

                return listing;
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                active.add(listing);

                listingsFile.set(id + ".ItemStack", encoded);
                listingsFile.set(id + ".Price", buyNowPrice);
                listingsFile.set(id + ".Creator", creator.toString());
                listingsFile.set(id + ".Start", start);
                listingsFile.set(id + ".Starting Bid", startingBid);
                listingsFile.set(id + ".Minimum Increment", minimumIncrement);

                fm.saveFile(listingsFile, "/database/listings");

                chat.log(p.getUniqueId() + " created listing " + chat.formatItem(listing.getItemStack()) + " " + id, AuctionHouse.getInstance().isDebug());

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

        double price = listing.getBuyNowPrice() + AuctionHouse.getInstance().getConfigFile().calculateListingTax(Objects.requireNonNull(buyer.getPlayer()), listing.getBuyNowPrice());

        if (AuctionHouse.getInstance().getEcon().getBalance(buyer) < price) return 0;

        AuctionHouse.getInstance().getEcon().withdrawPlayer(buyer, price);
        AuctionHouse.getInstance().getEcon().depositPlayer(Bukkit.getOfflinePlayer(listing.getCreator()), listing.getBuyNowPrice());

        boolean claimed = InventoryHandler.canCarryItem(buyer.getPlayer(), listing.getItemStack(), true);

        long end = System.currentTimeMillis();

        AtomicBoolean ret = new AtomicBoolean(false);

        switch (databaseType) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        remove(listing);

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getCompletedTable() + " (ID,ITEM_STACK,PRICE,CREATOR,START,END,BUYER,CLAIMED) VALUES (?,?,?,?,?,?,?,?)");

                        statement.setString(1, listing.getId().toString());
                        statement.setString(2, AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                        statement.setDouble(3, listing.getBuyNowPrice());
                        statement.setString(4, listing.getCreator().toString());
                        statement.setLong(5, listing.getStart());
                        statement.setLong(6, end);
                        statement.setString(7, buyer.getUniqueId().toString());
                        statement.setBoolean(8, claimed);

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
                completedFile.set(listing.getId().toString() + ".Price", listing.getBuyNowPrice());
                completedFile.set(listing.getId().toString() + ".Creator", listing.getCreator().toString());
                completedFile.set(listing.getId().toString() + ".Start", listing.getStart());
                completedFile.set(listing.getId().toString() + ".End", end);
                completedFile.set(listing.getId().toString() + ".Buyer", buyer.getUniqueId().toString());
                completedFile.set(listing.getId().toString() + ".Reclaimed", claimed);

                fm.saveFile(completedFile, "/database/completed");
                break;
        }

        listing.setComplete(buyer.getUniqueId(), end);

        if (claimed) InventoryHandler.addItem(buyer, listing.getItemStack());
        else {
            chat.sendMessage(buyer, "&cYou do not have enough space in your inventory. Open the Reclaim GUI to receive it.");
            listing.setReclaimed(false);
        }

        chat.sendMessage(buyer, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_BOUGHTBUYER, "%item%;" + chat.formatItem(listing.getItemStack()), "%price%;" + chat.formatMoney(price)));

        Bukkit.getServer().getPluginManager().callEvent(new ListingBoughtEvent(listing));
        chat.log("Auction " + listing.getId().toString() + " has been bought by " + listing.getBuyer().toString() + " for " + listing.getBuyNowPrice() + ".", AuctionHouse.getInstance().isDebug());
        if (creator != null) {
            chat.sendMessage(creator, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_BOUGHTCREATOR, "%item%;" + chat.formatItem(listing.getItemStack()), "%price%;" + chat.formatMoney(listing.getBuyNowPrice()), "%buyer%;" + buyer.getName()));
            if (AuctionHouse.getInstance().getUserManager().getUser(creator) != null)
                if (AuctionHouse.getInstance().getUserManager().getUser(creator).getUserSettings().isSounds())
                    creator.playSound(creator.getLocation(), AuctionHouse.getInstance().getConfigFile().getListingBoughtSound(), 1, 1);
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

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getListingsTable() + " SET PRICE=?,UPDATED=? WHERE ID=?");

                        statement.setDouble(1, newPrice);
                        statement.setLong(2, System.currentTimeMillis());
                        statement.setString(3, listing.getId().toString());

                        statement.executeUpdate();
                        statement.closeOnCompletion();

                        listing.setBuyNowPrice(newPrice);

                        ret.set(1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return ret.get();
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                listingsFile.set(listing.getId() + ".Price", newPrice);
                listing.setBuyNowPrice(newPrice);
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
     * @return 4: Listing not active -3: MySQL Error -2: Player can't carry returned items -1: Player does not have enough items to add 0: Through no change 1: Listing amount set
     */
    public int setAmount(Listing listing, int newAmount, Player player, boolean ignoreCheck) {

        if (!listing.isActive()) {
            return -4;
        }

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

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getListingsTable() + " SET ITEM_STACK=?,UPDATED=? WHERE ID=?");

                        listing.getItemStack().setAmount(newAmount);

                        statement.setString(1, AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                        statement.setLong(2, System.currentTimeMillis());
                        statement.setString(3, listing.getId().toString());

                        statement.executeUpdate();
                        statement.closeOnCompletion();

                        ret.set(1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return ret.get();
            case FILE:
                YamlConfiguration listingsFile = fm.getConfig("/database/listings");
                listing.getItemStack().setAmount(newAmount);
                listingsFile.set(listing.getId().toString() + ".ItemStack", AuctionHouse.getInstance().encode(listing.getItemStack(), false));
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
     * @param listing    Listing set to expire.
     * @param notify     Notify the creator that the listing expired.
     * @param ignoreTime Ignore when the listing is supposed to expire.
     * @param reason     Player UUID String, CONSOLE, or other reason.
     * @return -3: Already expired -2: MySQL Error -1: Not ready to expire 0: Through no change 1: Stored in Database
     * @apiNote Used to remove a listing from the AuctionHouse.
     */
    public int expire(Listing listing, boolean notify, boolean ignoreTime, String reason) {

        long now = System.currentTimeMillis() / 1000;
        long end = (listing.getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) / 1000;

        if (!(now > end) && !ignoreTime) return -1;
        if (listing.getEnd() != 0) return -3;

        Player creator = Bukkit.getPlayer(listing.getCreator());

        switch (databaseType) {
            case MYSQL:
                AtomicInteger ret = new AtomicInteger(-2);
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {

                        PreparedStatement preCheck = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getExpiredTable() + " WHERE ID=?");
                        preCheck.setString(1, listing.getId().toString());
                        ResultSet rs = preCheck.executeQuery();

                        if (rs.next()) {
                            ret.set(-3);
                            if (!rs.getBoolean(8)) {
                                unclaimed.add(listing);
                                active.remove(listing);
                                expired.add(listing);
                            }
                            return;
                        }

                        remove(listing);

                        long newEnd = System.currentTimeMillis();
                        listing.setExpired(newEnd, false);

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getExpiredTable() + " (ID,ITEM_STACK,PRICE,STARTING_BID,MINIMUM_INCREMENT,CREATOR,START,END,REASON,RECLAIMED) VALUES (?,?,?,?,?,?,?,?,?,?)");

                        statement.setString(1, listing.getId().toString());
                        statement.setString(2, AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                        statement.setDouble(3, listing.getBuyNowPrice());
                        statement.setDouble(4, listing.getStartingBid());
                        statement.setDouble(5, listing.getMinimumIncrement());
                        statement.setString(6, listing.getCreator().toString());
                        statement.setLong(7, listing.getStart());
                        statement.setLong(8, listing.getEnd());
                        statement.setString(9, reason);
                        statement.setBoolean(10, false);

                        statement.executeUpdate();
                        statement.close();
                        ret.set(1);

                        if (creator != null) {
                            if (notify)
                                chat.sendMessage(creator, "&fYour listing for &e" + chat.formatItem(listing.getItemStack()) + "&f has expired.");
                            if (AuctionHouse.getInstance().getGuiManager().getGui().containsKey(creator.getUniqueId().toString())) {
                                AkarianInventory inv = AuctionHouse.getInstance().getGuiManager().getGui().get(creator.getUniqueId().toString());
                                if (inv instanceof ExpireReclaimGUI) {
                                    ((ExpireReclaimGUI) inv).setUpdate(true);
                                    inv.updateInventory();
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return ret.get();
            case FILE:
                long newEnd = System.currentTimeMillis();
                YamlConfiguration expiredFile = fm.getConfig("/database/expired");
                expiredFile.set(listing.getId().toString() + ".ItemStack", AuctionHouse.getInstance().encode(listing.getItemStack(), false));
                expiredFile.set(listing.getId().toString() + ".Price", listing.getBuyNowPrice());
                expiredFile.set(listing.getId().toString() + ".Creator", listing.getCreator().toString());
                expiredFile.set(listing.getId().toString() + ".Start", listing.getStart());
                expiredFile.set(listing.getId().toString() + ".End", listing.getEnd());
                expiredFile.set(listing.getId().toString() + ".Reason", reason);
                expiredFile.set(listing.getId().toString() + ".Reclaimed", false);

                listing.setExpired(newEnd, false);

                fm.saveFile(expiredFile, "/database/expired");
                remove(listing);

                if (creator != null) {
                    if (notify)
                        chat.sendMessage(creator, "&fYour listing for &e" + chat.formatItem(listing.getItemStack()) + "&f has expired.");
                    if (AuctionHouse.getInstance().getGuiManager().getGui().containsKey(creator.getUniqueId().toString())) {
                        AkarianInventory inv = AuctionHouse.getInstance().getGuiManager().getGui().get(creator.getUniqueId().toString());
                        if (inv instanceof ExpireReclaimGUI) {
                            ((ExpireReclaimGUI) inv).setUpdate(true);
                            inv.updateInventory();
                        }
                    }
                }

                return 1;
        }
        return 0;
    }

    /**
     * Load all active listings
     */
    public void loadListings() {
        active.clear();
        chat.log("Loading Active listings...", AuctionHouse.getInstance().isDebug());
        AtomicInteger num = new AtomicInteger();
        AtomicInteger errors = new AtomicInteger();
        switch (databaseType) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getListingsTable());
                        ResultSet rs = statement.executeQuery();

                        while (rs.next()) {

                            UUID id = UUID.fromString(rs.getString(1));
                            ItemStack item = AuctionHouse.getInstance().decode(rs.getString(2));
                            double price = rs.getDouble(3);
                            UUID creator = UUID.fromString(rs.getString(7));
                            long start = rs.getLong(8);

                            Listing l = new Listing(id, creator, item, price, start);

                            active.add(l);
                            num.getAndIncrement();

                            chat.log("Loaded listing " + chat.formatItem(l.getItemStack()) + " id=" + l.getId().toString(), AuctionHouse.getInstance().isDebug());

                        }

                        chat.log("Loaded " + num.get() + " active listings.", AuctionHouse.getInstance().isDebug());
                        mysqlSyncTime = System.currentTimeMillis();
                        startMySQLSync();

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
                    if (listingsFile.getString(str + ".ItemStack") == null) {
                        chat.log("Error while loading auction with ID " + id + ". Skipping...", true);
                        errors.incrementAndGet();
                        continue;
                    }
                    ItemStack item = AuctionHouse.getInstance().decode(Objects.requireNonNull(listingsFile.getString(str + ".ItemStack")));
                    if (item == null || item.getType() == Material.AIR) {
                        chat.log("Error while loading auction with ID " + id + ". Skipping...", true);
                        errors.incrementAndGet();
                        continue;
                    }
                    if (!listingsFile.contains(str + ".Price")) {
                        chat.log("Error while loading auction with price " + id + ". Skipping...", true);
                        errors.incrementAndGet();
                        continue;
                    }
                    double price = listingsFile.getDouble(str + ".Price");
                    if (!listingsFile.contains(str + ".Creator")) {
                        chat.log("Error while loading auction with creator " + id + ". Skipping...", true);
                        errors.incrementAndGet();
                        continue;
                    }
                    UUID creator = UUID.fromString(Objects.requireNonNull(listingsFile.getString(str + ".Creator")));
                    if (!listingsFile.contains(str + ".Start")) {
                        chat.log("Error while loading auction with Start " + id + ". Skipping...", true);
                        errors.incrementAndGet();
                        continue;
                    }
                    long start = listingsFile.getLong(str + ".Start");
                    Listing l = new Listing(id, creator, item, price, start);

                    if (listingsFile.contains(str + ".Bidder")) {
                        l.setBidders(Objects.requireNonNull(listingsFile.getString(str + ".Bidder")));
                    }

                    long now = System.currentTimeMillis() / 1000;
                    long end = (l.getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) / 1000;

                    if (now > end) {
                        switch (expire(l, true, false, "TIME")) {
                            case -1:
                                chat.log("!! Error while saving " + chat.formatItem(item) + ".", AuctionHouse.getInstance().isDebug());
                                break;
                            case 1:
                            case 2:
                                chat.log("Listing " + chat.formatItem(item) + " has expired. Item saved in database.", AuctionHouse.getInstance().isDebug());
                                continue;
                        }
                    }

                    active.add(l);
                    num.getAndIncrement();

                    chat.log("Loaded listing " + chat.formatItem(l.getItemStack()) + " id=" + l.getId().toString(), AuctionHouse.getInstance().isDebug());
                }
                chat.log("Loaded " + num.get() + " active listings.", AuctionHouse.getInstance().isDebug());
                break;
        }
        startExpireCheck();
        startAuctionHouseRefresh();
        if (errors.get() > 0) {
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
                            double startingBid = rs.getDouble(4);
                            double minIncrement = rs.getDouble(5);
                            UUID creator = UUID.fromString(rs.getString(6));
                            long start = rs.getLong(7);
                            long end = rs.getLong(8);
                            String reason = rs.getString(9);
                            boolean reclaimed = rs.getBoolean(10);

                            Listing l = new Listing(id, creator, item, price, start);

                            l.setEnd(end);
                            l.setEnd(end);
                            l.setEndReason(reason);
                            l.setReclaimed(reclaimed);
                            l.setExpired(true);
                            l.setStartingBid(startingBid);
                            l.setMinimumIncrement(minIncrement);

                            if (!reclaimed) unclaimed.add(l);
                            expired.add(l);

                            chat.log("Loaded expired listing " + chat.formatItem(l.getItemStack()) + " id=" + l.getId().toString(), AuctionHouse.getInstance().isDebug());

                        }
                        chat.log("Loaded " + expired.size() + " expired listings, " + unclaimed.size() + " of which are unclaimed.", AuctionHouse.getInstance().isDebug());
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
                    if (listingsFile.getString(str + ".ItemStack") == null) {
                        chat.log("Error while loading auction with ID " + id + ". Skipping...", true);
                        errors.incrementAndGet();
                        continue;
                    }
                    ItemStack item = AuctionHouse.getInstance().decode(Objects.requireNonNull(listingsFile.getString(str + ".ItemStack")));
                    if (item == null || item.getType() == Material.AIR) {
                        chat.log("Error while loading auction with ID " + id + ". Skipping...", true);
                        errors.incrementAndGet();
                        continue;
                    }
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
                    l.setExpired(true);

                    if (!reclaimed) unclaimed.add(l);
                    expired.add(l);

                    chat.log("Loaded expired listing " + chat.formatItem(l.getItemStack()) + " id=" + l.getId().toString(), AuctionHouse.getInstance().isDebug());

                }
                chat.log("Loaded " + expired.size() + " expired listings, " + unclaimed.size() + " of which are unclaimed.", AuctionHouse.getInstance().isDebug());
                if (errors.get() > 0) {
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
                            boolean reclaimed = rs.getBoolean(8);

                            Listing l = new Listing(id, creator, item, price, start);

                            l.setEnd(end);
                            l.setBuyer(buyer);
                            l.setReclaimed(reclaimed);
                            l.setCompleted(true);

                            completed.add(l);

                            chat.log("Loaded completed listing " + chat.formatItem(l.getItemStack()) + " id=" + l.getId().toString() + " reclaimed=" + reclaimed, AuctionHouse.getInstance().isDebug());

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
                    if (completedFile.getString(str + ".ItemStack") == null) {
                        chat.log("Error while loading auction with ID " + id + ". Skipping...", AuctionHouse.getInstance().isDebug());
                        errors.incrementAndGet();
                        continue;
                    }
                    ItemStack item = AuctionHouse.getInstance().decode(Objects.requireNonNull(completedFile.getString(str + ".ItemStack")));
                    double price = completedFile.getDouble(str + ".Price");
                    UUID creator = UUID.fromString(Objects.requireNonNull(completedFile.getString(str + ".Creator")));
                    long start = completedFile.getLong(str + ".Start");
                    long end = completedFile.getLong(str + ".End");
                    UUID buyer = UUID.fromString(Objects.requireNonNull(completedFile.getString(str + ".Buyer")));
                    //Version 1.3.0_Pre-4 Adding Reclaimed to completed listings, listings already claimed.
                    if (!completedFile.isBoolean(str + ".Reclaimed")) {
                        completedFile.set(str + ".Reclaimed", true);
                    }
                    boolean reclaimed = completedFile.getBoolean(str + ".Reclaimed");

                    Listing l = new Listing(id, creator, item, price, start);

                    l.setEnd(end);
                    l.setBuyer(buyer);
                    l.setReclaimed(reclaimed);
                    l.setCompleted(true);

                    completed.add(l);

                    chat.log("Loaded completed listing " + chat.formatItem(l.getItemStack()) + " id=" + l.getId().toString() + " reclaimed=" + reclaimed, AuctionHouse.getInstance().isDebug());

                }
                chat.log("Loaded " + completed.size() + " completed listings.", AuctionHouse.getInstance().isDebug());
                if (errors.get() > 0) {
                    AuctionHouse.getInstance().getLogger().log(Level.SEVERE, "There was an error loading " + errors.get() + " completed auctions. Please review console to see which.");
                    chat.log("There was an error loading " + errors.get() + " completed auctions. Please review console to see which.", AuctionHouse.getInstance().isDebug());
                }
                fm.saveFile(completedFile, "/database/completed");
                break;
        }
    }

    public int reclaimCompleted(Listing listing, Player player, boolean returnItem) {
        AtomicInteger returnVal = new AtomicInteger(0);
        if (listing.isReclaimed()) return -2;
        if (returnItem) {
            final ItemStack[] itemStack = {null};
            switch (databaseType) {
                case MYSQL:
                    Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {

                        try {

                            PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getCompletedTable() + " WHERE ID=?");

                            statement.setString(1, listing.getId().toString());

                            ResultSet rs = statement.executeQuery();

                            while (rs.next()) {
                                if (rs.getBoolean(8)) {
                                    listing.setReclaimed(true);
                                    unclaimed.remove(listing);
                                    returnVal.set(-2);
                                } else {
                                    itemStack[0] = AuctionHouse.getInstance().decode(rs.getString(2));
                                    assert itemStack[0] != null;
                                    if (!InventoryHandler.canCarryItem(player, itemStack[0], true)) {
                                        chat.sendMessage(player, "&cYou do not have enough space in your inventory to hold this item.");
                                        returnVal.set(-1);
                                    } else {
                                        InventoryHandler.addItem(player, itemStack[0]);
                                        chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_EXPIREDRECLAIMED, "%item%;" + chat.formatItem(listing.getItemStack())));
                                    }
                                }
                            }

                            if (returnVal.get() == 0) {
                                PreparedStatement update = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getCompletedTable() + " SET CLAIMED=? WHERE ID=?");

                                update.setBoolean(1, true);
                                update.setString(2, listing.getId().toString());

                                update.executeUpdate();
                                update.closeOnCompletion();
                                unclaimed.remove(listing);
                                listing.setReclaimed(true);
                                returnVal.set(1);
                            }

                            statement.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    if (returnVal.get() == -1 || returnVal.get() == -2) return returnVal.get();
                    break;
                case FILE:
                    YamlConfiguration completedFile = fm.getConfig("/database/completed");
                    if (completedFile.getBoolean(listing.getId() + ".Reclaimed")) {
                        listing.setReclaimed(true);
                        return -2;
                    }
                    itemStack[0] = AuctionHouse.getInstance().decode(Objects.requireNonNull(completedFile.getString(listing.getId() + ".ItemStack")));
                    assert itemStack[0] != null;
                    if (!InventoryHandler.canCarryItem(player, itemStack[0], true)) {
                        chat.sendMessage(player, "&cYou do not have enough space in your inventory to hold this item.");
                        return -1;
                    }
                    InventoryHandler.addItem(player, itemStack[0]);
                    completedFile.set(listing.getId().toString() + ".Reclaimed", true);
                    fm.saveFile(completedFile, "/database/completed");
                    listing.setReclaimed(true);
                    chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_EXPIREDRECLAIMED, "%item%;" + chat.formatItem(listing.getItemStack())));
                    return 1;
            }
        }
        return returnVal.get();
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
                                if (rs.getBoolean(8)) {
                                    listing.setReclaimed(true);
                                    unclaimed.remove(listing);
                                    ret.set(-2);
                                } else {
                                    itemStack[0] = AuctionHouse.getInstance().decode(rs.getString(2));
                                    assert itemStack[0] != null;
                                    if (!InventoryHandler.canCarryItem(player, itemStack[0], true)) {
                                        chat.sendMessage(player, "&cYou do not have enough space in your inventory to hold this item.");
                                        ret.set(-1);
                                    } else {
                                        InventoryHandler.addItem(player, itemStack[0]);
                                        chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_EXPIREDRECLAIMED, "%item%;" + chat.formatItem(listing.getItemStack())));
                                    }
                                }
                            }

                            if (ret.get() == 0) {
                                PreparedStatement update = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getExpiredTable() + " SET RECLAIMED=? WHERE ID=?");

                                update.setBoolean(1, true);
                                update.setString(2, listing.getId().toString());

                                update.executeUpdate();
                                update.closeOnCompletion();
                                unclaimed.remove(listing);
                                listing.setReclaimed(true);
                                ret.set(1);
                            }

                            statement.closeOnCompletion();
                        } catch (Exception e) {
                            e.printStackTrace();
                            ret.set(3);
                        }
                    });
                    if (ret.get() == -1 || ret.get() == -2) return ret.get();
                    break;
                case FILE:
                    YamlConfiguration expiredFile = fm.getConfig("/database/expired");
                    if (expiredFile.getBoolean(listing.getId() + ".Reclaimed")) {
                        unclaimed.remove(listing);
                        listing.setReclaimed(true);
                        return -2;
                    }
                    itemStack[0] = AuctionHouse.getInstance().decode(Objects.requireNonNull(expiredFile.getString(listing.getId() + ".ItemStack")));
                    assert itemStack[0] != null;
                    if (!InventoryHandler.canCarryItem(player, itemStack[0], true)) {
                        chat.sendMessage(player, "&cYou do not have enough space in your inventory to hold this item.");
                        return -1;
                    }
                    InventoryHandler.addItem(player, itemStack[0]);

                    expiredFile.set(listing.getId().toString() + ".Reclaimed", true);
                    fm.saveFile(expiredFile, "/database/expired");
                    unclaimed.remove(listing);
                    listing.setReclaimed(true);
                    chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_GEN_EXPIREDRECLAIMED, "%item%;" + chat.formatItem(listing.getItemStack())));
                    return 1;

            }
        }
        return ret.get();
    }

    /**
     * Get a List of unclaimed Expired Listings
     *
     * @param uuid UUID of player
     * @return List of ItemStack
     */
    public ArrayList<Listing> getUnclaimedExpired(UUID uuid) {
        ArrayList<Listing> a = new ArrayList<>();

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
    public void startExpireCheck() {

        expireTimer = Bukkit.getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), () -> {
            int expired = 0;
            if (!active.isEmpty()) {
                List<Listing> copy = new ArrayList<>(active);
                for (Listing listing : copy) {
                    long now = System.currentTimeMillis() / 1000;
                    long end = (listing.getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) / 1000;

                    ItemStack item = listing.getItemStack();

                    //Alert near expire
                    for (User user : AuctionHouse.getInstance().getUserManager().getUsers()) {
                        if (!user.getUserSettings().isAlertNearExpire()) continue;
                        if (!user.getUserSettings().getNotified().contains(listing) && end - now < user.getUserSettings().getAlertNearExpireTime() && Bukkit.getPlayer(user.getUuid()) != null) {
                            user.getUserSettings().getNotified().add(listing);
                            chat.sendMessage(Objects.requireNonNull(Bukkit.getPlayer(user.getUuid())), AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SETTINGS_EXPIRATION_MESSAGE, "%listing%;" + chat.formatItem(listing.getItemStack()), "%time%;" + chat.formatTime(end - now), "%seller%;" + Objects.requireNonNull(Bukkit.getOfflinePlayer(listing.getCreator()).getName()), "%papi%;" + user.getUuid().toString()));
                        }
                    }
                    if (now > end) {
                        switch (expire(listing, true, false, "TIME")) {
                            case -1:
                                chat.log("!! Error while saving " + chat.formatItem(item) + ".", AuctionHouse.getInstance().isDebug());
                                expired++;
                                break;
                            case 1:
                                chat.log("Listing " + chat.formatItem(item) + " has expired with user online.", AuctionHouse.getInstance().isDebug());
                                expired++;
                                break;
                            case 2:
                                chat.log("Listing " + chat.formatItem(item) + " has expired. Item saved in database.", AuctionHouse.getInstance().isDebug());
                                expired++;
                                break;
                        }
                    }
                }
            }
        }, 0, 20);
    }

    public void startMySQLSync() {
        if (AuctionHouse.getInstance().getDatabaseType() != DatabaseType.MYSQL) return;

        AtomicInteger loaded = new AtomicInteger();
        AtomicInteger complete = new AtomicInteger();
        AtomicInteger expire = new AtomicInteger();

        mysqlSyncTask = Bukkit.getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), () -> {
            long newSync = System.currentTimeMillis();
            loaded.set(0);
            complete.set(0);
            expire.set(0);

            if (AuctionHouse.getInstance().getDatabaseType() != DatabaseType.MYSQL)
                Bukkit.getScheduler().cancelTask(mysqlSyncTask.getTaskId());

            try {
                if (mySQL.getConnection().isClosed()) {
                    Bukkit.getScheduler().cancelTask(mysqlSyncTask.getTaskId());
                    return;
                }
                PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getListingsTable() + " WHERE UPDATED>=?");
                statement.setLong(1, mysqlSyncTime);
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {

                    UUID id = UUID.fromString(rs.getString(1));
                    ItemStack item = AuctionHouse.getInstance().decode(rs.getString(2));
                    double price = rs.getDouble(3);
                    UUID creator = UUID.fromString(rs.getString(6));
                    long start = rs.getLong(7);

                    Listing l;

                    if (get(id) == null) {
                        l = new Listing(id, creator, item, price, start);
                        active.add(l);
                    } else {
                        l = get(id);
                        l.setBuyNowPrice(price);
                        l.setItemStack(item);
                    }

                    loaded.incrementAndGet();
                    chat.log("Synced listing " + id + ". item= " + chat.formatItem(item), AuctionHouse.getInstance().isDebug());
                }

                statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getCompletedTable() + " WHERE END>?");
                statement.setLong(1, mysqlSyncTime);
                rs = statement.executeQuery();

                while (rs.next()) {
                    UUID id = UUID.fromString(rs.getString(1));
                    if (isCompleted(id)) {
                        chat.log("Tried to sync completed listing " + id + " which is already completed.", AuctionHouse.getInstance().isDebug());
                        continue;
                    }
                    long end = rs.getLong(8);
                    UUID buyer = UUID.fromString(rs.getString(7));
                    Listing listing = get(id);
                    listing.setComplete(buyer, end);
                    complete.incrementAndGet();
                    chat.log("Synced bought listing " + id + " item= " + chat.formatItem(listing.getItemStack()) + " buyer= " + listing.getBuyer(), AuctionHouse.getInstance().isDebug());
                }

                statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getExpiredTable() + " WHERE END>?");
                statement.setLong(1, mysqlSyncTime);
                rs = statement.executeQuery();

                while (rs.next()) {
                    UUID id = UUID.fromString(rs.getString(1));
                    if (isExpired(id)) {
                        chat.log("Tried to sync expired listing " + id + " which is already expired.", AuctionHouse.getInstance().isDebug());
                        continue;
                    }
                    long end = rs.getLong(6);
                    boolean reclaimed = rs.getBoolean(8);
                    Listing listing = get(id);
                    listing.setExpired(end, reclaimed);
                    expire.incrementAndGet();
                    chat.log("Synced expired listing " + id + " item= " + chat.formatItem(listing.getItemStack()) + " reclaimed= " + reclaimed, AuctionHouse.getInstance().isDebug());
                }

                if (loaded.get() != 0)
                    chat.log("Synced " + loaded.get() + " listings from MySQL.", AuctionHouse.getInstance().isDebug());
                if (complete.get() != 0)
                    chat.log("Synced " + complete.get() + " bought listings from MySQL", AuctionHouse.getInstance().isDebug());
                if (expire.get() != 0)
                    chat.log("Synced " + expire.get() + " expired listings from MySQL", AuctionHouse.getInstance().isDebug());

                mysqlSyncTime = newSync;
                statement.close();
            } catch (Exception e) {
                chat.log("There was an error while trying to sync with MySQL database", true);
                e.printStackTrace();
            }

        }, 20 * 5, 20 * 5);

    }

    public boolean isCompleted(UUID uuid) {
        for (Listing listing : completed) if (listing.getId().toString().equalsIgnoreCase(uuid.toString())) return true;
        return false;
    }

    public boolean isExpired(UUID uuid) {
        for (Listing listing : expired) if (listing.getId().toString().equalsIgnoreCase(uuid.toString())) return true;
        return false;
    }

    /**
     * Start the AuctionHouse Refresh Timer
     */
    public void startAuctionHouseRefresh() {
        refreshTimer = Bukkit.getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), this::refreshAuctionHouse, 0, 20L * AuctionHouse.getInstance().getConfigFile().getAuctionhouseRefreshTime());
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
        Bukkit.getScheduler().cancelTask(expireTimer.getTaskId());
        if (expireTimer.isCancelled()) {
            chat.log("Cancelled expire timer.", AuctionHouse.getInstance().isDebug());
        }
    }

    public void cancelRefreshTimer() {
        Bukkit.getScheduler().cancelTask(refreshTimer.getTaskId());
        if (refreshTimer.isCancelled()) {
            chat.log("Cancelled refresh timer.", AuctionHouse.getInstance().isDebug());
        }
    }
}
