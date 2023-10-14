package net.akarian.auctionhouse.utils.messages;

import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MessageManager {

    private final int currentVersion;
    @Getter
    @Setter
    private String fileName;
    private final HashMap<MessageType, String> messageCache;
    private final HashMap<MessageType, List<String>> loreCache;

    public MessageManager(AuctionHouse plugin) {

        currentVersion = 1;
        messageCache = new HashMap<>();
        loreCache = new HashMap<>();

        FileManager fm = AuctionHouse.getInstance().getFileManager();
        //Load all the language files
        if (!fm.getFile("/lang/en_US").exists()) {
            fm.createFile("/lang/en_US");
            try {
                fm.copyInputStreamToFile(plugin.getResource("en_US.yml"), fm.getFile("/lang/en_US"));
                plugin.getChat().log("Loaded en_US.yml", plugin.isDebug());
                if (fm.getConfig("/lang/en_US").getInt("Version") < currentVersion) {
                    plugin.getChat().log("en_US is out of date and has been loaded. Please contact the developer.", true);
                }
            } catch (IOException ex) {
                plugin.getChat().log("Failed to load en_US.yml lang file.", true);
            }
        } else {
            plugin.getChat().log("Comparing en_US file...", plugin.isDebug());
            try {
                fm.copyInputStreamToFile(plugin.getResource("en_US.yml"), fm.getFile("/lang/en_US_temp"));
                File serverFile = fm.getFile("/lang/en_US");
                File tempFile = fm.getFile("/lang/en_US_temp");

                if (fm.compareFiles(serverFile, tempFile)) {
                    if (tempFile.delete()) {
                        plugin.getChat().log("en_US File is up to date.", plugin.isDebug());
                    }

                } else {
                    if (serverFile.delete() && tempFile.renameTo(serverFile)) {
                        plugin.getChat().log("**  en_US File has been updated!", plugin.isDebug());
                    }
                }
            } catch (IOException ex) {
                plugin.getChat().log("Failed to load en_US.yml lang file.", true);
            }
        }
        if (!fm.getFile("/lang/zh_TW").exists()) {
            try {
                fm.copyInputStreamToFile(plugin.getResource("zh_TW.yml"), fm.getFile("/lang/zh_TW"));
                plugin.getChat().log("Loaded zh_TW.yml", plugin.isDebug());
                if (fm.getConfig("/lang/zh_TW").getInt("Version") < currentVersion) {
                    plugin.getChat().log("zh_TW is out of date and has been loaded. Please contact the developer.", true);
                }
            } catch (IOException ex) {
                plugin.getChat().log("Failed to load zh_TW.yml lang file.", true);
            }
        } else {
            plugin.getChat().log("Comparing zh_TW file...", plugin.isDebug());
            try {
                fm.copyInputStreamToFile(plugin.getResource("zh_TW.yml"), fm.getFile("/lang/zh_TW_temp"));
                File serverFile = fm.getFile("/lang/zh_TW");
                File tempFile = fm.getFile("/lang/zh_TW_temp");

                if (fm.compareFiles(serverFile, tempFile)) {
                    if (tempFile.delete()) {
                        plugin.getChat().log("zh_TW File is up to date.", plugin.isDebug());
                    }

                } else {
                    if (serverFile.delete() && tempFile.renameTo(serverFile)) {
                        plugin.getChat().log("**  zh_TW File has been updated!", plugin.isDebug());
                    }
                }
            } catch (IOException ex) {
                plugin.getChat().log("Failed to load zh_TW.yml lang file.", true);
            }
        }
        if (!fm.getFile("/lang/pl_PL").exists()) {
            try {
                fm.copyInputStreamToFile(plugin.getResource("pl_PL.yml"), fm.getFile("/lang/pl_PL"));
                plugin.getChat().log("Loaded pl_PL.yml", plugin.isDebug());
                if (fm.getConfig("/lang/pl_PL").getInt("Version") < currentVersion) {
                    plugin.getChat().log("pl_PL is out of date and has been loaded. Please contact the developer.", true);
                }
            } catch (IOException ex) {
                plugin.getChat().log("Failed to load pl_PL.yml lang file.", true);
            }
        } else {
            plugin.getChat().log("Comparing pl_PL file...", plugin.isDebug());
            try {
                fm.copyInputStreamToFile(plugin.getResource("pl_PL.yml"), fm.getFile("/lang/pl_PL_temp"));
                File serverFile = fm.getFile("/lang/pl_PL");
                File tempFile = fm.getFile("/lang/pl_PL_temp");

                if (fm.compareFiles(serverFile, tempFile)) {
                    if (tempFile.delete()) {
                        plugin.getChat().log("pl_PL File is up to date.", plugin.isDebug());
                    }

                } else {
                    if (serverFile.delete() && tempFile.renameTo(serverFile)) {
                        plugin.getChat().log("**  pl_PL File has been updated!", plugin.isDebug());
                    }
                }
            } catch (IOException ex) {
                plugin.getChat().log("Failed to load pl_PL.yml lang file.", true);
            }
        }
        if (!fm.getFile("/lang/de_DE").exists()) {
            try {
                fm.copyInputStreamToFile(plugin.getResource("de_DE.yml"), fm.getFile("/lang/de_DE"));
                plugin.getChat().log("Loaded de_DE.yml", plugin.isDebug());
                if (fm.getConfig("/lang/de_DE").getInt("Version") < currentVersion) {
                    plugin.getChat().log("de_DE is out of date and has been loaded. Please contact the developer.", true);
                }
            } catch (IOException ex) {
                plugin.getChat().log("Failed to load de_DE.yml lang file.", true);
            }
        } else {
            plugin.getChat().log("Comparing de_DE file...", plugin.isDebug());
            try {
                fm.copyInputStreamToFile(plugin.getResource("de_DE.yml"), fm.getFile("/lang/de_DE_temp"));
                File serverFile = fm.getFile("/lang/de_DE");
                File tempFile = fm.getFile("/lang/de_DE_temp");

                if (fm.compareFiles(serverFile, tempFile)) {
                    if (tempFile.delete()) {
                        plugin.getChat().log("de_DE File is up to date.", plugin.isDebug());
                    }

                } else {
                    if (serverFile.delete() && tempFile.renameTo(serverFile)) {
                        plugin.getChat().log("**  de_DE File has been updated!", plugin.isDebug());
                    }
                }
            } catch (IOException ex) {
                plugin.getChat().log("Failed to load de_DE.yml lang file.", true);
            }
        }


        //Find which language file they need.
        YamlConfiguration configFile = fm.getConfig("config");
        boolean first = false;
        if (!configFile.contains("Language")) {
            String locale = Locale.getDefault().toLanguageTag().replace("-", "_");

            if (!fm.getFile("/lang/" + locale).exists()) locale = "en_US";

            configFile.set("Language", locale);
            fm.saveFile(configFile, "config");
            first = true;
        }
        fileName = configFile.getString("Language");
        if (first) {
            plugin.getChat().log("This is your first time loading the plugin and the language file \"" + fileName + "\". You can change this in-game with the command \"/aha messages\".", true);

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> plugin.getChat().alert("This is your first time loading the plugin with language files. \"&e" + fileName + "&7\" has been selected automatically. You can change this in-game with the command \"/aha messages\"."), 20 * 5);
        }

        if (!fm.getFile("messages").exists()) {
            fm.createFile("messages");
            FileUtil.copy(fm.getFile(fileName), fm.getFile("messages"));
        } else if (fm.getConfig("messages").contains("Messages.Syntax.List")) {
            transferMessagesFile();
        }

        ArrayList<String> result = compareMessageFileToLangFile();

        if (result.size() != 0) {
            plugin.getChat().log("There was a detection that " + result.size() + " message(s) from the lang file are not in your message.yml file. Fixing...", plugin.isDebug());
            YamlConfiguration langFile = fm.getConfig("/lang/" + fileName);
            YamlConfiguration messagesFile = fm.getConfig("messages");
            for (String s : result) {
                messagesFile.set(s, langFile.get(s));
            }
            fm.saveFile(messagesFile, "messages");
        }

    }

    public void clearCache() {
        messageCache.clear();
        loreCache.clear();
    }

    public ArrayList<String> compareMessageFileToLangFile() {
        FileManager fm = AuctionHouse.getInstance().getFileManager();
        YamlConfiguration lang = fm.getConfig("/lang/" + fileName);
        YamlConfiguration messages = fm.getConfig("messages");
        Set<String> langKeys = lang.getKeys(false);
        Set<String> messagesKeys = messages.getKeys(false);

        ArrayList<String> notInMessages = new ArrayList<>();

        for (String s : langKeys) {
            if (messagesKeys.contains(s)) messagesKeys.remove(s);
            else {
                notInMessages.add(s);
            }
        }
        if (messagesKeys.size() != 0) notInMessages.addAll(messagesKeys);
        return notInMessages;
    }

    /**
     * @param message      Message to send
     * @param placeholders Message Placeholders. %item%;value
     */
    public String getMessage(MessageType message, String... placeholders) {

        String langMessage;

        if (!messageCache.containsKey(message)) {

            FileManager fm = AuctionHouse.getInstance().getFileManager();
            fm.getConfig("/lang/" + fileName);
            YamlConfiguration messagesFile = fm.getConfig("messages");
            YamlConfiguration langFile = fm.getConfig("/lang/" + fileName);
            YamlConfiguration enFile = fm.getConfig("/lang/en_US");

            if (!messagesFile.contains(message.toString())) {
                langMessage = langFile.getString(message.toString());
            } else {
                langMessage = messagesFile.getString(message.toString());
            }

            if (langMessage == null) {
                langMessage = enFile.getString(message.toString());
                if (langMessage == null) {
                    AuctionHouse.getInstance().getChat().log("ERROR >> Tried to find message " + message + " but it was not found.", true);
                    return "Message Not Found";
                }
            }
            //Making the cache save with the placeholders
            messageCache.put(message, langMessage);
        } else {
            langMessage = messageCache.get(message);
        }

        for (String str : placeholders) {

            String[] split = str.split(";", 2);
            String key = split[0];
            String value = split[1];

            if (key.equalsIgnoreCase("%papi%")) {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    langMessage = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(UUID.fromString(value)), langMessage);
                }
                continue;
            }

            langMessage = langMessage.replace(key, value);


        }
        return langMessage;
    }

    /**
     * @param message      Which message to display
     * @param placeholders Placeholders to replace in the message separating variable and value with ';'
     * @return Formatted lore
     */
    public List<String> getLore(MessageType message, String... placeholders) {
        List<String> langMessage;
        if (!loreCache.containsKey(message)) {

            FileManager fm = AuctionHouse.getInstance().getFileManager();
            YamlConfiguration messagesFile = fm.getConfig("messages");
            YamlConfiguration langFile = fm.getConfig("/lang/" + fileName);
            YamlConfiguration enFile = fm.getConfig("/lang/en_US");

            if (!messagesFile.contains(message.toString())) {
                langMessage = langFile.getStringList(message.toString());
            } else {
                langMessage = messagesFile.getStringList(message.toString());
            }

            if (langMessage.isEmpty()) {
                langMessage = enFile.getStringList(message.toString());
                if (langMessage.isEmpty()) {
                    AuctionHouse.getInstance().getChat().log("ERROR >> Tried to find message " + message + " but it was not found.", true);
                    return Collections.singletonList("Message Not Found");
                }
            }
            loreCache.put(message, langMessage);
        } else {
            langMessage = loreCache.get(message);
        }

        List<String> formatted = new ArrayList<>();

        for (String main : langMessage) {
            for (String str : placeholders) {

                String[] split = str.split(";", 2);
                String key = split[0];
                String value = split[1];

                if (key.equalsIgnoreCase("%papi%")) {
                    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                        main = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(UUID.fromString(value)), main);
                    }
                    continue;
                }

                main = main.replace(key, value);
            }
            formatted.add(main);
        }
        return formatted;
    }

    /**
     * Transfers the messages.yml file from pre-1.3 to the new language files.
     */
    public void transferMessagesFile() {
        FileManager fm = AuctionHouse.getInstance().getFileManager();
        YamlConfiguration config = fm.getConfig("messages");

        /*
        config.set("", config.getString(""));
        config.set("", null);
         */

        checkMessageFileVersion();

        AuctionHouse.getInstance().getChat().log("Starting message.yml transfer...", true);

        config.set("MESSAGE_SYNTAX_LIST", config.getString("Messages.Syntax.List"));
        config.set("Messages.Syntax.List", null);

        config.set("MESSAGE_SYNTAX_SEARCH", config.getString("Messages.Syntax.Search"));
        config.set("Messages.Syntax.Search", null);

        config.set("MESSAGE_SYNTAX_SELLERTAG", config.getString("GUIs.AuctionHouse.Seller Tag"));
        config.set("GUIs.AuctionHouse.Seller Tag", null);

        config.set("MESSAGE_ERRORS_PLAYER", config.getString("Messages.Errors.Player"));
        config.set("Messages.Errors.Player", null);

        config.set("MESSAGE_ERRORS_NOITEM", config.getString("Messages.Errors.List.No Item"));
        config.set("Messages.Errors.List.No Item", null);

        config.set("MESSAGE_ERRORS_PRICE", config.getString("Messages.Errors.List.Invalid Price"));
        config.set("Messages.Errors.List.Invalid Price", null);

        config.set("MESSAGE_ERRORS_LISTINGNOTFOUND", config.getString("Messages.Errors.Listing Doesnt Exist"));
        config.set("Messages.Errors.Listing Doesnt Exist", null);

        config.set("MESSAGE_ERRORS_POOR", config.getString("Messages.Errors.Too Poor"));
        config.set("Messages.Errors.Too Poor", null);

        config.set("MESSAGE_ERRORS_NUMBER", config.getString("Messages.Errors.Valid Number"));
        config.set("Messages.Errors.Valid Number", null);

        config.set("MESSAGE_ERRORS_PERCENTAGE", config.getString("Messages.Errors.Valid Percentage"));
        config.set("Messages.Errors.Valid Percentage", null);

        config.set("MESSAGE_ERRORS_ZERO", config.getString("Messages.Errors.Greater Than Zero"));
        config.set("Messages.Errors.Greater Than Zero", null);

        config.set("MESSAGE_ERRORS_STACKSIZE", config.getString("Messages.Errors.Too Small Stack Size"));
        config.set("Messages.Errors.Too Small Stack Size", null);

        config.set("MESSAGE_GEN_PREFIX", config.getString("Messages.Prefix Icon"));
        config.set("Messages.Prefix Icon", null);

        config.set("MESSAGE_GEN_BOUGHTBUYER", config.getString("Messages.Listing.Bought.Buyer"));
        config.set("Messages.Listing.Bought.Buyer", null);

        config.set("MESSAGE_GEN_BOUGHTCREATOR", config.getString("Messages.Listing.Bought.Creator"));
        config.set("Messages.Listing.Bought.Creator", null);

        config.set("MESSAGE_GEN_LISTINGREMOVED", config.getString("Messages.Listing.Removed"));
        config.set("Messages.Listing.Removed", null);

        config.set("MESSAGE_GEN_LISTINGCREATED", config.getString("Messages.Listing.Create"));
        config.set("Messages.Listing.Create", null);

        config.set("MESSAGE_GEN_EXPIREDJOIN", config.getString("Messages.Expired Join Message"));
        config.set("Messages.Expired Join Message", null);

        config.set("MESSAGE_GEN_MINLISTINGPRICE", config.getString("Messages.Minimum Listing"));
        config.set("Messages.Minimum Listing", null);

        config.set("MESSAGE_GEN_MAXLISTINGPRICE", config.getString("Messages.Maximum Listing"));
        config.set("Messages.Maximum Listing", null);

        config.set("MESSAGE_GEN_COOLDOWN", config.getString("Messages.Cooldown Timer"));
        config.set("Messages.Cooldown Timer", null);

        config.set("MESSAGE_GEN_MAXLISTINGS", config.getString("Messages.Max Listings"));
        config.set("Messages.Max Listings", null);

        config.set("MESSAGE_GEN_EXPIREDRECLAIMED", config.getString("Messages.Expired Reclaimed"));
        config.set("Messages.Expired Reclaimed", null);

        config.set("MESSAGE_GEN_SAFEREMOVE", config.getString("Messages.Safe Remove"));
        config.set("Messages.Safe Remove", null);

        config.set("MESSAGE_GEN_POORLISTINGFEE", config.getString("Messages.Cannot Afford Listing Fee"));
        config.set("Messages.Cannot Afford Listing Fee", null);

        config.set("MESSAGE_GEN_INCOMPATIBLEPRICE", config.getString("Messages.Listing Edit Incompatible Price"));
        config.set("Messages.Listing Edit Incompatible Price", null);

        config.set("MESSAGE_GEN_RELOADSTART", config.getString("Messages.Auction House Reload"));
        config.set("Messages.Auction House Reload", null);

        config.set("MESSAGE_GEN_RELOADCOMPLETE", config.getString("Messages.Auction House Reload Complete"));
        config.set("Messages.Auction House Reload Complete", null);

        config.set("GUI_SETTINGS_ADMINMODE_NAME", config.getString("Settings.Admin Mode.Name"));
        config.set("Settings.Admin Mode.Name", null);

        config.set("GUI_SETTINGS_ADMINMODE_LORE", config.getStringList("Settings.Admin Mode.Lore"));
        config.set("Settings.Admin Mode.Lore", null);

        config.set("GUI_SETTINGS_EXPIRATION_NAME", config.getString("Settings.Expiration Notification.Name"));
        config.set("Settings.Expiration Notification.Name", null);

        config.set("GUI_SETTINGS_EXPIRATION_LORE", config.getStringList("Settings.Expiration Notification.Lore"));
        config.set("Settings.Expiration Notification.Lore", null);

        config.set("GUI_SETTINGS_EXPIRATION_MESSAGE", config.getString("Settings.Expiration Notification.Message"));
        config.set("Settings.Expiration Notification.Message", null);

        config.set("GUI_SETTINGS_EXPIRATIONTIME_NAME", config.getString("Settings.Expiration Time.Name"));
        config.set("Settings.Expiration Time.Name", null);

        config.set("GUI_SETTINGS_EXPIRATIONTIME_LORE", config.getStringList("Settings.Expiration Time.Lore"));
        config.set("Settings.Expiration Time.Lore", null);

        config.set("GUI_SETTINGS_EXPIRATIONTIME_MESSAGE", config.getString("Settings.Expiration Time.Message"));
        config.set("Settings.Expiration Time.Message", null);

        config.set("GUI_SETTINGS_LISTINGBOUGHT_NAME", config.getString("Settings.Listing Bought.Name"));
        config.set("Settings.Listing Bought.Name", null);

        config.set("GUI_SETTINGS_LISTINGBOUGHT_LORE", config.getStringList("Settings.Listing Bought.Lore"));
        config.set("Settings.Listing Bought.Lore", null);

        config.set("GUI_SETTINGS_LISTINGBOUGHT_MESSAGE", config.getString("Settings.Listing Bought.Message"));
        config.set("Settings.Listing Bought.Message", null);

        config.set("GUI_SETTINGS_SOUNDS_NAME", config.getString("Settings.Sounds.Name"));
        config.set("Settings.Sounds.Name", null);

        config.set("GUI_SETTINGS_SOUNDS_LORE", config.getStringList("Settings.Sounds.Lore"));
        config.set("Settings.Sounds.Lore", null);

        config.set("GUI_SETTINGS_LISTINGCREATED_NAME", config.getString("Settings.Listing Created.Name"));
        config.set("Settings.Listing Created.Name", null);

        config.set("GUI_SETTINGS_LISTINGCREATED_LORE", config.getStringList("Settings.Listing Created.Lore"));
        config.set("Settings.Listing Created.Lore", null);

        config.set("GUI_SETTINGS_LISTINGCREATED_MESSAGE", config.getString("Settings.Listing Created.Message"));
        config.set("Settings.Listing Created.Message", null);

        config.set("GUI_SETTINGS_AUTOCONFIRM_NAME", config.getString("Settings.Auto Confirm.Name"));
        config.set("Settings.Auto Confirm.Name", null);

        config.set("GUI_SETTINGS_AUTOCONFIRM_LORE", config.getStringList("Settings.Auto Confirm.Lore"));
        config.set("Settings.Auto Confirm.Lore", null);

        config.set("GUI_SETTINGS_LISTINGFEE_NAME", config.getString("Settings.Listing Fee.Name"));
        config.set("Settings.Listing Fee.Name", null);

        config.set("GUI_SETTINGS_LISTINGFEE_MESSAGE", config.getString("Settings.Listing Fee.Message"));
        config.set("Settings.Listing Fee.Message", null);

        config.set("GUI_SETTINGS_LISTINGFEE_MESSAGE", config.getString("Settings.Listing Fee.Lore"));
        config.set("Settings.Listing Fee.Lore", null);

        config.set("GUI_SETTINGS_SALESTAX_NAME", config.getString("Settings.Sales Tax.Name"));
        config.set("Settings.Sales Tax.Name", null);

        config.set("GUI_SETTINGS_SALESTAX_MESSAGE", config.getString("Settings.Sales Tax.Message"));
        config.set("Settings.Sales Tax.Message", null);

        config.set("GUI_SETTINGS_SALESTAX_LORE", config.getStringList("Settings.Sales Tax.Lore"));
        config.set("Settings.Sales Tax.Lore", null);

        config.set("GUI_SETTINGS_CREATIVELISTING_NAME", config.getString("Settings.Creative Listing.Name"));
        config.set("Settings.Creative Listing.Name", null);

        config.set("GUI_SETTINGS_CREATIVELISTING_MESSAGE", config.getString("Settings.Creative Listing.Message"));
        config.set("Settings.Creative Listing.Message", null);

        config.set("GUI_SETTINGS_CREATIVELISTING_LORE", config.getStringList("Settings.Creative Listing.Lore"));
        config.set("Settings.Creative Listing.Lore", null);

        config.set("GUI_SETTINGS_LISTINGTIME_NAME", config.getString("Settings.Listing Time.Name"));
        config.set("Settings.Listing Time.Name", null);

        config.set("GUI_SETTINGS_LISTINGTIME_MESSAGE", config.getString("Settings.Listing Time.Message"));
        config.set("Settings.Listing Time.Message", null);

        config.set("GUI_SETTINGS_LISTINGTIME_LORE", config.getStringList("Settings.Listing Time.Lore"));
        config.set("Settings.Listing Time.Lore", null);

        config.set("BUTTONS_PPAGE_NAME", config.getString("GUIs.Buttons.Previous Page.Name"));
        config.set("GUIs.Buttons.Previous Page.Name", null);

        config.set("BUTTONS_PPAGE_LORE", config.getStringList("GUIs.Buttons.Previous Page.Description"));
        config.set("GUIs.Buttons.Previous Page.Description", null);

        config.set("BUTTONS_PPAGE_NAME", config.getString("GUIs.Buttons.Next Page.Name"));
        config.set("GUIs.Buttons.Next Page.Name", null);

        config.set("BUTTONS_NPAGE_LORE", config.getStringList("GUIs.Buttons.Next Page.Description"));
        config.set("GUIs.Buttons.Next Page.Description", null);

        config.set("BUTTONS_CONFIRM_NAME", config.getString("GUIs.Buttons.Confirm.Name"));
        config.set("GUIs.Buttons.Confirm.Name", null);

        config.set("BUTTONS_CONFIRM_LORE", config.getStringList("GUIs.Buttons.Confirm.Description"));
        config.set("GUIs.Buttons.Confirm.Description", null);

        config.set("BUTTONS_DENY_NAME", config.getString("GUIs.Buttons.Deny.Name"));
        config.set("GUIs.Buttons.Deny.Name", null);

        config.set("BUTTONS_DENY_LORE", config.getStringList("GUIs.Buttons.Deny.Description"));
        config.set("GUIs.Buttons.Deny.Description", null);

        config.set("BUTTONS_RETURN_NAME", config.getString("GUIs.Buttons.Return.Name"));
        config.set("GUIs.Buttons.Return.Name", null);

        config.set("BUTTONS_RETURN_LORE", config.getStringList("GUIs.Buttons.Return.Description"));
        config.set("GUIs.Buttons.Return.Description", null);

        config.set("BUTTONS_CLOSE_NAME", config.getString("GUIs.AuctionHouse.Close Button.Name"));
        config.set("GUIs.AuctionHouse.Close Button.Name", null);

        config.set("BUTTONS_CLOSE_LORE", config.getStringList("GUIs.AuctionHouse.Close Button.Description"));
        config.set("GUIs.AuctionHouse.Close Button.Description", null);

        config.set("GUI_MAIN_TITLE", config.getString("GUIs.AuctionHouse.Title"));
        config.set("GUIs.AuctionHouse.Title", null);

        config.set("GUI_MAIN_SEARCH_NAME", config.getString("GUIs.AuctionHouse.Search.Name"));
        config.set("GUIs.AuctionHouse.Search.Name", null);

        config.set("GUI_MAIN_SEARCH_LORE", config.getStringList("GUIs.AuctionHouse.Search.Description"));
        config.set("GUIs.AuctionHouse.Search.Description", null);

        config.set("GUI_MAIN_SEARCH_LEFT", config.getString("GUIs.AuctionHouse.Search.Left Click"));
        config.set("GUIs.AuctionHouse.Search.Left Click", null);

        config.set("GUI_MAIN_SEARCH_RIGHT", config.getString("GUIs.AuctionHouse.Search.Right Click"));
        config.set("GUIs.AuctionHouse.Search.Right Click", null);

        config.set("GUI_MAIN_INFO_NAME", config.getString("GUIs.AuctionHouse.Info Item.Name"));
        config.set("GUIs.AuctionHouse.Info Item.Name", null);

        config.set("GUI_MAIN_INFO_LORE", config.getStringList("GUIs.AuctionHouse.Info Item.Description"));
        config.set("GUIs.AuctionHouse.Info Item.Description", null);

        config.set("GUI_MAIN_UNCLAIMED_NAME", config.getString("GUIs.AuctionHouse.Expired Listings.Name"));
        config.set("GUIs.AuctionHouse.Expired Listings.Name", null);

        config.set("GUI_MAIN_UNCLAIMED_LORE", config.getStringList("GUIs.AuctionHouse.Expired Listings.Description"));
        config.set("GUIs.AuctionHouse.Expired Listings.Description", null);

        config.set("GUI_MAIN_SORT_NAME", config.getString("GUIs.AuctionHouse.Sort.Name"));
        config.set("GUIs.AuctionHouse.Sort.Name", null);

        config.set("GUI_MAIN_SORT_LORE", config.getStringList("GUIs.AuctionHouse.Sort.Description"));
        config.set("GUIs.AuctionHouse.Sort.Description", null);

        config.set("GUI_MAIN_LISTING_ACTIVE", config.getStringList("GUIs.AuctionHouse.Listing.Description"));
        config.set("GUIs.AuctionHouse.Listing.Description", null);

        config.set("GUI_MAIN_INFO_SELLER", config.getStringList("GUIs.AuctionHouse.Listing.Seller Info"));
        config.set("GUIs.AuctionHouse.Listing.Seller Info", null);

        config.set("GUI_MAIN_INFO_BUYER", config.getStringList("GUIs.AuctionHouse.Listing.Buyer Info"));
        config.set("GUIs.AuctionHouse.Listing.Buyer Info", null);

        config.set("GUI_MAIN_LISTING_EXPIRED", config.getStringList("GUIs.AuctionHouse.Expired.Description"));
        config.set("GUIs.AuctionHouse.Expired.Description", null);

        config.set("GUI_MAIN_LISTING_EXPIRED_ADMIN", config.getStringList("GUIs.AuctionHouse.Expired.Admin Description"));
        config.set("GUIs.AuctionHouse.Expired.Admin Description", null);

        config.set("GUI_MAIN_LISTING_COMPLETED", config.getStringList("GUIs.AuctionHouse.Completed.Description"));
        config.set("GUIs.AuctionHouse.Completed.Description", null);

        config.set("GUI_MAIN_LISTING_COMPLETED_ADMIN", config.getStringList("GUIs.AuctionHouse.Completed.Admin Description"));
        config.set("GUIs.AuctionHouse.Completed.Admin Description", null);

        config.set("GUI_CONFIRMBUY_TITLE", config.getString("GUIs.Confirm Buy.Title"));
        config.set("GUIs.Confirm Buy.Title", null);

        config.set("GUI_UNCLAIMED_TITLE", config.getString("GUIs.Expire Reclaim.Title"));
        config.set("GUIs.Expire Reclaim.Title", null);

        config.set("GUI_LISTINGEDIT_TITLE", config.getString("GUIs.Listing Edit.Title"));
        config.set("GUIs.Listing Edit.Title", null);

        config.set("GUI_LISTINGEDIT_PRICE_MESSAGE", config.getString("GUIs.Listing Edit.Price.Click"));
        config.set("GUIs.Listing Edit.Price.Click", null);

        config.set("GUI_LISTINGEDIT_PRICE_NAME", config.getString("GUIs.Listing Edit.Price.Name"));
        config.set("GUIs.Listing Edit.Price.Name", null);

        config.set("GUI_LISTINGEDIT_PRICE_LORE", config.getStringList("GUIs.Listing Edit.Price.Description"));
        config.set("GUIs.Listing Edit.Price.Description", null);

        config.set("GUI_LISTINGEDIT_AMOUNT_MESSAGE", config.getString("GUIs.Listing Edit.Amount.Click"));
        config.set("GUIs.Listing Edit.Amount.Click", null);

        config.set("GUI_LISTINGEDIT_AMOUNT_NAME", config.getString("GUIs.Listing Edit.Amount.Name"));
        config.set("GUIs.Listing Edit.Amount.Name", null);

        config.set("GUI_LISTINGEDIT_AMOUNT_LORE", config.getStringList("GUIs.Listing Edit.Amount.Description"));
        config.set("GUIs.Listing Edit.Amount.Description", null);

        config.set("GUI_SHULKER_TITLE", config.getString("GUIs.Shulker View.Title"));
        config.set("GUIs.Shulker View.Title", null);

        config.set("GUI_SHULKER_LORE", config.getStringList("GUIs.Shulker View.Shulker Item"));
        config.set("GUIs.Shulker View.Shulker Item", null);

        config.set("GUI_SORT_TITLE", config.getString("GUIs.Sort.Title"));
        config.set("GUIs.Sort.Title", null);

        config.set("GUI_SORT_OVERALLPRICE_NAME", config.getString("GUIs.Sort.Phrases.Overall Price.Name"));
        config.set("GUIs.Sort.Phrases.Overall Price.Name", null);

        config.set("GUI_SORT_OVERALLPRICE_LORE", config.getStringList("GUIs.Sort.Phrases.Overall Price.Description"));
        config.set("GUIs.Sort.Phrases.Overall Price.Description", null);

        config.set("GUI_SORT_TIMELEFT_NAME", config.getString("GUIs.Sort.Phrases.Time Left.Name"));
        config.set("GUIs.Sort.Phrases.Time Left.Name", null);

        config.set("GUI_SORT_TIMELEFT_LORE", config.getStringList("GUIs.Sort.Phrases.Time Left.Description"));
        config.set("GUIs.Sort.Phrases.Time Left.Description", null);

        config.set("GUI_SORT_COSTPERITEM_NAME", config.getString("GUIs.Sort.Phrases.Cost per Item.Name"));
        config.set("GUIs.Sort.Phrases.Cost per Item.Name", null);

        config.set("GUI_SORT_COSTPERITEM_LORE", config.getStringList("GUIs.Sort.Phrases.Cost per Item.Description"));
        config.set("GUIs.Sort.Phrases.Cost per Item.Description", null);

        config.set("GUI_SORT_AMOUNTOFITEMS_NAME", config.getString("GUIs.Sort.Phrases.Amount of Items.Name"));
        config.set("GUIs.Sort.Phrases.Amount of Items.Name", null);

        config.set("GUI_SORT_AMOUNTOFITEMS_LORE", config.getStringList("GUIs.Sort.Phrases.Amount of Items.Description"));
        config.set("GUIs.Sort.Phrases.Amount of Items.Description", null);

        config.set("GUI_SORT_LONGEST", config.getString("GUIs.Sort.Phrases.Longest"));
        config.set("GUIs.Sort.Phrases.Longest", null);

        config.set("GUI_SORT_SHORTEST", config.getString("GUIs.Sort.Phrases.Shortest"));
        config.set("GUIs.Sort.Phrases.Shortest", null);

        config.set("GUI_SORT_HIGHEST", config.getString("GUIs.Sort.Phrases.Highest"));
        config.set("GUIs.Sort.Phrases.Highest", null);

        config.set("GUI_SORT_LOWEST", config.getString("GUIs.Sort.Phrases.Lowest"));
        config.set("GUIs.Sort.Phrases.Lowest", null);

        config.set("GUI_MAINADMIN_TITLE", config.getString("GUIs.Auction House Admin.Title"));
        config.set("GUIs.Auction House Admin.Title", null);

        config.set("GUI_MAINADMIN_LISTING", config.getStringList("GUIs.Auction House Admin.Listing"));
        config.set("GUIs.Auction House Admin.Listing", null);

        config.set("GUI_NPC_LORE", config.getStringList("GUIs.NPC.Lore"));
        config.set("GUIs.NPC.Lore", null);

        config.set("GUI_CONFIRMLIST_LORE", config.getStringList("GUIs.Confirm List.Display Lore"));
        config.set("GUIs.Confirm List.Display Lore", null);

        config.set("GUI_CONFIRMLIST_TITLE", config.getString("GUIs.Confirm List.Title"));
        config.set("GUIs.Confirm List.Title", null);

        config.set("GUI_SOUND_TITLE", config.getString("GUIs.Main Sound.Title"));
        config.set("GUIs.Main Sound.Title", null);

        config.set("Messages", null);
        config.set("Settings", null);
        config.set("GUIs", null);

        config.set("Version", null);

        AuctionHouse.getInstance().getChat().log("Successfully completed message.yml transfer", true);

        fm.saveFile(config, "messages");

    }

    public void checkMessageFileVersion() {

        FileManager fm = AuctionHouse.getInstance().getFileManager();
        YamlConfiguration messagesFile = fm.getConfig("messages");

        int version = messagesFile.getInt("Version");

        switch (version) {
            case 0:
                AuctionHouse.getInstance().getChat().log("Starting Message file update. Version 1", AuctionHouse.getInstance().isDebug());
                //Listing lore to add taxes and total cost

                AuctionHouse.getInstance().getChat().log("Changed Listing Lore message.", AuctionHouse.getInstance().isDebug());
                List<String> lore = new ArrayList<>();
                lore.add("&8&m&l---------------------------");
                lore.add("");
                lore.add("  &fTime Left &8&m&l-&e %time%");
                lore.add("  &fCreator &8&m&l-&e %creator%");
                lore.add("");
                lore.add("  &fPrice &8&m&l-&2 %price%");
                lore.add("  &fTax &8&m&l-&2 %tax%");
                lore.add("  &fTotal &8&m&l-&2 %total%");
                lore.add("");
                lore.add("%shulker%");
                lore.add("%self_info%");
                lore.add("");
                lore.add("&8&m&l---------------------------");
                messagesFile.set("GUIs.AuctionHouse.Listing.Description", lore);

                version = 1;
                messagesFile.set("Version", 1);
                AuctionHouse.getInstance().getChat().log("Message File update complete. Now version " + version, true);
            case 1:
                messagesFile.set("GUIs.Buttons.Next Page.Name", messagesFile.getString("GUIs.Buttons.Next Page.Name") + " &7&o(%next%/%max%)");
                messagesFile.set("GUIs.Buttons.Previous Page.Name", messagesFile.getString("GUIs.Buttons.Previous Page.Name") + " &7&o(%previous%/%max%)");
                version = 2;
                messagesFile.set("Version", 2);
                AuctionHouse.getInstance().getChat().log("Message File update complete. Now version " + version, true);
            case 2:
                messagesFile.set("GUIs.Expire Reclaim.Title", "&6&lListing Reclaim");
                messagesFile.set("GUIs.AuctionHouse.Expired Listings.Name", "&6Claim Listings");
                lore = new ArrayList<>();
                lore.add("&7&oClaim your unclaimed listings.");
                messagesFile.set("GUIs.AuctionHouse.Expired Listings.Description", lore);
                break;

        }
        fm.saveFile(messagesFile, "messages");
    }

}
