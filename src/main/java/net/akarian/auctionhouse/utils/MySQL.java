package net.akarian.auctionhouse.utils;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.database.transfer.DatabaseTransferStatusGUI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

public class MySQL {

    @Getter
    @Setter
    private Connection connection;
    private final Plugin plugin = AuctionHouse.getInstance();
    @Getter
    private String host, database, username, password, listingsTable, expiredTable, completedTable;
    @Getter
    private int port;
    private final Chat chat = AuctionHouse.getInstance().getChat();
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

            chat.log("---------- Akarian Auction House MySQL Manager ----------", AuctionHouse.getInstance().isDebug());
            chat.log("", AuctionHouse.getInstance().isDebug());

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                chat.log("Connecting to the MySQL database...", AuctionHouse.getInstance().isDebug());
                setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));
                chat.log("", AuctionHouse.getInstance().isDebug());
                chat.log(plugin.getName() + " has successfully established a connection to the MySQL database.", AuctionHouse.getInstance().isDebug());
                chat.log("", AuctionHouse.getInstance().isDebug());
                chat.log("Checking Tables...", AuctionHouse.getInstance().isDebug());
                chat.log("", AuctionHouse.getInstance().isDebug());
                if (checkTable(listingsTable, "ID VARCHAR(50) NOT NULL PRIMARY KEY, ITEM_STACK TEXT(65535) NOT NULL, PRICE DOUBLE NOT NULL, CREATOR varchar(255) NOT NULL, START bigint(20) NOT NULL, END bigint(20) DEFAULT '0', BUYER varchar(255) DEFAULT NULL"))
                    chat.log("Listings table checked.", AuctionHouse.getInstance().isDebug());
                else
                    chat.log("!! Listings Table Failed Check !!", AuctionHouse.getInstance().isDebug());
                if (checkTable(expiredTable, "ID varchar(50) NOT NULL PRIMARY KEY, ITEM_STACK TEXT(65535) NOT NULL, PRICE DOUBLE NOT NULL, CREATOR varchar(255) NOT NULL, START bigint(20) NOT NULL, END bigint(20) NOT NULL, REASON varchar(255) NOT NULL, RECLAIMED BOOLEAN DEFAULT '0'"))
                    chat.log("Expired table checked.", AuctionHouse.getInstance().isDebug());
                else
                    chat.log("!! Expired Table Failed Check !!", AuctionHouse.getInstance().isDebug());
                if (checkTable(completedTable, "ID varchar(50) NOT NULL PRIMARY KEY, ITEM_STACK TEXT(65535) NOT NULL, PRICE DOUBLE NOT NULL, CREATOR varchar(255) NOT NULL, START bigint(20) NOT NULL, END bigint(20) NOT NULL, BUYER varchar(255) NOT NULL"))
                    chat.log("Completed table checked.", AuctionHouse.getInstance().isDebug());
                else
                    chat.log("!! Completed Table Failed Check !!", AuctionHouse.getInstance().isDebug());
                chat.log("", AuctionHouse.getInstance().isDebug());
                chat.log("Starting connection timer.", AuctionHouse.getInstance().isDebug());
                startConnectionTimer();
                chat.log("---------------------------------------------", AuctionHouse.getInstance().isDebug());
                if (transferring != null) {
                    Bukkit.getPlayer(transferring).openInventory(new DatabaseTransferStatusGUI(Bukkit.getPlayer(transferring)).getInventory());
                }
                return connected = true;
            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().log(Level.SEVERE, "An error has occurred while connecting to the database. Please see stacktrace above.");
                chat.log("", AuctionHouse.getInstance().isDebug());
                chat.log("---------------------------------------------", AuctionHouse.getInstance().isDebug());
                if (transferring != null) {
                    Bukkit.getPlayer(transferring).openInventory(new DatabaseTransferStatusGUI(Bukkit.getPlayer(transferring)).connectionDisapproved());
                }
                if (e.getCause() == null) return false;
                chat.alert("&c&l" + plugin.getName() + " has encountered an error connecting to the MySQL database. Please check console. E" + e.getCause().getLocalizedMessage());
                return connected = false;
            }
        }
        return true;
    }

    public boolean checkTable(String tableName, String query){
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + query + ")");

            if (tableName.equalsIgnoreCase(expiredTable)) {

                try {
                    s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN PRICE DOUBLE NOT NULL AFTER ITEM_STACK");
                    s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN START bigint(20) NOT NULL AFTER CREATOR");
                    s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN END bigint(20) NOT NULL AFTER START");
                    s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN REASON varchar(255) NOT NULL AFTER END");
                    s.executeUpdate("ALTER TABLE " + expiredTable + " ADD COLUMN RECLAIMED BOOLEAN DEFAULT 0 AFTER REASON");
                    return true;
                } catch (SQLException e) {
                    return true;
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private void startConnectionTimer() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if(reconnect())
                chat.log("Successfully established reconnection timer to the database.", AuctionHouse.getInstance().isDebug());
            else
                chat.log("Failed to establish reconnection timer.", AuctionHouse.getInstance().isDebug());
        }, 0, 20 * 60 * 60);
    }

    public boolean reconnect() {
        try {
            if(getConnection().isClosed()) {
                setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password));
                chat.log("Successfully reconnected to MySQL Database.", AuctionHouse.getInstance().isDebug());
            } else {
                chat.log("Connection to Database not closed. Not reconnecting.", AuctionHouse.getInstance().isDebug());
            }
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

}
