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
            loadUser(p.getUniqueId());
        }
    }

    public User loadUser(UUID uuid) {
        User user = new User(uuid);
        users.add(user);
        user.loadUserSettings();
        return user;
    }

    public void unloadUser(User user) {
        users.remove(user);
        user.getUserSettings().save();
    }

}
