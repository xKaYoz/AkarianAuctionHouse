package net.akarian.auctionhouse.utils;

import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;
import org.geysermc.floodgate.api.FloodgateApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class NameManager {

    private final HashMap<String, HashMap<String, Object>> cache = new HashMap<>();
    private final List<String> queued = new ArrayList<>();
    private final String NAME_URL = "https://sessionserver.mojang.com" + "/session/minecraft/profile/";

    private BukkitTask timer;

    public NameManager() {
        startCacheTimer();
    }

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
        //Return the name of Console if it is console
        if (uuid.equalsIgnoreCase("console")) {
            return "CONSOLE";
        }
        //UUID verification bypass
        if (AuctionHouse.getInstance().getConfigFile().isUuidBypass()) {
            //Check the cache
            if (cache.containsKey(uuid)) {
                HashMap<String, Object> nc = cache.get(uuid);
                nc.replace("Timer", System.currentTimeMillis());
                cache.replace(uuid, nc);
                return (String) nc.get("Name");
            }
            return checkDatabase(uuid);
        }
        //Check if we can use the BukkitAPI to get the UUID
        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) return Bukkit.getPlayer(UUID.fromString(uuid)).getName();
        //Check if we already have this UUID cached from a previous check
        if (cache.containsKey(uuid)) {
            HashMap<String, Object> nc = cache.get(uuid);
            nc.replace("Timer", System.currentTimeMillis());
            cache.replace(uuid, nc);
            return (String) nc.get("Name");
        }
        //Check with Floodgate API
        if (AuctionHouse.getInstance().isFloodgate()) {
            HashMap<String, Object> sc = new HashMap<>();
            FloodgateApi floodgate = FloodgateApi.getInstance();
            try {
                String name = floodgate.getPlayer(UUID.fromString(uuid)).getUsername();
                sc.put("Name", name);
                sc.put("Timer", System.currentTimeMillis());
                cache.put(uuid, sc);
                return name;
            } catch (NullPointerException e) {
                return checkDatabase(uuid);
            }
        }
        return checkDatabase(uuid);
        /*
        //Check with the MojangAPI
        final String[] s = new String[1];
        s[0] = uuid;
        final String fuuid = uuid;
        Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
            try {
                HashMap<String, Object> sc = new HashMap<>();
                String edit;

                edit = fuuid.replace("-", "");
                String output = callURL(NAME_URL + edit);
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < 20000; i++) {
                    if(output.length() == 0) return;
                    if (output.charAt(i) == 'n' && output.charAt(i + 1) == 'a' && output.charAt(i + 2) == 'm' && output.charAt(i + 3) == 'e') {
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
                cache.put(fuuid, sc);
                s[0] = result.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return s[0]; */
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
                in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                int cp;
                while ((cp = bufferedReader.read()) != -1) {
                    sb.append((char) cp);
                }
                bufferedReader.close();
                in.close();
            }
        } catch (Exception e) {
            AuctionHouse.getInstance().getChat().log("Error while trying to connect to NameManager URL.", AuctionHouse.getInstance().isDebug());
        }
        return sb.toString();
    }

    private void startCacheTimer() {
        timer = Bukkit.getScheduler().runTaskTimerAsynchronously(AuctionHouse.getInstance(), () -> {
            List<String> toRemove = new ArrayList<>();
            for (String uuid : cache.keySet()) {
                HashMap<String, Object> lc = cache.get(uuid);
                long time = (long) lc.get("Timer");
                if (time + (10 * 60 * 1000) < System.currentTimeMillis()) toRemove.add(uuid);

            }
            toRemove.forEach(cache::remove);
        }, 0, 30 * 20);
    }

    private String checkDatabase(String uuid) {
        switch (AuctionHouse.getInstance().getDatabaseType()) {
            case FILE:
                YamlConfiguration config = AuctionHouse.getInstance().getFileManager().getConfig("/database/users");
                if (config.isConfigurationSection(uuid)) {
                    HashMap<String, Object> sc = new HashMap<>();
                    sc.put("Name", config.getString(uuid + ".Username"));
                    sc.put("Timer", System.currentTimeMillis());
                    cache.put(uuid, sc);
                    return config.getString(uuid + ".Username");
                }
                break;
            case MYSQL:
                final String finalUUID = uuid;
                final String[] s = new String[1];
                //Set filler text to the UUID
                s[0] = uuid;
                //Checks if we already have a query sent
                if (queued.contains(uuid)) {
                    return uuid;
                }
                queued.add(uuid);
                Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getInstance(), () -> {
                    try {
                        MySQL mySQL = AuctionHouse.getInstance().getMySQL();

                        PreparedStatement statement = mySQL.getConnection().prepareStatement("SELECT USERNAME FROM " + mySQL.getUsersTable() + " WHERE ID=?");
                        statement.setString(1, finalUUID);
                        ResultSet rs = statement.executeQuery();

                        if (rs.next()) {
                            HashMap<String, Object> sc = new HashMap<>();
                            sc.put("Name", rs.getString(1));
                            sc.put("Timer", System.currentTimeMillis());
                            cache.put(finalUUID, sc);
                            s[0] = rs.getString(1);
                            queued.remove(finalUUID);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                });
                return s[0];
        }
        return uuid;
    }

}
