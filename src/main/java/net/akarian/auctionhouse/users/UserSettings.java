package net.akarian.auctionhouse.users;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.listings.Listing;
import net.akarian.auctionhouse.utils.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class UserSettings {

    @Getter
    private final User user;
    private final FileManager fm;
    @Getter @Setter
    private boolean alertCreateListings;
    @Getter @Setter
    private boolean openAdminMode;
    @Getter @Setter
    private boolean alertNearExpire;
    @Getter @Setter
    private long alertNearExpireTime;
    @Getter @Setter
    private boolean alertListingBought;
    @Getter @Setter
    private boolean autoConfirmListing;
    @Getter
    private List<Listing> notified;

    public UserSettings(User user) {
        this.user = user;
        this.fm = AuctionHouse.getInstance().getFileManager();
        this.notified = new ArrayList<>();
    }

    public UserSettings create() {
        YamlConfiguration usersFile = fm.getConfig("/database/users");
        usersFile.set(user.getUuid().toString() + ".Alert Create Listings", AuctionHouse.getInstance().getConfigFile().isDps_create());
        usersFile.set(user.getUuid().toString() + ".Open Admin Mode", AuctionHouse.getInstance().getConfigFile().isDps_adminMode());
        usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Status", AuctionHouse.getInstance().getConfigFile().isDps_expire());
        usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Time", AuctionHouse.getInstance().getConfigFile().getDps_expireTime());
        usersFile.set(user.getUuid().toString() + ".Alert Listing Bought", AuctionHouse.getInstance().getConfigFile().isDps_bought());
        usersFile.set(user.getUuid().toString() + ".Auto Confirm Listing", AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm());
        fm.saveFile(usersFile, "/database/users");
        return this;
    }

    public UserSettings load() {
        YamlConfiguration usersFile = fm.getConfig("/database/users");
        alertCreateListings = usersFile.getBoolean(user.getUuid().toString() + ".Alert Create Listings");
        openAdminMode = usersFile.getBoolean(user.getUuid().toString() + ".Open Admin Mode");
        alertNearExpire = usersFile.getBoolean(user.getUuid().toString() + ".Alert Near Expire.Status");
        alertNearExpireTime = usersFile.getLong(user.getUuid().toString() + ".Alert Near Expire.Time");
        alertListingBought = usersFile.getBoolean(user.getUuid().toString() + ".Alert Listing Bought");
        autoConfirmListing = usersFile.getBoolean(user.getUuid().toString() + ".Auto Confirm Listing");
        return this;
    }

    public UserSettings save() {
        YamlConfiguration usersFile = fm.getConfig("/database/users");
        usersFile.set(user.getUuid().toString() + ".Alert Create Listings", alertCreateListings);
        usersFile.set(user.getUuid().toString() + ".Open Admin Mode", openAdminMode);
        usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Status", alertNearExpire);
        usersFile.set(user.getUuid().toString() + ".Alert Near Expire.Time", alertNearExpireTime);
        usersFile.set(user.getUuid().toString() + ".Alert Listing Bought", alertListingBought);
        usersFile.set(user.getUuid().toString() + ".Auto Confirm Listing", autoConfirmListing);
        fm.saveFile(usersFile, "/database/users");
        return this;
    }

}
