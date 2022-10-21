package net.akarian.auctionhouse.users;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.UUID;

public class User {

    @Getter
    private final UUID uuid;
    private final FileManager fm;
    @Getter
    private UserSettings userSettings;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.fm = AuctionHouse.getInstance().getFileManager();
    }

    public UserSettings createUserSettings() {
        return userSettings = new UserSettings(this).create();
    }

    public UserSettings loadUserSettings() {
        YamlConfiguration usersFile = fm.getConfig("/database/users");
        if (!usersFile.isConfigurationSection(uuid.toString())) {
            return userSettings = createUserSettings().load();
        }
        return userSettings = new UserSettings(this).load();
    }

}
