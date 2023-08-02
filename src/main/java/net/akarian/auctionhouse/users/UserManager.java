package net.akarian.auctionhouse.users;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.DatabaseType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManager {
    @Getter
    public final List<User> users;

    public UserManager() {
        this.users = new ArrayList<>();
        checkVersion();
        loadOnlineUsers();
    }

    public User getUser(Player player) {
        for(User u : users) {
            if(u.getUuid().toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                return u;
            }
        }
        return null;
    }

    public void saveUsers() {
        for(User user : users) {
            user.getUserSettings().save();
        }
    }

    public void loadOnlineUsers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            loadUser(p.getUniqueId(), p.getName());
        }
    }

    public User loadUser(UUID uuid, String username) {
        User user = new User(uuid);
        users.add(user);
        user.loadUserSettings(username);

        return user;
    }

    public void unloadUser(User user) {
        users.remove(user);
        user.getUserSettings().save();
    }

    public void checkVersion() {
        if (AuctionHouse.getInstance().getConfigFile().getVersion() == 0) {
            if (AuctionHouse.getInstance().getDatabaseType() == DatabaseType.MYSQL) {
                AuctionHouse.getInstance().getChat().log("Transferring Users from File to MySQL for new update", AuctionHouse.getInstance().isDebug());
                YamlConfiguration usersFile = AuctionHouse.getInstance().getFileManager().getConfig("/database/users");
                if (usersFile.getKeys(false).size() != 0) {
                    //Transfer the users file to mysql from first install
                    AuctionHouse.getInstance().getMySQL().transferUsersFromFileToMySQL();
                }
            }
            AuctionHouse.getInstance().getConfigFile().setVersion(1);
        }
    }

}
