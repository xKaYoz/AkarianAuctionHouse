package net.akarian.auctionhouse.users;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserManager {
    @Getter
    public final List<User> users;

    public UserManager() {
        this.users = new ArrayList<>();
    }

    public void loadOnlineUsers() {
        for (Player p : Bukkit.getOnlinePlayers()) {

        }
    }

    public User loadUser(UUID uuid) {
        User user = new User(uuid);
        users.add(user);
        user.loadUserSettings();
        return user;
    }

}
