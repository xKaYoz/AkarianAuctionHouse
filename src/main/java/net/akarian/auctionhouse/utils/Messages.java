package net.akarian.auctionhouse.utils;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Messages {

    private final FileManager fm;
    @Getter
    private String incompatibleListingPrice, listingFeePoor, ahReloadComplete, ahReload, safeRemove, prefixIcon, createListing, expiredJoinMessage, gui_er_title, auctionHouseTitle, gui_aha_title, listingRemoved, listingBoughtBuyer, listingBoughtCreator, error_player, list_syntax, list_item, list_price, search_syntax, gui_ah_sn, gui_ah_sl, gui_ah_sr, gui_ah_cn, gui_buttons_ppn, gui_buttons_npn, gui_ah_st, gui_ah_in, gui_ah_en, gui_ah_stn, gui_cb_title, gui_buttons_cn, gui_buttons_dn, gui_le_pc, gui_le_ac, gui_le_title, gui_buttons_rt, gui_le_pn, gui_le_an, error_deleted, error_tooSmallStackSize, error_greaterThanZero, error_validPercentage, error_validNumber, error_poor, gui_sv_title, gui_st_title, gui_st_op, gui_st_tl, gui_st_cp, gui_st_ai, gui_st_lg, gui_st_st, gui_st_hg, gui_st_lw, minimumListing, maximumListing, cooldownTimer, expiredReclaim, maxListings,
            st_admin_name, st_expire_name, st_expireTime_name, st_created_name, st_bought_name, st_sounds_name, st_autoConfirm_name, st_expireTime_message, st_expire_message, st_bought_message, st_created_message, st_listingFee_name, st_creativeListing_name, st_listingTime_name, st_listingFee_message, st_listingTime_message, st_creativeListing_message, gui_sound_title, gui_cl_title, st_salesTax_name, st_salesTax_message;

    @Getter
    private List<String> listingLore, gui_aha_listing, gui_sv_sh, selfInfoCreator, selfInfoBuyer, gui_ah_sd, gui_ah_cd, gui_buttons_ppd, gui_buttons_npd, gui_ah_id, gui_ah_ed, gui_ah_std, gui_buttons_cd, gui_buttons_dd, gui_buttons_rd, gui_le_pd, gui_le_ad, gui_st_od, gui_st_td, gui_st_cd, gui_st_ad, expiredAdminLore, completedAdminLore, expiredLore, completedLore,
            st_admin_lore, st_expire_lore, st_expireTime_lore, st_created_lore, st_sounds_lore, st_bought_lore, st_autoConfirm_lore, gui_npc_lore, st_creativeListing_lore, st_listingFee_lore, st_listingTime_lore, gui_cl_lore, st_salesTax_lore;
    @Getter
    private YamlConfiguration messagesFile;
    @Getter
    private int version;

    public Messages() {
        fm = AuctionHouse.getInstance().getFileManager();
        if (!fm.getFile("messages").exists()) {
            fm.createFile("messages");
        }
        reloadMessages();
    }

    public void reloadMessages() {
        messagesFile = fm.getConfig("messages");

        if (!messagesFile.contains("Version")) {
            messagesFile.set("Version", 0);

        }
        version = messagesFile.getInt("Version");

        /*Messages*/
        {
            /*Syntax*/
            {
                if (!messagesFile.contains("Messages.Syntax.List")) {
                    messagesFile.set("Messages.Syntax.List", "/ah list <price>");
                }
                list_syntax = messagesFile.getString("Messages.Syntax.List");

                if (!messagesFile.contains("Messages.Syntax.Search")) {
                    messagesFile.set("Messages.Syntax.Search", "/ah search <query>");
                }
                search_syntax = messagesFile.getString("Messages.Syntax.Search");
            }
            /*Errors*/
            {
                if (!messagesFile.contains("Messages.Errors.Player")) {
                    messagesFile.set("Messages.Errors.Player", "&cYou must be a player to execute this command.");
                }
                error_player = messagesFile.getString("Messages.Errors.Player");

                if (!messagesFile.contains("Messages.Errors.List.No Item")) {
                    messagesFile.set("Messages.Errors.List.No Item", "&cYou must be holding an item.");
                }
                list_item = messagesFile.getString("Messages.Errors.List.No Item");

                if (!messagesFile.contains("Messages.Errors.List.Invalid Price")) {
                    messagesFile.set("Messages.Errors.List.Invalid Price", "&cThe price must be above $0.");
                }
                list_price = messagesFile.getString("Messages.Errors.List.Invalid Price");

                if (!messagesFile.contains("Messages.Errors.Listing Doesnt Exist")) {
                    messagesFile.set("Messages.Errors.Listing Doesnt Exist", "&cThat listing no longer exists.");
                }
                error_deleted = messagesFile.getString("Messages.Errors.Listing Doesnt Exist");

                if (!messagesFile.contains("Messages.Errors.Too Poor")) {
                    messagesFile.set("Messages.Errors.Too Poor", "&cYou do not have enough money to purchase that.");
                }
                error_poor = messagesFile.getString("Messages.Errors.Too Poor");

                if (!messagesFile.contains("Messages.Errors.Valid Number")) {
                    messagesFile.set("Messages.Errors.Valid Number", "&cYou must provide a valid number.");
                }
                error_validNumber = messagesFile.getString("Messages.Errors.Valid Number");

                if (!messagesFile.contains("Messages.Errors.Valid Percentage")) {
                    messagesFile.set("Messages.Errors.Valid Percentage", "&cYou must provide a valid percentage.");
                }
                error_validPercentage = messagesFile.getString("Messages.Errors.Valid Percentage");

                if (!messagesFile.contains("Messages.Errors.Greater Than Zero")) {
                    messagesFile.set("Messages.Errors.Greater Than Zero", "&cThe amount must be greater than 0.");
                }
                error_greaterThanZero = messagesFile.getString("Messages.Errors.Greater Than Zero");

                if (!messagesFile.contains("Messages.Errors.Too Small Stack Size")) {
                    messagesFile.set("Messages.Errors.Too Small Stack Size", "&cThe amount must be less than the max stack size of the item.");
                }
                error_tooSmallStackSize = messagesFile.getString("Messages.Errors.Too Small Stack Size");

            }
            /*Settings*/
            {
                //Admin Button
                if(!messagesFile.contains("Settings.Admin Mode.Name")) {
                    messagesFile.set("Settings.Admin Mode.Name", "&eAdmin Mode");
                }
                st_admin_name = messagesFile.getString("Settings.Admin Mode.Name");

                if(!messagesFile.contains("Settings.Admin Mode.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Open the auction house in admin mode automatically.");
                    lore.add("%status%");
                    messagesFile.set("Settings.Admin Mode.Lore", lore);
                }
                st_admin_lore = messagesFile.getStringList("Settings.Admin Mode.Lore");

                //Expiration Button
                if(!messagesFile.contains("Settings.Expiration Notification.Name")) {
                    messagesFile.set("Settings.Expiration Notification.Name", "&eNotify when near expiration");
                }
                st_expire_name = messagesFile.getString("Settings.Expiration Notification.Name");

                if(!messagesFile.contains("Settings.Expiration Notification.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Notify you when an auction is about to expire.");
                    lore.add("%status%");
                    messagesFile.set("Settings.Expiration Notification.Lore", lore);
                }
                st_expire_lore = messagesFile.getStringList("Settings.Expiration Notification.Lore");

                if(!messagesFile.contains("Settings.Expiration Notification.Message")) {
                    messagesFile.set("Settings.Expiration Notification.Message", "&e%seller%&f's listing of &e%listing% &fis about to expire in &e%time%&f.");
                }
                st_expire_message = messagesFile.getString("Settings.Expiration Notification.Message");

                //Expiration Time Button
                if(!messagesFile.contains("Settings.Expiration Time.Name")) {
                    messagesFile.set("Settings.Expiration Time.Name", "&eWhen to notify");
                }
                st_expireTime_name = messagesFile.getString("Settings.Expiration Time.Name");

                if(!messagesFile.contains("Settings.Expiration Time.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Select how long an auction should have");
                    lore.add("&7left before you are notified.");
                    lore.add("&7Currently: &e%time%");
                    messagesFile.set("Settings.Expiration Time.Lore", lore);
                }
                st_expireTime_lore = messagesFile.getStringList("Settings.Expiration Time.Lore");

                if(!messagesFile.contains("Settings.Expiration Time.Message")) {
                    messagesFile.set("Settings.Expiration Time.Message", "&eEnter how many seconds should be left on an auction before you are notified. ");
                }
                st_expireTime_message = messagesFile.getString("Settings.Expiration Time.Message");

                //Listing Bought Button
                if(!messagesFile.contains("Settings.Listing Bought.Name")) {
                    messagesFile.set("Settings.Listing Bought.Name", "&eNotify when bought");
                }
                st_bought_name = messagesFile.getString("Settings.Listing Bought.Name");

                if(!messagesFile.contains("Settings.Listing Bought.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Notify you when an auction is bought.");
                    lore.add("%status%");
                    messagesFile.set("Settings.Listing Bought.Lore", lore);
                }
                st_bought_lore = messagesFile.getStringList("Settings.Listing Bought.Lore");

                if (!messagesFile.contains("Settings.Listing Bought.Message")) {
                    messagesFile.set("Settings.Listing Bought.Message", "&e%buyer%&f has bought &e%seller%&f's listing of &e%listing%&f for &2$%price%&f.");
                }
                st_bought_message = messagesFile.getString("Settings.Listing Bought.Message");

                //Sound Button
                if (!messagesFile.contains("Settings.Sounds.Name")) {
                    messagesFile.set("Settings.Sounds.Name", "&eToggle sounds");
                }
                st_sounds_name = messagesFile.getString("Settings.Sounds.Name");

                if (!messagesFile.contains("Settings.Sounds.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Toggle sounds when interacting with the Auction House.");
                    lore.add("%status%");
                    messagesFile.set("Settings.Sounds.Lore", lore);
                }
                st_sounds_lore = messagesFile.getStringList("Settings.Sounds.Lore");

                //Listing Created Button
                if (!messagesFile.contains("Settings.Listing Created.Name")) {
                    messagesFile.set("Settings.Listing Created.Name", "&eNotify when created");
                }
                st_created_name = messagesFile.getString("Settings.Listing Created.Name");

                if (!messagesFile.contains("Settings.Listing Created.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Notify you when an auction is created.");
                    lore.add("%status%");
                    messagesFile.set("Settings.Listing Created.Lore", lore);
                }
                st_created_lore = messagesFile.getStringList("Settings.Listing Created.Lore");

                if(!messagesFile.contains("Settings.Listing Created.Message")) {
                    messagesFile.set("Settings.Listing Created.Message", "&e%seller%&f has created a listing for &e%listing%&f for &2%price%&f.");
                }
                st_created_message = messagesFile.getString("Settings.Listing Created.Message");

                //Auto Confirm Button
                if(!messagesFile.contains("Settings.Auto Confirm.Name")) {
                    messagesFile.set("Settings.Auto Confirm.Name", "&eAutomatically confirm listings");
                }
                st_autoConfirm_name = messagesFile.getString("Settings.Auto Confirm.Name");

                if(!messagesFile.contains("Settings.Auto Confirm.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Automatically confirm the creation of your listing.");
                    lore.add("%status%");
                    messagesFile.set("Settings.Auto Confirm.Lore", lore);
                }
                st_autoConfirm_lore = messagesFile.getStringList("Settings.Auto Confirm.Lore");

                //Listing Fee Button
                if(!messagesFile.contains("Settings.Listing Fee.Name")) {
                    messagesFile.set("Settings.Listing Fee.Name", "&eListing Fee");
                }
                st_listingFee_name = messagesFile.getString("Settings.Listing Fee.Name");

                if(!messagesFile.contains("Settings.Listing Fee.Message")) {
                    messagesFile.set("Settings.Listing Fee.Message", "&eEnter the new listing fee or left click to cancel.");
                }
                st_listingFee_message = messagesFile.getString("Settings.Listing Fee.Message");

                if (!messagesFile.contains("Settings.Listing Fee.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Change the listing fee amount or percentage.");
                    lore.add("&fCurrent Fee: &e%fee%");
                    messagesFile.set("Settings.Listing Fee.Lore", lore);
                }
                st_listingFee_lore = messagesFile.getStringList("Settings.Listing Fee.Lore");

                //Sales Tax Button
                if (!messagesFile.contains("Settings.Sales Tax.Name")) {
                    messagesFile.set("Settings.Sales Tax.Name", "&eSales Tax");
                }
                st_salesTax_name = messagesFile.getString("Settings.Sales Tax.Name");

                if (!messagesFile.contains("Settings.Sales Tax.Message")) {
                    messagesFile.set("Settings.Sales Tax.Message", "&eEnter the new sales tax or left click to cancel.");
                }
                st_salesTax_message = messagesFile.getString("Settings.Sales Tax.Message");

                if (!messagesFile.contains("Settings.Sales Tax.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Change the sales tax amount or percentage.");
                    lore.add("&fCurrent tax: &e%tax%");
                    messagesFile.set("Settings.Sales Tax.Lore", lore);
                }
                st_salesTax_lore = messagesFile.getStringList("Settings.Sales Tax.Lore");

                //Creative Listing Button
                if (!messagesFile.contains("Settings.Creative Listing.Name")) {
                    messagesFile.set("Settings.Creative Listing.Name", "&eCreative Listing");
                }
                st_creativeListing_name = messagesFile.getString("Settings.Creative Listing.Name");

                if (!messagesFile.contains("Settings.Creative Listing.Message")) {
                    messagesFile.set("Settings.Creative Listing.Message", "&cYou cannot create a listing while in creative mode.");
                }
                st_creativeListing_message = messagesFile.getString("Settings.Creative Listing.Message");

                if(!messagesFile.contains("Settings.Creative Listing.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Allow players to list items while in creative.");
                    lore.add("%status%");
                    messagesFile.set("Settings.Creative Listing.Lore", lore);
                }
                st_creativeListing_lore = messagesFile.getStringList("Settings.Creative Listing.Lore");

                //Listing Time Button
                if(!messagesFile.contains("Settings.Listing Time.Name")) {
                    messagesFile.set("Settings.Listing Time.Name", "&eListing Time");
                }
                st_listingTime_name = messagesFile.getString("Settings.Listing Time.Name");

                if(!messagesFile.contains("Settings.Listing Time.Message")) {
                    messagesFile.set("Settings.Listing Time.Message", "&eEnter the new listing time in seconds or left click to cancel.");
                }
                st_listingTime_message = messagesFile.getString("Settings.Listing Time.Message");

                if(!messagesFile.contains("Settings.Listing Time.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Change the amount of time a listing will be posted for.");
                    lore.add("&fCurrent Time: &e%time%");
                    messagesFile.set("Settings.Listing Time.Lore", lore);
                }
                st_listingTime_lore = messagesFile.getStringList("Settings.Listing Time.Lore");

            }
            /*Core*/
            {
                if (!messagesFile.contains("Messages.Prefix Icon")) {
                    messagesFile.set("Messages.Prefix Icon", "»");
                }
                prefixIcon = messagesFile.getString("Messages.Prefix Icon");
                if (!messagesFile.contains("Messages.Listing.Bought.Buyer")) {
                    messagesFile.set("Messages.Listing.Bought.Buyer", "&fYou have bought &e%item%&f for &2%price%&f.");
                }
                listingBoughtBuyer = messagesFile.getString("Messages.Listing.Bought.Buyer");

                if (!messagesFile.contains("Messages.Listing.Bought.Creator")) {
                    messagesFile.set("Messages.Listing.Bought.Creator", "&c%buyer%&f has bought &e%item%&f for &2%price%&f.");
                }
                listingBoughtCreator = messagesFile.getString("Messages.Listing.Bought.Creator");

                if (!messagesFile.contains("Messages.Listing.Removed")) {
                    messagesFile.set("Messages.Listing.Removed", "&fYour listing for &e%item%&f has been removed.");
                }
                listingRemoved = messagesFile.getString("Messages.Listing.Removed");

                if (!messagesFile.contains("Messages.Listing.Create")) {
                    messagesFile.set("Messages.Listing.Create", "&fYou have created an auction for &e%item%&f with the price of &2%price%&f.");
                }
                createListing = messagesFile.getString("Messages.Listing.Create");

                if (!messagesFile.contains("Messages.Expired Join Message")) {
                    messagesFile.set("Messages.Expired Join Message", "&fYou have &e%amount%x Expired Listings&f waiting for you to reclaim. Use &7&o/ah expired&f to reclaim.");
                }
                expiredJoinMessage = messagesFile.getString("Messages.Expired Join Message");

                if (!messagesFile.contains("Messages.Minimum Listing")) {
                    messagesFile.set("Messages.Minimum Listing", "&cYour listing price is too low! It must be above %price%.");
                }
                minimumListing = messagesFile.getString("Messages.Minimum Listing");

                if (!messagesFile.contains("Messages.Maximum Listing")) {
                    messagesFile.set("Messages.Maximum Listing", "&cYour listing price is too high! It must be below %price%.");
                }
                maximumListing = messagesFile.getString("Messages.Maximum Listing");

                if (!messagesFile.contains("Messages.Cooldown Timer")) {
                    messagesFile.set("Messages.Cooldown Timer", "&eYou have %time% remaining before you can make another listing.");
                }
                cooldownTimer = messagesFile.getString("Messages.Cooldown Timer");

                if (!messagesFile.contains("Messages.Max Listings")) {
                    messagesFile.set("Messages.Max Listings", "&cError! You can only have a maximum of %max% listings.");
                }
                maxListings = messagesFile.getString("Messages.Max Listings");

                if (!messagesFile.contains("Messages.Expired Reclaimed")) {
                    messagesFile.set("Messages.Expired Reclaimed", "&fYou have reclaimed your listing of &e%item%&f.");
                }
                expiredReclaim = messagesFile.getString("Messages.Expired Reclaimed");

                if (!messagesFile.contains("Messages.Safe Remove")) {
                    messagesFile.set("Messages.Safe Remove", "&fYou have successfully removed the listing of &e%item%&f.");
                }
                safeRemove = messagesFile.getString("Messages.Safe Remove");

                //Cannot afford listing fee Message
                if (!messagesFile.contains("Messages.Cannot Afford Listing Fee")) {
                    messagesFile.set("Messages.Cannot Afford Listing Fee", "&cYou do not have enough money to cover the listing fee.");
                }
                listingFeePoor = messagesFile.getString("Messages.Cannot Afford Listing Fee");

                //Listing Edit Incompatible Price
                if (!messagesFile.contains("Messages.Listing Edit Incompatible Price")) {
                    messagesFile.set("Messages.Listing Edit Incompatible Price", "&cYou must provide a compatible price.");
                }
                incompatibleListingPrice = messagesFile.getString("Messages.Listing Edit Incompatible Price");

                //Auction House Reload Message
                if (!messagesFile.contains("Messages.Auction House Reload")) {
                    messagesFile.set("Messages.Auction House Reload", "&7Reloading...");
                }
                ahReload = messagesFile.getString("Messages.Auction House Reload");

                //Auction House Reload Complete Message
                if (!messagesFile.contains("Messages.Auction House Reload Complete")) {
                    messagesFile.set("Messages.Auction House Reload Complete", "&aReload complete.");
                }
                ahReloadComplete = messagesFile.getString("Messages.Auction House Reload Complete");
            }
        }

        /*GUIs*/
        {
            /*General Buttons*/
            {
                if (!messagesFile.contains("GUIs.Buttons.Previous Page.Name")) {
                    messagesFile.set("GUIs.Buttons.Previous Page.Name", "&6Previous Page");
                }
                gui_buttons_ppn = messagesFile.getString("GUIs.Buttons.Previous Page.Name");

                if (!messagesFile.contains("GUIs.Buttons.Previous Page.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Go to the previous page.");
                    messagesFile.set("GUIs.Buttons.Previous Page.Description", lore);
                }
                gui_buttons_ppd = messagesFile.getStringList("GUIs.Buttons.Previous Page.Description");

                if (!messagesFile.contains("GUIs.Buttons.Next Page.Name")) {
                    messagesFile.set("GUIs.Buttons.Next Page.Name", "&6Next Page");
                }
                gui_buttons_npn = messagesFile.getString("GUIs.Buttons.Next Page.Name");

                if (!messagesFile.contains("GUIs.Buttons.Next Page.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Go to the next page.");
                    messagesFile.set("GUIs.Buttons.Next Page.Description", lore);
                }
                gui_buttons_npd = messagesFile.getStringList("GUIs.Buttons.Next Page.Description");

                if (!messagesFile.contains("GUIs.Buttons.Confirm.Name")) {
                    messagesFile.set("GUIs.Buttons.Confirm.Name", "&a&lConfirm");
                }
                gui_buttons_cn = messagesFile.getString("GUIs.Buttons.Confirm.Name");

                if (!messagesFile.contains("GUIs.Buttons.Confirm.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Click to confirm.");
                    messagesFile.set("GUIs.Buttons.Confirm.Description", lore);
                }
                gui_buttons_cd = messagesFile.getStringList("GUIs.Buttons.Confirm.Description");

                if (!messagesFile.contains("GUIs.Buttons.Deny.Name")) {
                    messagesFile.set("GUIs.Buttons.Deny.Name", "&c&lCancel");
                }
                gui_buttons_dn = messagesFile.getString("GUIs.Buttons.Deny.Name");

                if (!messagesFile.contains("GUIs.Buttons.Deny.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Click to cancel.");
                    messagesFile.set("GUIs.Buttons.Deny.Description", lore);
                }
                gui_buttons_dd = messagesFile.getStringList("GUIs.Buttons.Deny.Description");

                if (!messagesFile.contains("GUIs.Buttons.Return.Name")) {
                    messagesFile.set("GUIs.Buttons.Return.Name", "&c&lReturn");
                }
                gui_buttons_rt = messagesFile.getString("GUIs.Buttons.Return.Name");

                if (!messagesFile.contains("GUIs.Buttons.Return.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7&oGo back to the previous page.");
                    messagesFile.set("GUIs.Buttons.Return.Description", lore);
                } else {
                    if (messagesFile.getStringList("GUIs.Buttons.Return.Description").size() == 1) {
                        if (messagesFile.getStringList("GUIs.Buttons.Return.Description").get(0).equalsIgnoreCase("&7&oReturn the AuctionHouse.")) {
                            List<String> lore = new ArrayList<>();
                            lore.add("&7&oGo back to the previous page.");
                            messagesFile.set("GUIs.Buttons.Return.Description", lore);
                        }
                    }
                }
                gui_buttons_rd = messagesFile.getStringList("GUIs.Buttons.Return.Description");
            }
            /*Auction House*/
            {

                if (!messagesFile.contains("GUIs.AuctionHouse.Title")) {
                    if (messagesFile.contains("Auction House Title")) {
                        messagesFile.set("GUIs.AuctionHouse.Title", messagesFile.getString("Auction House Title"));
                        messagesFile.set("Auction House Title", null);
                    } else messagesFile.set("GUIs.AuctionHouse.Title", "&6&lAuction&f&lHouse");
                }
                auctionHouseTitle = messagesFile.getString("GUIs.AuctionHouse.Title");

                if (!messagesFile.contains("GUIs.AuctionHouse.Search.Name")) {
                    messagesFile.set("GUIs.AuctionHouse.Search.Name", "&6Search");
                }
                gui_ah_sn = messagesFile.getString("GUIs.AuctionHouse.Search.Name");

                if (!messagesFile.contains("GUIs.AuctionHouse.Search.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Search for specific listings.");
                    messagesFile.set("GUIs.AuctionHouse.Search.Description", lore);
                }
                gui_ah_sd = messagesFile.getStringList("GUIs.AuctionHouse.Search.Description");

                if (!messagesFile.contains("GUIs.AuctionHouse.Search.Left Click")) {
                    messagesFile.set("GUIs.AuctionHouse.Search.Left Click", "&eLeft click to clear");
                }
                gui_ah_sl = messagesFile.getString("GUIs.AuctionHouse.Search.Left Click");

                if (!messagesFile.contains("GUIs.AuctionHouse.Search.Right Click")) {
                    messagesFile.set("GUIs.AuctionHouse.Search.Right Click", "&eEnter your search query...");
                }
                gui_ah_sr = messagesFile.getString("GUIs.AuctionHouse.Search.Right Click");

                if (!messagesFile.contains("GUIs.AuctionHouse.Close Button.Name")) {
                    messagesFile.set("GUIs.AuctionHouse.Close Button.Name", "&c&lClose");
                }
                gui_ah_cn = messagesFile.getString("GUIs.AuctionHouse.Close Button.Name");

                if (!messagesFile.contains("GUIs.AuctionHouse.Close Button.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7&oClose the menu.");
                    messagesFile.set("GUIs.AuctionHouse.Close Button.Description", lore);
                }
                gui_ah_cd = messagesFile.getStringList("GUIs.AuctionHouse.Close Button.Description");

                if (!messagesFile.contains("GUIs.AuctionHouse.Info Item.Name")) {
                    messagesFile.set("GUIs.AuctionHouse.Info Item.Name", "&6Information");
                }
                gui_ah_in = messagesFile.getString("GUIs.AuctionHouse.Info Item.Name");

                if (!messagesFile.contains("GUIs.AuctionHouse.Info Item.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&8&m&l-----------------------------------");
                    lore.add("");
                    lore.add("  &7Welcome to the Auction House!");
                    lore.add("  &7There are &e%items% &7for sale.");
                    lore.add("  &7You can list an item on the auction house with");
                    lore.add("  &e/ah list <price>");
                    lore.add("");
                    lore.add("  &7Your Balance &8&m&l-&e %balance%");
                    lore.add("");
                    lore.add("&8&m&l-----------------------------------");
                    messagesFile.set("GUIs.AuctionHouse.Info Item.Description", lore);
                }
                gui_ah_id = messagesFile.getStringList("GUIs.AuctionHouse.Info Item.Description");

                if (!messagesFile.contains("GUIs.AuctionHouse.Expired Listings.Name")) {
                    messagesFile.set("GUIs.AuctionHouse.Expired Listings.Name", "&6Expired Listings");
                }
                gui_ah_en = messagesFile.getString("GUIs.AuctionHouse.Expired Listings.Name");

                if (!messagesFile.contains("GUIs.AuctionHouse.Expired Listings.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7&oReclaim your expired listings.");
                    messagesFile.set("GUIs.AuctionHouse.Expired Listings.Description", lore);
                }
                gui_ah_ed = messagesFile.getStringList("GUIs.AuctionHouse.Expired Listings.Description");

                if (!messagesFile.contains("GUIs.AuctionHouse.Sort.Name")) {
                    messagesFile.set("GUIs.AuctionHouse.Sort.Name", "&6Sort");
                }
                gui_ah_stn = messagesFile.getString("GUIs.AuctionHouse.Sort.Name");

                if (!messagesFile.contains("GUIs.AuctionHouse.Sort.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7&oSort the listings.");
                    messagesFile.set("GUIs.AuctionHouse.Sort.Description", lore);
                }
                gui_ah_std = messagesFile.getStringList("GUIs.AuctionHouse.Sort.Description");

                if (!messagesFile.contains("GUIs.AuctionHouse.Listing.Description")) {
                    if (messagesFile.contains("Auction House Listing")) {
                        //Compatibility from v.0.0.5 to 0.0.6
                        messagesFile.set("GUIs.AuctionHouse.Listing", messagesFile.getStringList("Auction House Listing"));
                        messagesFile.set("Auction House Listing", null);
                    } else {
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
                    }
                }
                listingLore = messagesFile.getStringList("GUIs.AuctionHouse.Listing.Description");

                if (!messagesFile.contains("GUIs.AuctionHouse.Listing.Seller Info")) {
                    List<String> input = new ArrayList<>();
                    input.add("  &c&oLeft Click to edit");
                    input.add("  &c&oShift + Right Click to remove");
                    messagesFile.set("GUIs.AuctionHouse.Listing.Seller Info", input);
                }
                selfInfoCreator = messagesFile.getStringList("GUIs.AuctionHouse.Listing.Seller Info");

                if (!messagesFile.contains("GUIs.AuctionHouse.Listing.Buyer Info")) {
                    List<String> input = new ArrayList<>();
                    input.add("  &7&oClick to purchase");
                    messagesFile.set("GUIs.AuctionHouse.Listing.Buyer Info", input);
                }
                selfInfoBuyer = messagesFile.getStringList("GUIs.AuctionHouse.Listing.Buyer Info");

                if (!messagesFile.contains("GUIs.AuctionHouse.Expired.Description")) {

                    List<String> lore = new ArrayList<>();
                    lore.add("&8&m&l---------------------------");
                    lore.add("");
                    lore.add("  &fStarted &8&m&l-&e %start%");
                    lore.add("  &fExpired &8&m&l-&e %end%");
                    lore.add("  &fPrice &8&m&l-&2 %price%");
                    lore.add("");
                    lore.add("  &7Click to reclaim item.");
                    lore.add("");
                    lore.add("&8&m&l---------------------------");
                    messagesFile.set("GUIs.AuctionHouse.Expired.Description", lore);

                }
                expiredLore = messagesFile.getStringList("GUIs.AuctionHouse.Expired.Description");

                if (!messagesFile.contains("GUIs.AuctionHouse.Expired.Admin Description")) {

                    List<String> lore = new ArrayList<>();
                    lore.add("&8&m&l---------------------------------");
                    lore.add("");
                    lore.add("  &fStarted &8&m&l-&e %start%");
                    lore.add("  &fExpired &8&m&l-&e %end%");
                    lore.add("  &fPrice &8&m&l-&2 %price%");
                    lore.add("  &fReclaimed &8&m&l-&e %reclaimed%");
                    lore.add("%shulker%");
                    lore.add("");
                    lore.add("  &7Shift + Right Click to remove listing.");
                    lore.add("");
                    lore.add("&8&m&l---------------------------------");
                    messagesFile.set("GUIs.AuctionHouse.Expired.Admin Description", lore);

                }
                expiredAdminLore = messagesFile.getStringList("GUIs.AuctionHouse.Expired.Admin Description");

                if (!messagesFile.contains("GUIs.AuctionHouse.Completed.Admin Description")) {

                    List<String> lore = new ArrayList<>();
                    lore.add("&8&m&l---------------------------------");
                    lore.add("");
                    lore.add("  &fStarted &8&m&l-&e %start%");
                    lore.add("  &fBought &8&m&l-&e %end%");
                    lore.add("  &fPrice &8&m&l-&2 %price%");
                    lore.add("  &fBuyer &8&m&l-&e %buyer%");
                    lore.add("");
                    lore.add("%shulker%");
                    lore.add("  &7Shift + Right Click to remove and refund listing.");
                    lore.add("");
                    lore.add("&8&m&l---------------------------------");
                    messagesFile.set("GUIs.AuctionHouse.Completed.Admin Description", lore);

                }
                completedAdminLore = messagesFile.getStringList("GUIs.AuctionHouse.Completed.Admin Description");

                //Version 2
                if (!messagesFile.contains("GUIs.AuctionHouse.Completed.Description")) {

                    List<String> lore = new ArrayList<>();
                    lore.add("&8&m&l---------------------------------");
                    lore.add("");
                    lore.add("  &fStarted &8&m&l-&e %start%");
                    lore.add("  &fBought &8&m&l-&e %end%");
                    lore.add("  &fPrice &8&m&l-&2 %price%");
                    lore.add("  &fSeller &8&m&l-&e %seller%");
                    lore.add("");
                    lore.add("%shulker%");
                    lore.add("  &7Left click to claim listing.");
                    lore.add("");
                    lore.add("&8&m&l---------------------------------");
                    messagesFile.set("GUIs.AuctionHouse.Completed.Description", lore);

                }
                completedLore = messagesFile.getStringList("GUIs.AuctionHouse.Completed.Description");

                if (!messagesFile.contains("GUIs.AuctionHouse.Seller Tag")) {
                    messagesFile.set("GUIs.AuctionHouse.Seller Tag", "seller");
                }
                gui_ah_st = messagesFile.getString("GUIs.AuctionHouse.Seller Tag");

            }
            /*Confirm Buy*/
            {
                if (!messagesFile.contains("GUIs.Confirm Buy.Title")) {
                    messagesFile.set("GUIs.Confirm Buy.Title", "&eAre you sure???");
                }
                gui_cb_title = messagesFile.getString("GUIs.Confirm Buy.Title");
            }
            /*Expire Reclaim*/
            {
                if (!messagesFile.contains("GUIs.Expire Reclaim.Title")) {
                    messagesFile.set("GUIs.Expire Reclaim.Title", "&c&lExpired Item &f&lReclaim");
                }
                gui_er_title = messagesFile.getString("GUIs.Expire Reclaim.Title");
            }
            /*Listing Edit*/
            {
                if (!messagesFile.contains("GUIs.Listing Edit.Title")) {
                    messagesFile.set("GUIs.Listing Edit.Title", "&eEdit your listing");
                }
                gui_le_title = messagesFile.getString("GUIs.Listing Edit.Title");
                if (!messagesFile.contains("GUIs.Listing Edit.Price.Click")) {
                    messagesFile.set("GUIs.Listing Edit.Price.Click", "&eEnter the new price of your auction or left click to cancel.");
                }
                gui_le_pc = messagesFile.getString("GUIs.Listing Edit.Price.Click");

                if (!messagesFile.contains("GUIs.Listing Edit.Price.Name")) {
                    messagesFile.set("GUIs.Listing Edit.Price.Name", "&6Price");
                }
                gui_le_pn = messagesFile.getString("GUIs.Listing Edit.Price.Name");

                if (!messagesFile.contains("GUIs.Listing Edit.Price.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&7&oClick to edit the price.");
                    messagesFile.set("GUIs.Listing Edit.Price.Description", lore);
                }
                gui_le_pd = messagesFile.getStringList("GUIs.Listing Edit.Price.Description");

                if (!messagesFile.contains("GUIs.Listing Edit.Amount.Name")) {
                    messagesFile.set("GUIs.Listing Edit.Amount.Name", "&6Amount");
                }
                gui_le_an = messagesFile.getString("GUIs.Listing Edit.Amount.Name");

                if (!messagesFile.contains("GUIs.Listing Edit.Amount.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&eIf you enter a higher amount than the current listing,");
                    lore.add("&eyou must have the items in your inventory.");
                    lore.add("&eIf you enter a lower amount than the current listing,");
                    lore.add("&eyou will be given the items you removed.");
                    lore.add(" ");
                    lore.add("&7&oClick to remove or add items to this listing.");
                    messagesFile.set("GUIs.Listing Edit.Amount.Description", lore);
                }
                gui_le_ad = messagesFile.getStringList("GUIs.Listing Edit.Amount.Description");

                if (!messagesFile.contains("GUIs.Listing Edit.Amount.Click")) {
                    messagesFile.set("GUIs.Listing Edit.Amount.Click", "&eEnter the new amount of your auction or left click to cancel.");
                }
                gui_le_ac = messagesFile.getString("GUIs.Listing Edit.Amount.Click");
            }
            /*Shulker View*/
            {
                if (!messagesFile.contains("GUIs.Shulker View.Title")) {
                    messagesFile.set("GUIs.Shulker View.Title", "&eAre you sure???");
                }
                gui_sv_title = messagesFile.getString("GUIs.Shulker View.Title");

                if (!messagesFile.contains("GUIs.Shulker View.Shulker Item")) {
                    List<String> input = new ArrayList<>();
                    input.add(" &fThere are &e%amount%&f items in this box.");
                    input.add("");
                    messagesFile.set("GUIs.Shulker View.Shulker Item", input);
                }
                gui_sv_sh = messagesFile.getStringList("GUIs.Shulker View.Shulker Item");
            }
            /*Sort*/
            {
                if (!messagesFile.contains("GUIs.Sort.Title")) {
                    messagesFile.set("GUIs.Sort.Title", "&eSort Menu");
                }
                gui_st_title = messagesFile.getString("GUIs.Sort.Title");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Overall Price.Name")) {
                    messagesFile.set("GUIs.Sort.Phrases.Overall Price.Name", "Overall Price");
                }
                gui_st_op = messagesFile.getString("GUIs.Sort.Phrases.Overall Price.Name");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Overall Price.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("Click to enable sorting by the overall price");
                    messagesFile.set("GUIs.Sort.Phrases.Overall Price.Description", lore);
                }
                gui_st_od = messagesFile.getStringList("GUIs.Sort.Phrases.Overall Price.Description");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Time Left.Name")) {
                    messagesFile.set("GUIs.Sort.Phrases.Time Left.Name", "Time Left");
                }
                gui_st_tl = messagesFile.getString("GUIs.Sort.Phrases.Time Left.Name");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Time Left.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("Click to enable sorting by the time remaining.");
                    messagesFile.set("GUIs.Sort.Phrases.Time Left.Description", lore);
                }
                gui_st_td = messagesFile.getStringList("GUIs.Sort.Phrases.Time Left.Description");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Cost per Item.Name")) {
                    messagesFile.set("GUIs.Sort.Phrases.Cost per Item.Name", "Cost per Item");
                }
                gui_st_cp = messagesFile.getString("GUIs.Sort.Phrases.Cost per Item.Name");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Cost per Item.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("Click to enable sorting by the cost of each item.");
                    messagesFile.set("GUIs.Sort.Phrases.Cost per Item.Description", lore);
                }
                gui_st_cd = messagesFile.getStringList("GUIs.Sort.Phrases.Cost per Item.Description");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Amount of Items.Name")) {
                    messagesFile.set("GUIs.Sort.Phrases.Amount of Items.Name", "Amount of Items");
                }
                gui_st_ai = messagesFile.getString("GUIs.Sort.Phrases.Amount of Items.Name");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Amount of Items.Description")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("Click to enable sorting by the amount of items in the listing.");
                    messagesFile.set("GUIs.Sort.Phrases.Amount of Items.Description", lore);
                }
                gui_st_ad = messagesFile.getStringList("GUIs.Sort.Phrases.Amount of Items.Description");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Longest")) {
                    messagesFile.set("GUIs.Sort.Phrases.Longest", "Longest");
                }
                gui_st_lg = messagesFile.getString("GUIs.Sort.Phrases.Longest");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Shortest")) {
                    messagesFile.set("GUIs.Sort.Phrases.Shortest", "Shortest");
                }
                gui_st_st = messagesFile.getString("GUIs.Sort.Phrases.Shortest");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Highest")) {
                    messagesFile.set("GUIs.Sort.Phrases.Highest", "Highest");
                }
                gui_st_hg = messagesFile.getString("GUIs.Sort.Phrases.Highest");

                if (!messagesFile.contains("GUIs.Sort.Phrases.Lowest")) {
                    messagesFile.set("GUIs.Sort.Phrases.Lowest", "Lowest");
                }
                gui_st_lw = messagesFile.getString("GUIs.Sort.Phrases.Lowest");

            }
            /* Auction House Admin*/
            {
                if (!messagesFile.contains("GUIs.Auction House Admin.Title")) {
                    messagesFile.set("GUIs.Auction House Admin.Title", "&6&lAuctionHouse Admin Menu");
                }
                gui_aha_title = messagesFile.getString("GUIs.Auction House Admin.Title");

                if (!messagesFile.contains("GUIs.Auction House Admin.Listing")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&8&m&l---------------------------");
                    lore.add("");
                    lore.add("  &fTime Left &8&m&l-&e %time%");
                    lore.add("  &fCreator &8&m&l-&e %creator%");
                    lore.add("  &fPrice &8&m&l-&2 %price%");
                    lore.add("");
                    lore.add("%shulker%");
                    lore.add("  &cLeft click to edit listing");
                    lore.add("  &cShift + Right Click to Safely Remove");
                    lore.add("");
                    lore.add("&8&m&l---------------------------");
                    messagesFile.set("GUIs.Auction House Admin.Listing", lore);
                }
                gui_aha_listing = messagesFile.getStringList("GUIs.Auction House Admin.Listing");
            }
            /* NPC Menu*/
            {
                if (!messagesFile.contains("GUIs.NPC.Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&fLocation:");
                    lore.add("&fX: &e%x%");
                    lore.add("&fY: &e%y%");
                    lore.add("&fZ: &e%z%");
                    lore.add(" ");
                    lore.add("&7Left click to teleport");
                    lore.add("&7Shift + Left Click to remove auctioneer trait");
                    lore.add("&7Shift + Right Click to delete NPC");
                    messagesFile.set("GUIs.NPC.Lore", lore);
                }
                gui_npc_lore = messagesFile.getStringList("GUIs.NPC.Lore");
            }
            /* Confirm List GUI */
            {
                if(!messagesFile.contains("GUIs.Confirm List.Display Lore")) {
                    List<String> lore = new ArrayList<>();
                    lore.add("&8&m&l-------------------------------");
                    lore.add(" ");
                    lore.add(" &fListing Amount: &e%amount%");
                    lore.add(" &fListing Fee: &e%fee%");
                    lore.add(" ");
                    lore.add("&7&oListing fee will be removed when listing is posted");
                    lore.add(" ");
                    lore.add("&8&m&l-------------------------------");
                    messagesFile.set("GUIs.Confirm List.Display Lore", lore);
                }
                gui_cl_lore = messagesFile.getStringList("GUIs.Confirm List.Display Lore");
                if (!messagesFile.contains("GUIs.Confirm List.Title")) {
                    messagesFile.set("GUIs.Confirm List.Title", "&6Confirm Listing");
                }
                gui_cl_title = messagesFile.getString("GUIs.Confirm List.Title");
            }
            /* Sound Select GUI */
            {
                if (!messagesFile.contains("GUIs.Main Sound.Title")) {
                    messagesFile.set("GUIs.Main Sound.Title", "&6Select Sound to Change...");
                }
                gui_sound_title = messagesFile.getString("GUIs.Main Sound.Title");
            }
        }

        /* Templates

        Add String
        if(!messagesFile.contains("1")) {
            messagesFile.set("1", "2");
        }
        3 = messagesFile.getString("1");

        Add Lore
        if(!messagesFile.contains("1")) {
            List<String> lore = new ArrayList<>();
            lore.add("2");
            messagesFile.set("1", lore);
        }
        3 = messagesFile.getStringList("1");
        */

        checkVersion();

        fm.saveFile(messagesFile, "messages");
    }

    public void checkVersion() {

        switch (version) {
            case 0:
                AuctionHouse.getInstance().getChat().log("Starting Message file update. Version 1", AuctionHouse.getInstance().isDebug());
                //Listing lore to add taxes and total cost
                if (listingLore.size() == 10) {
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
                }
                listingLore = messagesFile.getStringList("GUIs.AuctionHouse.Listing.Description");
                version = 1;
                messagesFile.set("Version", 1);
                AuctionHouse.getInstance().getChat().log("Message File update complete. Now version " + version, true);
                break;
            case 1:
                messagesFile.set("GUIs.Buttons.Next Page.Name", messagesFile.getString("GUIs.Buttons.Next Page.Name") + " &7&o(%next%/%max%)");
                messagesFile.set("GUIs.Buttons.Previous Page.Name", messagesFile.getString("GUIs.Buttons.Previous Page.Name") + " &7&o(%previous%/%max%)");
                version = 2;
                messagesFile.set("Version", 2);
                AuctionHouse.getInstance().getChat().log("Message File update complete. Now version " + version, true);
                break;
            case 2:
                messagesFile.set("GUIs.Expire Reclaim.Title", "&6&lListing Reclaim");
                version = 3;
                break;
        }
        fm.saveFile(messagesFile, "messages");
    }

}
