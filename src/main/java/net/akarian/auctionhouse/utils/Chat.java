package net.akarian.auctionhouse.utils;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class Chat {

    @Getter
    @Setter
    private String prefix;
    private final Plugin plugin;

    /**
     * Chat API
     *
     * @param plugin - JavaPlugin class of the Plugin
     * @param prefix - Prefix of the Plugin
     */
    public Chat(Plugin plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = prefix;
    }

    /** Format color codes in a String
     *
     * @param str - String to format
     * @return - Formatted String
     */
    public String format(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    /** Format money in the instance of a Double
     *
     * @param obj - Object of money to format
     * @return - Formatted money in a String
     */
    public String formatMoney(Object obj) {

        return String.format("%,.2f", Double.parseDouble(obj.toString()));

    }

    /** Format time
     *
     * @param se - Length of time to measure
     * @return - Formatted Time
     */
    public String formatTime(long se) {
        long delay = se;
        String s = "";
        if (delay >= 86400) {
            long days = delay / 86400;
            if (days >= 2) s += days + "d ";
            else s += "1d ";
            delay = delay - (days * 86400);

            if (delay >= 3600) {
                long hours = delay / 3600;
                if (hours >= 2) s += hours + "h ";
                else s += "1h ";

                delay = delay - (hours * 3600);

                if (delay >= 60) {
                    long minutes = delay / 60;
                    if (minutes >= 2) s += minutes + "m ";
                    else s += "1m";

                    delay = delay - (minutes * 60);

                } else {

                    s += "0m ";

                }

            } else {

                s += "0h ";

                if (delay >= 60) {
                    long minutes = delay / 60;
                    if (minutes >= 2) s += minutes + "m ";
                    else s += "1m";

                    delay = delay - (minutes * 60);

                } else {

                    s += "0m ";

                }

            }

        } else {
            if (delay >= 3600) {

                long hours = delay / 3600;
                if (hours >= 2) s += hours + "h ";
                else s += "1h ";

                delay = delay - (hours * 3600);

                if (delay >= 60) {
                    long minutes = delay / 60;
                    if (minutes >= 2) s += minutes + "m ";
                    else s += "1m ";

                    delay = delay - (minutes * 60);

                }
            } else {
                if (delay >= 60) {
                    long minutes = delay / 60;
                    if (minutes >= 2) s += minutes + "m ";
                    else s += "1m ";

                    delay = delay - (minutes * 60);

                }
            }
        }
        if (delay >= 1) {
            if (delay >= 2) s += delay + "s ";
            else s += "1s ";
        } else {
            s += "0s ";
        }
        return s.trim();
    }

    /** Format the color codes in a List
     *
     * @param list - List to format
     * @return - Formatted List
     */
    public ArrayList<String> formatList(List<String> list) {

        ArrayList<String> formatted = new ArrayList<>();

        for (String str : list) {
            formatted.add(format(str.replace("-", "\u2505")));
        }

        return formatted;
    }

    public String formatDate(Long val) {
        Date date = new Date(val);
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        return formatter.format(date);
    }

    /**
     * Send the Akarian No Permission Message to a user
     *
     * @param sender - CommandSender to send the message to
     */
    public void noPermission(CommandSender sender) {
        sender.sendMessage(format("&cYou do not have permission to execute this command!"));
    }

    public void debug(String str) {
        plugin.getLogger().log(Level.INFO, "DEBUG: " + str);
        alert("&4DEBUG: &e" + str);
    }

    /**
     * Send the Akarian Incorrect Usage Message to a user
     *
     * @param sender - CommandSender to send the message to
     * @param m      - Correct usage
     */
    public void usage(CommandSender sender, String m) {
        sender.sendMessage(format("&cIncorrect Usage: &7&o" + m));
    }

    /** Send a user a formatted message
     *
     * @param sender - CommandSender to send the message to
     * @param str - Message to send
     */
    public void sendRawMessage(CommandSender sender, String str) {
        sender.sendMessage(format(str));
    }

    /** Send a user a prefixed message
     *
     * @param sender - CommandSender to send the message to
     * @param str - Message to send
     */
    public void sendMessage(CommandSender sender, String str) {
        sender.sendMessage(format(prefix + " &8" + AuctionHouse.getInstance().getMessages().getPrefixIcon() + " &7" + str));
    }

    /** Broadcast a formatted message to all players
     *
     * @param str - Message to send
     */
    public void broadcastRawMessage(String str) {
        Bukkit.broadcastMessage(format(str));
    }

    /** Broadcast a prefixed message to all players
     *
     * @param str - Message to send
     */
    public void broadcastMessage(String str) {
        Bukkit.broadcastMessage(format(prefix + " &8" + AuctionHouse.getInstance().getMessages().getPrefixIcon() + " &7" + str));
    }

    /** Get the String of the given Component
     *
     * @param component - Component to get the String of
     * @return - String of the Component
     */

    /** Get the Akarian formatted name of an ItemStack
     *
     * @param itemStack - ItemStack to get the name of
     * @return - Formatted name of the ItemStack
     */
    public String formatItem(ItemStack itemStack) {
        Material material = itemStack.getType();

        if (itemStack.hasItemMeta()) {
            if (itemStack.getItemMeta().hasDisplayName()) {
                String name = itemStack.getItemMeta().getDisplayName();
                return format("&e" + itemStack.getAmount() + "x " + name);
            }
        }

        StringBuilder builder = new StringBuilder();
        for (String word : material.toString().split("_"))
            builder.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase()).append(" ");

        return format(itemStack.getAmount() + "x " + builder.toString().trim());
    }

    /** Send a message to all ops and the console
     *
     * @param str - Message to send
     */
    public void alert(String str) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.isOp()){
                sendMessage(p, str);
            }
        }
        sendMessage(Bukkit.getConsoleSender(), str);
    }

    /** Log a message to the log.txt file
     *
     * @param str - Message to log
     */
    public void log(String str) {

        DateTimeFormatter logStart = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        try {
            File saveTo = new File(plugin.getDataFolder() + "/logs", "log.txt");
            if(!(plugin.getDataFolder().isDirectory())) {
                plugin.getDataFolder().mkdir();
            }
            if (!(new File(plugin.getDataFolder() + "/logs").isDirectory())) {
                new File(plugin.getDataFolder() + "/logs").mkdir();
            }
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }

            FileWriter fw = new FileWriter(saveTo, true);

            PrintWriter pw = new PrintWriter(fw);

            pw.println("[" + logStart.format(now) + "] " + str);

            pw.flush();

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
