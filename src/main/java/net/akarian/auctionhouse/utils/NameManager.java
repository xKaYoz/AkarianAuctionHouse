package net.akarian.auctionhouse.utils;
import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NameManager {

    private final HashMap<UUID, HashMap<String, Object>> cache = new HashMap<>();
    private int timer;

    public NameManager() {
        startCacheTimer();
    }

    private final String NAME_URL = "https://sessionserver.mojang.com"
            + "/session/minecraft/profile/";

    /**
     * Returns the name of the searched player.
     *
     * @param uuid The UUID of a player.
     * @return The name of the given player.
     */
    public String getName(UUID uuid) {
        return getName(uuid.toString());
    }

    /**
     * Returns the name of the searched player.
     *
     * @param uuid The String UUID of a player (can be trimmed or the normal version).
     * @return The name of the given player.
     */
    public String getName(String uuid) {

        if (uuid.equalsIgnoreCase("console")) {
            return "CONSOLE";
        }

        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) return Bukkit.getPlayer(UUID.fromString(uuid)).getName();
        if (cache.containsKey(UUID.fromString(uuid))) {
            HashMap<String, Object> nc = cache.get(UUID.fromString(uuid));
            return (String) nc.get("Name");
        }
        HashMap<String, Object> sc = new HashMap<>();
        String dash = uuid;

        uuid = uuid.replace("-", "");
        String output = callURL(NAME_URL + uuid);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 20000; i++) {
            if (output.charAt(i) == 'n' && output.charAt(i + 1) == 'a'
                    && output.charAt(i + 2) == 'm'
                    && output.charAt(i + 3) == 'e') {
                for (int k = i + 9; k < 20000; k++) {
                    char curr = output.charAt(k);
                    if (curr != '"') {
                        result.append(curr);
                    } else {
                        break;
                    }
                }
                break;
            }
        }
        sc.put("Name", result.toString());
        sc.put("Timer", System.currentTimeMillis());
        cache.put(UUID.fromString(dash), sc);
        return result.toString();
    }

    private String callURL(String urlStr) {
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn;
        InputStreamReader in;
        try {
            URL url = new URL(urlStr);
            urlConn = url.openConnection();
            if (urlConn != null) {
                urlConn.setReadTimeout(60 * 1000);
            }
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                int cp;
                while ((cp = bufferedReader.read()) != -1) {
                    sb.append((char) cp);
                }
                bufferedReader.close();
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void startCacheTimer() {
        timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(AuctionHouse.getInstance(), new Runnable() {
            @Override
            public void run() {
                List<UUID> toRemove = new ArrayList<>();
                for (UUID uuid : cache.keySet()) {
                    HashMap<String, Object> lc = cache.get(uuid);
                    long time = (long) lc.get("Timer");
                    if (time + (10 * 60 * 1000) < System.currentTimeMillis())
                        toRemove.add(uuid);

                }
                toRemove.forEach(uuid -> cache.remove(uuid));
            }
        }, 0, 30 * 20);
    }

}
