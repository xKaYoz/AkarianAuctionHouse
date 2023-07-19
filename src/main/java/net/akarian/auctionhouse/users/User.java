package net.akarian.auctionhouse.users;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.FileManager;
import net.akarian.auctionhouse.utils.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    public void loadUserSettings() {
        Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            switch (AuctionHouse.getInstance().getDatabaseType()) {
                case FILE:
                    YamlConfiguration usersFile = fm.getConfig("/database/users");
                    if (!usersFile.isConfigurationSection(uuid.toString())) {
                        userSettings = createUserSettings().load();
                    } else {
                        userSettings = new UserSettings(this).load();
                    }
                    break;
                case MYSQL:
                    MySQL mySQL = AuctionHouse.getInstance().getMySQL();
                        try {
                            PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT USERNAME FROM " + mySQL.getUsersTable() + " WHERE ID=?");
                            statement.setString(1, uuid.toString());
                            ResultSet rs = statement.executeQuery();

                            if (rs.next()) {
                                userSettings = new UserSettings(User.this).load();
                            } else {
                                userSettings = createUserSettings().load();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
            }

            OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(uuid);
            if (!this.getUsername().equals(offPlayer.getName())) {
                final String oldUsername = this.getUsername();
                this.setUsername(offPlayer.getName());
                this.getUserSettings().saveUsername();
                AuctionHouse.getInstance().getChat().log("Saved new username for " + uuid + " - New: " + this.getUsername() + " Old: " + oldUsername, AuctionHouse.getInstance().isDebug());
            }
        });
    }

}
