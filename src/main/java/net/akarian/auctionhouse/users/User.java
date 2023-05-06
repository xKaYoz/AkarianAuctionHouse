package net.akarian.auctionhouse.users;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.FileManager;
import net.akarian.auctionhouse.utils.MySQL;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class User {

    @Getter
    private final UUID uuid;
    private final FileManager fm;
    @Getter
    private UserSettings userSettings;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private boolean adminMode;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.fm = AuctionHouse.getInstance().getFileManager();
        adminMode = false;
    }

    public UserSettings createUserSettings() {
        return userSettings = new UserSettings(this).create();
    }

    public UserSettings loadUserSettings() {
        switch (AuctionHouse.getInstance().getDatabaseType()) {
            case FILE:
                YamlConfiguration usersFile = fm.getConfig("/database/users");
                if (!usersFile.isConfigurationSection(uuid.toString())) {
                    return userSettings = createUserSettings().load();
                } else {
                    return userSettings = new UserSettings(this).load();
                }
            case MYSQL:
                MySQL mySQL = AuctionHouse.getInstance().getMySQL();
                try {
                    PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT USERNAME FROM " + mySQL.getUsersTable() + " WHERE ID=?");
                    statement.setString(1, uuid.toString());
                    ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        return userSettings = new UserSettings(this).load();
                    } else {
                        return userSettings = createUserSettings().load();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
        }
        return userSettings = new UserSettings(this).load();
    }

}
