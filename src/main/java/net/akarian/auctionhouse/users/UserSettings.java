package net.akarian.auctionhouse.users;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.FileManager;
import net.akarian.auctionhouse.utils.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserSettings {

    @Getter
    private final User user;
    private final FileManager fm;
    @Getter @Setter
    private boolean alertCreateListings;
    @Getter @Setter
    private boolean openAdminMode;
    @Getter
    @Setter
    private boolean alertNearExpire;
    @Getter
    @Setter
    private long alertNearExpireTime;
    @Getter
    @Setter
    private boolean alertListingBought;
    @Getter
    @Setter
    private boolean autoConfirmListing;

    @Getter
    @Setter
    private boolean sounds;
    @Getter
    private final List<Listing> notified;

    public UserSettings(User user) {
        this.user = user;
        this.fm = AuctionHouse.getInstance().getFileManager();
        this.notified = new ArrayList<>();
    }

    public UserSettings create() {
        String name = AuctionHouse.getInstance().getNameManager().getName(user.getUuid());
        MySQL mySQL = AuctionHouse.getInstance().getMySQL();
        switch (AuctionHouse.getInstance().getDatabaseType()) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("INSERT INTO " + mySQL.getUsersTable() + " (ID,USERNAME,ALERT_CREATE,OPEN_ADMIN,ALERT_NEAR_EXPIRE,ALERT_NEAR_EXPIRE_TIME,LISTING_BOUGHT,AUTO_CONFIRM,SOUNDS) VALUES (?,?,?,?,?,?,?,?,?)");

                        statement.setString(1, user.getUuid().toString());
                        statement.setString(2, name);
                        statement.setBoolean(3, AuctionHouse.getInstance().getConfigFile().isDps_create());
                        statement.setBoolean(4, AuctionHouse.getInstance().getConfigFile().isDps_adminMode());
                        statement.setBoolean(5, AuctionHouse.getInstance().getConfigFile().isDps_expire());
                        statement.setLong(6, AuctionHouse.getInstance().getConfigFile().getDps_expireTime());
                        statement.setBoolean(7, AuctionHouse.getInstance().getConfigFile().isDps_bought());
                        statement.setBoolean(8, AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm());
                        statement.setBoolean(9, AuctionHouse.getInstance().getConfigFile().isDps_sounds());

                        statement.executeUpdate();
                        statement.closeOnCompletion();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                break;
            case FILE:
                YamlConfiguration usersFile = fm.getConfig("/database/users");
                usersFile.set(user.getUuid().toString() + ".Username", name);
                usersFile.set(user.getUuid().toString() + ".Alert Create Listings", AuctionHouse.getInstance().getConfigFile().isDps_create());
                usersFile.set(user.getUuid().toString() + ".Open Admin Mode", AuctionHouse.getInstance().getConfigFile().isDps_adminMode());
                usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Status", AuctionHouse.getInstance().getConfigFile().isDps_expire());
                usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Time", AuctionHouse.getInstance().getConfigFile().getDps_expireTime());
                usersFile.set(user.getUuid().toString() + ".Alert Listing Bought", AuctionHouse.getInstance().getConfigFile().isDps_bought());
                usersFile.set(user.getUuid().toString() + ".Auto Confirm Listing", AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm());
                usersFile.set(user.getUuid().toString() + ".Sounds", AuctionHouse.getInstance().getConfigFile().isDps_sounds());
                fm.saveFile(usersFile, "/database/users");
                break;
        }

        alertCreateListings = AuctionHouse.getInstance().getConfigFile().isDps_create();
        openAdminMode = AuctionHouse.getInstance().getConfigFile().isDps_adminMode();
        alertNearExpire = AuctionHouse.getInstance().getConfigFile().isDps_expire();
        alertNearExpireTime = AuctionHouse.getInstance().getConfigFile().getDps_expireTime();
        alertListingBought = AuctionHouse.getInstance().getConfigFile().isDps_bought();
        autoConfirmListing = AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm();
        return this;
    }

    public UserSettings load() {
        MySQL mySQL = AuctionHouse.getInstance().getMySQL();
        switch (AuctionHouse.getInstance().getDatabaseType()) {
            case FILE:
                YamlConfiguration usersFile = fm.getConfig("/database/users");
                user.setUsername(usersFile.getString(user.getUuid().toString() + ".Username"));
                alertCreateListings = usersFile.getBoolean(user.getUuid().toString() + ".Alert Create Listings");
                openAdminMode = usersFile.getBoolean(user.getUuid().toString() + ".Open Admin Mode");
                alertNearExpire = usersFile.getBoolean(user.getUuid().toString() + ".Alert Near Expire.Status");
                alertNearExpireTime = usersFile.getLong(user.getUuid().toString() + ".Alert Near Expire.Time");
                alertListingBought = usersFile.getBoolean(user.getUuid().toString() + ".Alert Listing Bought");
                autoConfirmListing = usersFile.getBoolean(user.getUuid().toString() + ".Auto Confirm Listing");
                sounds = usersFile.getBoolean(user.getUuid().toString() + ".Sounds");

                if (!usersFile.contains(user.getUuid().toString() + ".Username")) {
                    usersFile.set(user.getUuid().toString() + ".Username", Objects.requireNonNull(Bukkit.getPlayer(user.getUuid())).getName());
                }

                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(user.getUuid()); // Running async because this makes an api call
                    if (!user.getUsername().equals(offPlayer.getName())) {
                        final String oldUsername = user.getUsername();
                        user.setUsername(offPlayer.getName());
                        user.getUserSettings().saveUsername();
                        AuctionHouse.getInstance().getChat().log("Saved new username for " + user.getUuid() + " - New: " + user.getUsername() + " Old: " + oldUsername, AuctionHouse.getInstance().isDebug());
                    }
                });
                break;
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT * FROM " + mySQL.getUsersTable() + " WHERE ID=?");

                        statement.setString(1, user.getUuid().toString());

                        ResultSet rs = statement.executeQuery();

                        while (rs.next()) {
                            user.setUsername(rs.getString(2));
                            alertCreateListings = rs.getBoolean(3);
                            openAdminMode = rs.getBoolean(4);
                            alertNearExpire = rs.getBoolean(5);
                            alertNearExpireTime = rs.getLong(6);
                            alertListingBought = rs.getBoolean(7);
                            autoConfirmListing = rs.getBoolean(8);
                            sounds = rs.getBoolean(9);
                        }

                        OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(user.getUuid());
                        if (!user.getUsername().equals(offPlayer.getName())) {
                            final String oldUsername = user.getUsername();
                            user.setUsername(offPlayer.getName());
                            user.getUserSettings().saveUsername();
                            AuctionHouse.getInstance().getChat().log("Saved new username for " + user.getUuid() + " - New: " + user.getUsername() + " Old: " + oldUsername, AuctionHouse.getInstance().isDebug());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                break;
        }
        return this;
    }

    public UserSettings save() {
        String name = AuctionHouse.getInstance().getNameManager().getName(user.getUuid());
        MySQL mySQL = AuctionHouse.getInstance().getMySQL();
        switch (AuctionHouse.getInstance().getDatabaseType()) {
            case MYSQL:
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getUsersTable() + " SET USERNAME=?,ALERT_CREATE=?,OPEN_ADMIN=?,ALERT_NEAR_EXPIRE=?,ALERT_NEAR_EXPIRE_TIME=?,LISTING_BOUGHT=?,AUTO_CONFIRM=?,SOUNDS=? WHERE ID=?");

                        statement.setString(1, name);
                        statement.setBoolean(2, alertCreateListings);
                        statement.setBoolean(3, openAdminMode);
                        statement.setBoolean(4, alertNearExpire);
                        statement.setLong(5, alertNearExpireTime);
                        statement.setBoolean(6, alertListingBought);
                        statement.setBoolean(7, autoConfirmListing);
                        statement.setBoolean(8, sounds);
                        statement.setString(9, user.getUuid().toString());

                        statement.executeUpdate();
                        statement.closeOnCompletion();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                break;
            case FILE:
                YamlConfiguration usersFile = fm.getConfig("/database/users");
                usersFile.set(user.getUuid().toString() + ".Username", name);
                usersFile.set(user.getUuid().toString() + ".Alert Create Listings", alertCreateListings);
                usersFile.set(user.getUuid().toString() + ".Open Admin Mode", openAdminMode);
                usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Status", alertNearExpire);
                usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Time", alertNearExpireTime);
                usersFile.set(user.getUuid().toString() + ".Alert Listing Bought", alertListingBought);
                usersFile.set(user.getUuid().toString() + ".Auto Confirm Listing", autoConfirmListing);
                usersFile.set(user.getUuid().toString() + ".Sounds", sounds);
                fm.saveFile(usersFile, "/database/users");
                break;
        }
        return this;
    }

    public UserSettings saveUsername() {
        switch (AuctionHouse.getInstance().getDatabaseType()) {
            case FILE:
                YamlConfiguration usersFile = fm.getConfig("/database/users");
                usersFile.set(user.getUuid().toString() + ".Username", user.getUsername());
                fm.saveFile(usersFile, "/database/users");
                break;
            case MYSQL:
                MySQL mySQL = AuctionHouse.getInstance().getMySQL();
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        PreparedStatement statement = mySQL.getConnection().prepareStatement("UPDATE " + mySQL.getUsersTable() + " SET USERNAME=? WHERE ID=?");
                        statement.setString(1, user.getUsername());
                        statement.setString(2, user.getUuid().toString());
                        statement.executeUpdate();
                        statement.closeOnCompletion();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                break;
        }
        return this;
    }

}
