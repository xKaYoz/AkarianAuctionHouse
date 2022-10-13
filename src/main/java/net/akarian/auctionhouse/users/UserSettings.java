package net.akarian.auctionhouse.users;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;

public class UserSettings {

    @Getter
    private final User user;
    private final FileManager fm;
    @Getter
    private boolean alertCreateListings;
    @Getter
    private boolean openAdminMode;
    @Getter
    private boolean alertNearExpire;
    @Getter
    private long alertNearExpireTime;

    public UserSettings(User user) {
        this.user = user;
        this.fm = AuctionHouse.getInstance().getFileManager();
    }

    public UserSettings create() {
        YamlConfiguration usersFile = fm.getConfig("/database/users");
        usersFile.set(user.getUuid().toString() + ".Alert Create Listings", true);
        usersFile.set(user.getUuid().toString() + ".Open Admin Mode", false);
        usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Status", false);
        usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Time", 300);
        fm.saveFile(usersFile, "/database/users");
        return this;
    }

    public UserSettings load() {
        YamlConfiguration usersFile = fm.getConfig("/database/users");
        alertCreateListings = usersFile.getBoolean(user.getUuid().toString() + ".Alert Create Listings");
        openAdminMode = usersFile.getBoolean(user.getUuid().toString() + ".Open Admin Mode");
        alertNearExpire = usersFile.getBoolean(user.getUuid().toString() + ".Alert Near Expire.Status");
        alertNearExpireTime = usersFile.getLong(user.getUuid().toString() + ".Alert Near Expire.Time");
        return this;
    }

    public UserSettings save() {
        YamlConfiguration usersFile = fm.getConfig("/database/users");
        usersFile.set(user.getUuid().toString() + ".Alert Create Listings", alertCreateListings);
        usersFile.set(user.getUuid().toString() + ".Open Admin Mode", openAdminMode);
        usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Status", alertNearExpire);
        usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Time", alertNearExpireTime);
        fm.saveFile(usersFile, "/database/users");
        return this;
    }

}
