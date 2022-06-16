package net.akarian.auctionhouse.utils;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private final FileManager fm;
    @Getter
    private String prefix, databaseType, db_host, db_username, db_password, db_listings, db_expired, db_completed;
    @Getter
    private DatabaseType db_database;
    @Getter
    private boolean updates;
    @Getter
    private double minListing, maxListing;
    @Getter
    private int listingDelay;
    @Getter
    private YamlConfiguration configFile;

    public Configuration() {
        fm = AuctionHouse.getInstance().getFileManager();
        if (!fm.getFile("config").exists()) {
            fm.createFile("config");
        }
        reloadConfig();
    }

    public void setDatabaseType(DatabaseType type) {
        configFile = fm.getConfig("config");
        configFile.set("database", type.toString());
        fm.saveFile(configFile, "config");
        AuctionHouse.getInstance().setDatabaseType(db_database = type);
    }

    public void reloadConfig() {

        configFile = fm.getConfig("config");

        List<String> header = new ArrayList<>();
        header.add("Akarian Auction House v" + AuctionHouse.getInstance().getDescription().getVersion());
        header.add(" ");
        header.add("database: This is how the database will be saved. Available types are FILE MYSQL FILE2MYSQL MYSQL2FILE");
        header.add("updates: Wheather or not to enable updates.");
        configFile.options().setHeader(header);

        /* Defaults */
        {
            if (!configFile.contains("Prefix")) {
                configFile.set("Prefix", "&6&lAuctionHouse");
            }
            prefix = configFile.getString("Prefix");

            if (!configFile.contains("database")) {
                configFile.set("database", "FILE");
            }
            databaseType = configFile.getString("database");

            if (!configFile.contains("updates")) {
                configFile.set("updates", "true");
            }
            updates = configFile.getBoolean("updates");

            if (!configFile.contains("Minimum Listing")) {
                configFile.set("Minimum Listing", 10.00D);
            }
            minListing = configFile.getDouble("Minimum Listing");

            if (!configFile.contains("Maximum Listing")) {
                configFile.set("Maximum Listing", 1000000000.00D);
            }
            maxListing = configFile.getDouble("Maximum Listing");

            if (!configFile.contains("Listing Delay")) {
                configFile.set("Listing Delay", 10);
            }
            listingDelay = configFile.getInt("Listing Delay");
        }
        /* MySQL */
        {
            /* Connections */
            {
                if (!configFile.contains("MySQL.Connection.Host")) {
                    configFile.set("MySQL.Connection.Host", "localhost");
                }
                db_host = configFile.getString("MySQL.Connection.Host");

                if (!configFile.contains("MySQL.Connection.Username")) {
                    configFile.set("MySQL.Connection.Username", "test");
                }
                db_username = configFile.getString("MySQL.Connection.Username");

                if (!configFile.contains("MySQL.Connection.Password")) {
                    configFile.set("MySQL.Connection.Password", "password");
                }
                db_password = configFile.getString("MySQL.Connection.Password");

                if (!configFile.contains("MySQL.Connection.Database")) {
                    configFile.set("MySQL.Connection.Database", "database");
                }
                db_database = DatabaseType.getByStr(configFile.getString("MySQL.Connection.Database"));
            }
            /* Tables */
            {
                if (!configFile.contains("MySQL.Tables.Listings")) {
                    configFile.set("MySQL.Tables.Listings", "ah_listings");
                }
                db_listings = configFile.getString("MySQL.Tables.Listings");

                if (!configFile.contains("MySQL.Tables.Completed")) {
                    configFile.set("MySQL.Tables.Completed", "ah_completed");
                }
                db_completed = configFile.getString("MySQL.Tables.Completed");

                if (!configFile.contains("MySQL.Tables.Expired")) {
                    configFile.set("MySQL.Tables.Expired", "ah_expired");
                }
                db_expired = configFile.getString("MySQL.Tables.Expired");
            }
        }

        /* Templates

        Add String
        if(!configFile.contains("1")) {
            configFile.set("1", "2");
        }
        3 = configFile.getString("1");

        Add Double
        if(!configFile.contains("1")) {
            configFile.set("1", 2.0);
        }
        3 = configFile.getDouble("1");

        Add Int
        if(!configFile.contains("1")) {
            configFile.set("1", 2);
        }
        3 = configFile.getInt("1");
        */
        fm.saveFile(configFile, "config");
    }

}
