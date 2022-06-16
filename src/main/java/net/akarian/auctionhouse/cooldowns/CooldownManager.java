package net.akarian.auctionhouse.cooldowns;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {

    private final HashMap<UUID, Long> map;
    FileManager fm;
    YamlConfiguration cooldownFile;

    public CooldownManager() {
        this.map = new HashMap<>();
        this.fm = AuctionHouse.getInstance().getFileManager();

        if (!fm.getFile("/database/cooldowns").exists()) {
            fm.createFile("/database/cooldowns");
        }
        loadCooldowns();
    }

    public boolean isCooldown(Player player) {
        if (map.containsKey(player.getUniqueId())) {
            if (map.get(player.getUniqueId()) <= System.currentTimeMillis()) {
                map.remove(player.getUniqueId());
                return false;
            }
            return true;
        }
        return false;
    }

    public int getRemaining(Player player) {
        if (map.containsKey(player.getUniqueId())) {
            if (map.get(player.getUniqueId()) <= System.currentTimeMillis()) {
                map.remove(player.getUniqueId());
                return 0;
            }
            return (int) (map.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
        }
        return 0;
    }

    public void setCooldown(Player player) {
        map.put(player.getUniqueId(), System.currentTimeMillis() + (AuctionHouse.getInstance().getConfigFile().getListingDelay() * 1000L));
    }

    public void saveCooldowns() {
        cooldownFile = fm.getConfig("/database/cooldowns");
        cooldownFile.set("Cooldowns", null);
        int saved = 0;

        for (UUID uuid : map.keySet()) {
            if (map.get(uuid) <= System.currentTimeMillis()) {
                map.remove(uuid);
            } else {
                cooldownFile.set("Cooldowns." + uuid.toString(), map.get(uuid));
                saved++;
            }
        }
        AuctionHouse.getInstance().getChat().log("Saved " + saved + " cooldowns.");
        fm.saveFile(cooldownFile, "/database/cooldowns");
    }

    public void loadCooldowns() {
        cooldownFile = fm.getConfig("/database/cooldowns");
        int loaded = 0;
        for (String s : cooldownFile.getConfigurationSection("Cooldowns").getKeys(false)) {
            UUID uuid = UUID.fromString(s);
            map.put(uuid, cooldownFile.getLong("Cooldowns." + uuid));
            loaded++;
        }
        AuctionHouse.getInstance().getChat().log("Loaded " + loaded + " cooldowns.");
    }

}
