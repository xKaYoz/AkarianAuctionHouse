package net.akarian.auctionhouse.utils;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Configuration {

    private final FileManager fm;
    @Getter
    @Setter
    private String prefix, db_database, db_host, db_username, db_password, db_listings, db_expired, db_completed, db_users;
    @Getter
    @Setter
    private String listingFee, listingTax;
    @Getter
    private DatabaseType databaseType;
    @Getter
    @Setter
    private boolean updates, uuidBypass, dps_adminMode, dps_bought, dps_create, dps_expire, dps_autoConfirm, creativeListing;
    @Getter
    @Setter
    private double minListing, maxListing;
    @Getter
    @Setter
    private long dps_expireTime;
    @Getter
    @Setter
    private int listingDelay, listingTime, db_port, auctionhouseRefreshTime;
    @Getter
    private AkarianConfiguration configFile;
    @Getter
    @Setter
    private Material spacerItem;
    @Getter
    @Setter
    private int version;

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
        AuctionHouse.getInstance().setDatabaseType(databaseType = type);
    }

    public void reloadConfig() {

        configFile = fm.getConfig("config");

        List<String> header = new ArrayList<>();
        header.add("Akarian Auction House v" + AuctionHouse.getInstance().getDescription().getVersion());
        header.add(" ");
        header.add("database: This is how the database will be saved. Available types are FILE MYSQL FILE2MYSQL MYSQL2FILE");
        header.add("updates: Whether or not to enable updates.");
        header.add("Listing Delay: Delay between listings. Set to 0 to disable. Permission to bypass \"auctionhouse.delay.bypass\"");
        header.add("Listing Time: Time that a listing is on the auction house in seconds. 86400 = 1 day.");
        header.add("Listing Fee: For percentage of listing, use \"5%\". To take a flat rate, use \"5\".");
        configFile.setHeader(header);

        /* Defaults */
        {
            if (!configFile.contains("Version")) {
                configFile.set("Version", 0);
            }
            version = configFile.getInt("Version");
            if (!configFile.contains("Prefix")) {
                configFile.set("Prefix", "&6&lAuctionHouse");
            }
            prefix = configFile.getString("Prefix");

            if (!configFile.contains("Debug")) {
                configFile.set("Debug", false);
            }
            AuctionHouse.getInstance().setDebug(configFile.getBoolean("Debug"));


            if (!configFile.contains("database")) {
                configFile.set("database", "FILE");
            }
            databaseType = DatabaseType.getByStr(configFile.getString("database"));

            if (!configFile.contains("updates")) {
                configFile.set("updates", true);
            } else if (configFile.isString("updates")) {
                if (configFile.getString("updates").equalsIgnoreCase("true")) {
                    configFile.set("updates", true);
                } else if (configFile.getString("updates").equalsIgnoreCase("false")) {
                    configFile.set("updates", false);
                }
            }
            updates = configFile.getBoolean("updates");

            if (!configFile.contains("UUID Bypass")) {
                configFile.set("UUID Bypass", false);
            }
            uuidBypass = configFile.getBoolean("UUID Bypass");

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

            if (!configFile.contains("Listing Time")) {
                configFile.set("Listing Time", 86400);
            }
            listingTime = configFile.getInt("Listing Time");

            if (!configFile.contains("Listing Fee")) {
                configFile.set("Listing Fee", "0%");
            }
            listingFee = configFile.getString("Listing Fee");

            if (!configFile.contains("Sales Tax")) {
                configFile.set("Sales Tax", "7%");
            }
            listingTax = configFile.getString("Sales Tax");

            if (!configFile.contains("Spacer Item")) {
                configFile.set("Spacer Item", Material.GRAY_STAINED_GLASS_PANE.name());
            }
            try {
                spacerItem = Material.valueOf(configFile.getString("Spacer Item"));
            } catch (Exception e) {
                spacerItem = Material.GRAY_STAINED_GLASS_PANE;
            }

            if (!configFile.contains("Default Player Settings.Admin Mode")) {
                configFile.set("Default Player Settings.Admin Mode", false);
            }
            dps_adminMode = configFile.getBoolean("Default Player Settings.Admin Mode");

            if (!configFile.contains("Default Player Settings.Expire Notify")) {
                configFile.set("Default Player Settings.Expire Notify", false);
            }
            dps_expire = configFile.getBoolean("Default Player Settings.Expire Notify");

            if(!configFile.contains("Default Player Settings.Expire Time")) {
                configFile.set("Default Player Settings.Expire Time", 300);
            }
            dps_expireTime = configFile.getLong("Default Player Settings.Expire Time");

            if(!configFile.contains("Default Player Settings.Bought Notify")) {
                configFile.set("Default Player Settings.Bought Notify", false);
            }
            dps_bought = configFile.getBoolean("Default Player Settings.Bought Notify");

            if(!configFile.contains("Default Player Settings.Create Notify")) {
                configFile.set("Default Player Settings.Create Notify", true);
            }
            dps_create = configFile.getBoolean("Default Player Settings.Create Notify");

            if (!configFile.contains("Default Player Settings.Auto Confirm Listing")) {
                configFile.set("Default Player Settings.Auto Confirm Listing", false);
            }
            dps_autoConfirm = configFile.getBoolean("Default Player Settings.Auto Confirm Listing");

            if (!configFile.contains("Creative Listing")) {
                configFile.set("Creative Listing", false);
            }
            creativeListing = configFile.getBoolean("Creative Listing");

            if (!configFile.contains("Auction House Refresh Time")) {
                configFile.set("Auction House Refresh Time", 1);
            }
            auctionhouseRefreshTime = configFile.getInt("Auction House Refresh Time");


        }
        /* MySQL */
        {
            /* Connections */
            {
                if (!configFile.contains("MySQL.Connection.Host")) {
                    configFile.set("MySQL.Connection.Host", "localhost");
                }
                db_host = configFile.getString("MySQL.Connection.Host");

                if (!configFile.contains("MySQL.Connection.Port")) {
                    configFile.set("MySQL.Connection.Port", 3306);
                }
                db_port = configFile.getInt("MySQL.Connection.Port");

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
                db_database = configFile.getString("MySQL.Connection.Database");
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

                if (!configFile.contains("MySQL.Tables.Users")) {
                    configFile.set("MySQL.Tables.Users", "ah_users");
                }
                db_users = configFile.getString("MySQL.Tables.Users");
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

    public void saveConfig() {
        configFile = fm.getConfig("config");

        List<String> header = new ArrayList<>();
        header.add("Akarian Auction House v" + AuctionHouse.getInstance().getDescription().getVersion());
        header.add(" ");
        header.add("database: This is how the database will be saved. Available types are FILE MYSQL FILE2MYSQL MYSQL2FILE");
        header.add("updates: Whether or not to enable updates.");
        header.add("Listing Delay: Delay between listings. Set to 0 to disable. Permission to bypass \"auctionhouse.delay.bypass\"");
        header.add("Listing Time: Time that a listing is on the auction house in seconds. 86400 = 1 day.");
        header.add("Listing Fee: For percentage of listing, use \"5%\". To take a flat rate, use \"$5\".");
        configFile.setHeader(header);

        /* Defaults */
        {
            configFile.set("Version", version);
            configFile.set("Prefix", prefix);
            configFile.set("Debug", AuctionHouse.getInstance().isDebug());
            configFile.set("UUID Bypass", uuidBypass);
            configFile.set("database", databaseType.toString());
            configFile.set("updates", updates);
            configFile.set("Minimum Listing", minListing);
            configFile.set("Maximum Listing", maxListing);
            configFile.set("Listing Delay", listingDelay);
            configFile.set("Listing Time", listingTime);
            configFile.set("Listing Fee", listingFee);
            configFile.set("Sales Tax", listingTax);
            configFile.set("Spacer Item", spacerItem.name());
            configFile.set("Default Player Settings.Admin Mode", dps_adminMode);
            configFile.set("Default Player Settings.Expire Notify", dps_expire);
            configFile.set("Default Player Settings.Expire Time", dps_expireTime);
            configFile.set("Default Player Settings.Bought Notify", dps_bought);
            configFile.set("Default Player Settings.Auto Confirm Listing", dps_autoConfirm);
            configFile.set("Default Player Settings.Create Notify", dps_create);
            configFile.set("Creative Listing", creativeListing);
        }
        /* MySQL */
        {
            /* Connections */
            {
                configFile.set("MySQL.Connection.Host", db_host);
                configFile.set("MySQL.Connection.Port", db_port);
                configFile.set("MySQL.Connection.Username", db_username);
                configFile.set("MySQL.Connection.Password", db_password);
                configFile.set("MySQL.Connection.Database", db_database);
            }
            /* Tables */
            {
                configFile.set("MySQL.Tables.Listings", db_listings);
                configFile.set("MySQL.Tables.Completed", db_completed);
                configFile.set("MySQL.Tables.Expired", db_expired);
                configFile.set("MySQL.Tables.Users", db_users);
            }
        }
        fm.saveFile(configFile, "config");
        reloadConfig();
    }

    public double calculateListingFee(double price) {
        if (listingFee.contains("%")) {
            double percentage = Double.parseDouble(listingFee.replace("%", "")) / 100;

            return price * percentage;
        } else {
            return Double.parseDouble(listingFee.replace("$", ""));
        }
    }

    public double calculateListingTax(Player player, double price) {
        AtomicInteger tax = new AtomicInteger(Integer.parseInt(listingTax.replace("%", "")));

        player.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).map(String::toLowerCase).filter(value -> value.startsWith("auctionhouse.salestax.")).map(value -> value.replace("auctionhouse.salestax.", "")).forEach(value -> {
            //Get amount of max listings
            try {
                int amount = Integer.parseInt(value);

                if (amount > tax.get())
                    tax.set(amount);
            } catch (NumberFormatException ignored) {
            }
        });

        if (listingTax.contains("%")) {
            double percentage = tax.get() / 100d;

            return price * percentage;
        } else {
            return tax.get();
        }
    }
}
