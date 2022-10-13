package net.akarian.auctionhouse.users;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;

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
        switch (AuctionHouse.getInstance().getDatabaseType()) {
            case FILE:
                YamlConfiguration usersFile = fm.getConfig("/database/users");
                if (!usersFile.isConfigurationSection(uuid.toString())) {
                    return createUserSettings();
                }
                return new UserSettings(this).load();
        }
        return null;
    }

}
