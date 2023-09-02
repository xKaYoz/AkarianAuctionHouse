package net.akarian.auctionhouse.utils;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.database.transfer.DatabaseTransferStatusGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class MySQL {

    private final Plugin plugin = AuctionHouse.getInstance();
    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    @Setter
    private Connection connection;
    @Getter
    private String host, database, username, password, listingsTable, expiredTable, completedTable, usersTable;
    @Getter
    private int port;
    @Getter
    @Setter
    private boolean connected;
    @Getter
    @Setter
    private UUID transferring = null;

    public boolean setup() {
        if (!connected) {
            host = AuctionHouse.getInstance().getConfigFile().getDb_host();
            database = AuctionHouse.getInstance().getConfigFile().getDb_database();
            username = AuctionHouse.getInstance().getConfigFile().getDb_username();
            password = AuctionHouse.getInstance().getConfigFile().getDb_password();
            port = AuctionHouse.getInstance().getConfigFile().getDb_port();
            listingsTable = AuctionHouse.getInstance().getConfigFile().getDb_listings();
            expiredTable = AuctionHouse.getInstance().getConfigFile().getDb_expired();
            completedTable = AuctionHouse.getInstance().getConfigFile().getDb_completed();
            usersTable = AuctionHouse.getInstance().getConfigFile().getDb_users();

            chat.log("---------- Akarian Auction House MySQL Manager ----------", AuctionHouse.getInstance().isDebug());
            chat.log("", AuctionHouse.getInstance().isDebug());

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                chat.log("Connecting to the MySQL database...", AuctionHouse.getInstance().isDebug());
                setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", this.username, this.password));
                chat.log("", AuctionHouse.getInstance().isDebug());
                chat.log(plugin.getName() + " has successfully established a connection to the MySQL database.", AuctionHouse.getInstance().isDebug());
                chat.log("", AuctionHouse.getInstance().isDebug());
                chat.log("Checking Tables...", AuctionHouse.getInstance().isDebug());
                chat.log("", AuctionHouse.getInstance().isDebug());
                if (checkTable(listingsTable, "ID VARCHAR(50) NOT NULL PRIMARY KEY, ITEM_STACK TEXT(65535) NOT NULL, PRICE DOUBLE NOT NULL, STARTING_BID DOUBLE NOT NULL, MINIMUM_INCREMENT DOUBLE NOT NULL, BIDDERS TEXT(65535) DEFAULT NULL, CREATOR varchar(255) NOT NULL, START bigint(20) NOT NULL, END bigint(20) DEFAULT '0', BUYER varchar(255) DEFAULT NULL, UPDATED bigint(20) NOT NULL"))
                    chat.log("Listings table checked.", AuctionHouse.getInstance().isDebug());
                else chat.log("!! Listings Table Failed Check !!", true);
                if (checkTable(expiredTable, "ID varchar(50) NOT NULL PRIMARY KEY, ITEM_STACK TEXT(65535) NOT NULL, PRICE DOUBLE NOT NULL, STARTING_BID DOUBLE NOT NULL, MINIMUM_INCREMENT DOUBLE NOT NULL, CREATOR varchar(255) NOT NULL, START bigint(20) NOT NULL, END bigint(20) NOT NULL, REASON varchar(255) NOT NULL, RECLAIMED BOOLEAN DEFAULT '0'"))
                    chat.log("Expired table checked.", AuctionHouse.getInstance().isDebug());
                else chat.log("!! Expired Table Failed Check !!", true);
                if (checkTable(completedTable, "ID varchar(50) NOT NULL PRIMARY KEY, ITEM_STACK TEXT(65535) NOT NULL, PRICE DOUBLE NOT NULL, STARTING_BID DOUBLE NOT NULL, MINIMUM_INCREMENT DOUBLE NOT NULL, CREATOR varchar(255) NOT NULL, START bigint(20) NOT NULL, END bigint(20) NOT NULL, BUYER varchar(255) NOT NULL, CLAIMED BOOLEAN DEFAULT '1'"))
                    chat.log("Completed table checked.", AuctionHouse.getInstance().isDebug());
                else chat.log("!! Completed Table Failed Check !!", true);
                if (checkTable(usersTable, "ID varchar(50) NOT NULL PRIMARY KEY, USERNAME TEXT(65535) NOT NULL, ALERT_CREATE BOOLEAN DEFAULT '0', OPEN_ADMIN BOOLEAN DEFAULT '0', ALERT_NEAR_EXPIRE BOOLEAN DEFAULT '0', ALERT_NEAR_EXPIRE_TIME bigint(20) NOT NULL, LISTING_BOUGHT BOOLEAN DEFAULT '0', AUTO_CONFIRM BOOLEAN DEFAULT '0', SOUNDS BOOLEAN DEFAULT '0'"))
                    chat.log("Users table checked.", AuctionHouse.getInstance().isDebug());
                else chat.log("!! Users Table Failed Check !!", true);
                chat.log("", AuctionHouse.getInstance().isDebug());
                chat.log("Starting connection timer.", AuctionHouse.getInstance().isDebug());
                startConnectionTimer();
                chat.log("---------------------------------------------", AuctionHouse.getInstance().isDebug());
                if (transferring != null) {
                    Player p = Bukkit.getPlayer(transferring);
                    if (p == null) return connected = true;
                    Bukkit.getScheduler().runTask(plugin, () -> p.openInventory(new DatabaseTransferStatusGUI(p).getInventory()));

                    transferring = null;
                }
                return connected = true;
            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().log(Level.SEVERE, "An error has occurred while connecting to the database. Please see stacktrace above.");
                chat.log("", AuctionHouse.getInstance().isDebug());
                chat.log("---------------------------------------------", AuctionHouse.getInstance().isDebug());
                if (e.getCause() == null) {
                    if (transferring != null) {
                        Player p = Bukkit.getPlayer(transferring);
                        if (p == null) return connected = false;
                        Bukkit.getScheduler().runTask(plugin, () -> p.openInventory(new DatabaseTransferStatusGUI(Bukkit.getPlayer(transferring)).connectionDisapproved()));
                    }
                    return connected = false;
                }
                chat.alert("&c&l" + plugin.getName() + " has encountered an error connecting to the MySQL database. Please check console. E" + e.getCause().getLocalizedMessage());
                if (transferring != null) {
                    Player p = Bukkit.getPlayer(transferring);
                    if (p == null) return connected = false;
                    Bukkit.getScheduler().runTask(plugin, () -> p.openInventory(new DatabaseTransferStatusGUI(Bukkit.getPlayer(transferring)).connectionDisapproved()));
                }
                return connected = false;
            }
        }
        return true;
    }

    public boolean checkTable(String tableName, String query) {
        try {
            Statement s = connection.createStatement();
            PreparedStatement check = connection.prepareStatement("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=? AND TABLE_NAME=? AND COLUMN_NAME=?");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + query + ")");
            check.setString(1, database);

            if (tableName.equalsIgnoreCase(expiredTable)) {

                boolean priceCheck = false;
                boolean bidCheck = false;
                check.setString(2, expiredTable);

                try {
                    check.setString(3, "PRICE");

                    ResultSet rs = check.executeQuery();
                    while (rs.next()) {
                        priceCheck = true;
                    }
                    if (!priceCheck) {
                        s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN PRICE DOUBLE NOT NULL AFTER ITEM_STACK");
                        s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN START bigint(20) NOT NULL AFTER CREATOR");
                        s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN END bigint(20) NOT NULL AFTER START");
                        s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN REASON varchar(255) NOT NULL AFTER END");
                        s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN RECLAIMED BOOLEAN DEFAULT 0 AFTER REASON");
                    }

                    check.setString(3, "STARTING_BID");
                    rs = check.executeQuery();
                    while (rs.next()) {
                        bidCheck = true;
                    }
                    if (!bidCheck) {
                        s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN STARTING_BID DOUBLE NOT NULL AFTER PRICE");
                        s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN MINIMUM_INCREMENT DOUBLE NOT NULL AFTER STARTING_BID");
                        chat.log("Created bidding columns for expired table (v1.4.0)", AuctionHouse.getInstance().isDebug());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            if (tableName.equalsIgnoreCase(usersTable)) {

                boolean soundsCheck = false;
                check.setString(2, usersTable);

                try {
                    check.setString(3, "SOUNDS");
                    ResultSet rs = check.executeQuery();

                    while (rs.next()) {
                        soundsCheck = true;
                    }
                    if (!soundsCheck) {
                        s.executeUpdate("ALTER TABLE " + usersTable + " ADD COLUMN SOUNDS BOOLEAN DEFAULT 0 AFTER AUTO_CONFIRM");
                        chat.log("Created sounds column for users table (v1.2.4)", AuctionHouse.getInstance().isDebug());
                    }
                } catch (SQLException e) {
                    return false;
                }

            }
            if (tableName.equalsIgnoreCase(listingsTable)) {

                boolean syncCheck = false;
                boolean biddingCheck = false;
                check.setString(2, listingsTable);
                try {
                    check.setString(3, "UPDATED");
                    ResultSet rs = check.executeQuery();

                    while (rs.next()) {
                        syncCheck = true;
                    }
                    if (!syncCheck) {
                        s.executeUpdate("ALTER TABLE " + listingsTable + " ADD COLUMN UPDATED bigint(20) NOT NULL AFTER BUYER");
                        chat.log("Created sync column for listings table.", AuctionHouse.getInstance().isDebug());
                    }
                    check.setString(3, "STARTING_BID");
                    rs = check.executeQuery();

                    while (rs.next()) {
                        biddingCheck = true;
                    }
                    if (!biddingCheck) {
                        s.executeUpdate("ALTER TABLE " + listingsTable + " ADD COLUMN STARTING_BID DOUBLE NOT NULL AFTER PRICE");
                        s.executeUpdate("ALTER TABLE " + listingsTable + " ADD COLUMN MINIMUM_INCREMENT DOUBLE NOT NULL AFTER STARTING_BID");
                        s.executeUpdate("ALTER TABLE " + listingsTable + " ADD COLUMN BIDDER TEXT(65535) DEFAULT NULL AFTER MINIMUM_INCREMENT");
                        chat.log("Created bidding columns for listings table (v1.4.0)", AuctionHouse.getInstance().isDebug());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            if (tableName.equalsIgnoreCase(completedTable)) {
                boolean reclaimedCheck = false;
                boolean biddingCheck = false;
                check.setString(2, completedTable);
                try {
                    check.setString(3, "CLAIMED");
                    ResultSet rs = check.executeQuery();

                    while (rs.next()) {
                        reclaimedCheck = true;
                    }

                    if (!reclaimedCheck) {
                        s.executeUpdate("ALTER TABLE " + completedTable + " ADD COLUMN CLAIMED BOOLEAN DEFAULT 1 AFTER BUYER");
                        chat.log("Created claimed column for completed table", AuctionHouse.getInstance().isDebug());
                    }

                    check.setString(3, "STARTING_BID");
                    rs = check.executeQuery();

                    while (rs.next()) {
                        biddingCheck = true;
                    }

                    if (!biddingCheck) {
                        s.executeUpdate("ALTER TABLE " + completedTable + " ADD COLUMN STARTING_BID DOUBLE NOT NULL AFTER PRICE");
                        s.executeUpdate("ALTER TABLE " + completedTable + " ADD COLUMN MINIMUM_INCREMENT DOUBLE NOT NULL AFTER STARTING_BID");
                        chat.log("Created bidding columns for completed table (v1.4.0)", AuctionHouse.getInstance().isDebug());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private void startConnectionTimer() {

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (reconnect())
                chat.log("Successfully established reconnection timer to the database.", AuctionHouse.getInstance().isDebug());
            else chat.log("Failed to establish reconnection timer.", AuctionHouse.getInstance().isDebug());
        }, 0, 20 * 60 * 60);
    }

    public boolean reconnect() {
        try {
            PreparedStatement s = connection.prepareStatement("/* ping */ SELECT 1");
            s.executeQuery();
            s.closeOnCompletion();
            chat.log("Pinged database", AuctionHouse.getInstance().isDebug());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            chat.log("!! Failed to reconnect to MySQL Database.", AuctionHouse.getInstance().isDebug());
            return false;
        }
    }

    public boolean shutdown() {
        try {
            this.connection.close();
            chat.log("MySQL Connection has successfully shut down.", AuctionHouse.getInstance().isDebug());
        } catch (SQLException e) {
            e.printStackTrace();
            chat.log("!! MySQL Connection failed to shut down.", AuctionHouse.getInstance().isDebug());
            return false;
        }
        this.connected = false;
        return true;
    }

    public int transferCompletedFromFileToMySQL() {
        FileManager fm = AuctionHouse.getInstance().getFileManager();
        AtomicInteger ct = new AtomicInteger(0);
        YamlConfiguration completedFile = fm.getConfig("/database/completed");
        Set<String> completedKeySet = completedFile.getValues(false).keySet();
        for (String s : completedKeySet) {

            String itemstack = completedFile.getString(s + ".ItemStack");
            double price = completedFile.getDouble(s + ".Price");
            double startingBid = completedFile.getDouble(s + ".Starting Bid");
            double minimumIncrement = completedFile.getDouble(s + ".Minimum Increment");
            String creator = completedFile.getString(s + ".Creator");
            long start = completedFile.getLong(s + ".Start");
            long end = completedFile.getLong(s + ".End");
            String buyer = completedFile.getString(s + ".Buyer");
            boolean claimed = completedFile.getBoolean(s + ".Reclaimed");

            try {
                PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + getCompletedTable() + " (ID,ITEM_STACK,PRICE,STARTING_BID,MINIMUM_INCREMENT,CREATOR,START,END,BUYER,CLAIMED) VALUES (?,?,?,?,?,?,?,?,?,?)");

                statement.setString(1, s);
                statement.setString(2, itemstack);
                statement.setDouble(3, price);
                statement.setDouble(4, startingBid);
                statement.setDouble(5, minimumIncrement);
                statement.setString(6, creator);
                statement.setLong(7, start);
                statement.setLong(8, end);
                statement.setString(9, buyer);
                statement.setBoolean(10, claimed);

                statement.executeUpdate();
                statement.closeOnCompletion();

                completedFile.set(s, null);

                chat.log("Transferred complete listing " + s, AuctionHouse.getInstance().isDebug());
                ct.getAndIncrement();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fm.saveFile(completedFile, "/database/completed");
        chat.alert("Transferred " + ct + " completed listings to MySQL.");
        return ct.get();
    }

    public int transferExpiredFromFileToMySQL() {
        FileManager fm = AuctionHouse.getInstance().getFileManager();
        AtomicInteger et = new AtomicInteger(0);
        YamlConfiguration expiredFile = fm.getConfig("/database/expired");
        Set<String> expireKeySet = expiredFile.getValues(false).keySet();
        for (String s : expireKeySet) {
            String creator = expiredFile.getString(s + ".Creator");
            double price = expiredFile.getDouble(s + ".Price");
            double startingBid = expiredFile.getDouble(s + ".Starting Bid");
            double minimumIncrement = expiredFile.getDouble(s + ".Minimum Increment");
            String itemStack = expiredFile.getString(s + ".ItemStack");
            long start = expiredFile.getLong(s + ".Start");
            long end = expiredFile.getLong(s + ".End");
            String reason = expiredFile.getString(s + ".Reason");
            boolean reclaimed = expiredFile.getBoolean(s + ".Reclaimed");

            try {
                PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + getExpiredTable() + " (ID,ITEM_STACK,PRICE,STARTING_BID,MINIMUM_INCREMENT,CREATOR,START,END,REASON,RECLAIMED) VALUES (?,?,?,?,?,?,?,?,?,?)");

                statement.setString(1, s);
                statement.setString(2, itemStack);
                statement.setDouble(3, price);
                statement.setDouble(4, startingBid);
                statement.setDouble(5, minimumIncrement);
                statement.setString(6, creator);
                statement.setLong(7, start);
                statement.setLong(8, end);
                statement.setString(9, reason);
                statement.setBoolean(10, reclaimed);

                statement.executeUpdate();
                statement.close();
                et.getAndIncrement();

                expiredFile.set(s, null);

                chat.log("Transferred expired listing " + s, AuctionHouse.getInstance().isDebug());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fm.saveFile(expiredFile, "/database/expired");
        chat.alert("Transferred " + et + " expired listings to MySQL.");
        return et.get();
    }

    public int transferActiveListingsFromFileToMySQL() {
        FileManager fm = AuctionHouse.getInstance().getFileManager();
        AtomicInteger lt = new AtomicInteger(0);
        YamlConfiguration listingsFile = fm.getConfig("/database/listings");
        Set<String> listingsKeySet = listingsFile.getValues(false).keySet();
        int size = listingsKeySet.size();
        //TODO send chat messages about completion percentage
        for (String s : listingsKeySet) {
            String itemstack = listingsFile.getString(s + ".ItemStack");
            double price = listingsFile.getDouble(s + ".Price");
            double startingBid = listingsFile.getDouble(s + ".Starting Bid");
            double minimumIncrement = listingsFile.getDouble(s + ".Minimum Increment");
            String bidder = listingsFile.getString(s + ".Bidder");
            String creator = listingsFile.getString(s + ".Creator");
            long start = listingsFile.getLong(s + ".Start");
            try {
                PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + getListingsTable() + " (ID,ITEM_STACK,PRICE,STARTING_BID,MINIMUM_INCREMENT,BIDDER,CREATOR,START,END,BUYER,UPDATED) VALUES (?,?,?,?,?,?,?,?,?,?,?)");

                statement.setString(1, s);
                statement.setString(2, itemstack);
                statement.setDouble(3, price);
                statement.setDouble(4, startingBid);
                statement.setDouble(5, minimumIncrement);
                statement.setString(6, bidder);
                statement.setString(7, creator);
                statement.setLong(8, start);
                statement.setLong(9, 0);
                statement.setString(10, null);

                statement.executeUpdate();
                statement.close();

                lt.getAndIncrement();

                listingsFile.set(s, null);

                chat.log("Transferred listing " + s, AuctionHouse.getInstance().isDebug());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fm.saveFile(listingsFile, "/database/listings");
        chat.alert("Transferred " + lt + " active listings to MySQL.");
        return lt.get();
    }

    public int transferUsersFromFileToMySQL() {
        FileManager fm = AuctionHouse.getInstance().getFileManager();
        AtomicInteger ut = new AtomicInteger(0);
        YamlConfiguration usersFile = fm.getConfig("/database/users");
        Set<String> usersKeySet = usersFile.getValues(false).keySet();
        for (String s : usersKeySet) {

            String username = usersFile.getString(s + ".Username") == null ? s : usersFile.getString(s + ".Username");
            boolean alert_create = usersFile.getBoolean(s + ".Alert Create Listings");
            boolean open_admin = usersFile.getBoolean(s + ".Open Admin Mode");
            boolean alert_expire = usersFile.getBoolean(s + ".Alert Near Expire.Status");
            long alert_expire_time = usersFile.getLong(s + ".Alert Near Expire.Time");
            boolean alert_bought = usersFile.getBoolean(s + ".Alert Listing Bought");
            boolean auto_confirm = usersFile.getBoolean(s + ".Auto Confirm Listing");
            boolean sounds = usersFile.getBoolean(s + ".Sounds");


            try {
                PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + getUsersTable() + " (ID,USERNAME,ALERT_CREATE,OPEN_ADMIN,ALERT_NEAR_EXPIRE,ALERT_NEAR_EXPIRE_TIME,LISTING_BOUGHT,AUTO_CONFIRM,SOUNDS) VALUES (?,?,?,?,?,?,?,?,?)");

                statement.setString(1, s);
                statement.setString(2, username);
                statement.setBoolean(3, alert_create);
                statement.setBoolean(4, open_admin);
                statement.setBoolean(5, alert_expire);
                statement.setLong(6, alert_expire_time);
                statement.setBoolean(7, alert_bought);
                statement.setBoolean(8, auto_confirm);
                statement.setBoolean(9, sounds);

                statement.executeUpdate();
                statement.closeOnCompletion();

                usersFile.set(s, null);

                chat.log("Transferred user " + s, AuctionHouse.getInstance().isDebug());
                ut.getAndIncrement();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fm.saveFile(usersFile, "/database/users");
        chat.alert("Transferred " + ut + " users to MySQL.");
        return ut.get();
    }
}
