package net.akarian.auctionhouse.guis;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SettingsGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    private final boolean isAdmin;
    @Getter
    private Inventory inv;
    @Getter
    private static final HashMap<UUID, SettingsGUI> timeMap = new HashMap<>();

    public SettingsGUI(Player player, boolean admin) {
        this.player = player;
        this.isAdmin = admin;
    }


    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        User user = AuctionHouse.getInstance().getUserManager().getUser(player);

        if(isAdmin) {
            switch (slot) {
                case 10:
                    user.getUserSettings().setOpenAdminMode(!user.getUserSettings().isOpenAdminMode());
                    updateInventory();
                    break;
                case 13:
                    user.getUserSettings().setAlertNearExpire(!user.getUserSettings().isAlertNearExpire());
                    updateInventory();
                    break;
                case 16:
                    user.getUserSettings().setAlertListingBought(!user.getUserSettings().isAlertListingBought());
                    updateInventory();
                    break;
                case 22:
                    chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getSt_expireTime_message());
                    timeMap.put(player.getUniqueId(), this);
                    player.closeInventory();
                    break;
                case 28:
                    user.getUserSettings().setAlertCreateListings(!user.getUserSettings().isAlertCreateListings());
                    updateInventory();
                    break;
                case 34:
                    user.getUserSettings().setAutoConfirmListing(!user.getUserSettings().isAutoConfirmListing());
                    updateInventory();
                    break;
            }
        } else {
            switch (slot) {
                case 10:
                    user.getUserSettings().setAlertCreateListings(!user.getUserSettings().isAlertCreateListings());
                    updateInventory();
                    break;
                case 12:
                    user.getUserSettings().setAlertNearExpire(!user.getUserSettings().isAlertNearExpire());
                    updateInventory();
                    break;
                case 14:
                    user.getUserSettings().setAlertListingBought(!user.getUserSettings().isAlertListingBought());
                    updateInventory();
                    break;
                case 16:
                    user.getUserSettings().setAutoConfirmListing(!user.getUserSettings().isAutoConfirmListing());
                    updateInventory();
                    break;
                case 21:
                    chat.sendMessage(player, AuctionHouse.getInstance().getMessages().getSt_expireTime_message());
                    timeMap.put(player.getUniqueId(), this);
                    player.closeInventory();
                    break;

            }
        }

    }

    @Override
    public void updateInventory() {

        User user = AuctionHouse.getInstance().getUserManager().getUser(player);
        Messages m = AuctionHouse.getInstance().getMessages();

        List<String> adminLore = new ArrayList<>();
        for (String s : m.getSt_admin_lore()) {
            if (s.contains("%status%")) {
                adminLore.add(s.replace("%status%", (user.getUserSettings().isOpenAdminMode() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                adminLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }

        List<String> expireLore = new ArrayList<>();
        for (String s : m.getSt_expire_lore()) {
            if (s.contains("%status%")) {
                expireLore.add(s.replace("%status%", (user.getUserSettings().isAlertNearExpire() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                expireLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }

        List<String> expireTimeLore = new ArrayList<>();
        for (String s : m.getSt_expireTime_lore()) {
            if (s.contains("%time%")) {
                expireTimeLore.add(s.replace("%time%", chat.formatTime(user.getUserSettings().getAlertNearExpireTime())));
            } else {
                expireTimeLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }
        List<String> boughtLore = new ArrayList<>();
        for (String s : m.getSt_bought_lore()) {
            if (s.contains("%status%")) {
                boughtLore.add(s.replace("%status%", (user.getUserSettings().isAlertListingBought() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                boughtLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }
        List<String> createdLore = new ArrayList<>();
        for (String s : m.getSt_created_lore()) {
            if (s.contains("%status%")) {
                createdLore.add(s.replace("%status%", (user.getUserSettings().isAlertCreateListings() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                createdLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }

        List<String> autoConfirmLore = new ArrayList<>();
        for (String s : m.getSt_autoConfirm_lore()) {
            if (s.contains("%status%")) {
                autoConfirmLore.add(s.replace("%status%", (user.getUserSettings().isAutoConfirmListing() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                autoConfirmLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }

        if (isAdmin) {
            inv.setItem(10, ItemBuilder.build(user.getUserSettings().isOpenAdminMode() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_admin_name(),
                    adminLore));
            inv.setItem(13, ItemBuilder.build(user.getUserSettings().isAlertNearExpire() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_expire_name(),
                    expireLore));
            inv.setItem(22, ItemBuilder.build(Material.PAPER, 1, m.getSt_expireTime_name(),
                    expireTimeLore));
            inv.setItem(16, ItemBuilder.build(user.getUserSettings().isAlertListingBought() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_bought_name(),
                    boughtLore));
            inv.setItem(28, ItemBuilder.build(user.getUserSettings().isAlertCreateListings() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_created_name(),
                    createdLore));
            inv.setItem(34, ItemBuilder.build(user.getUserSettings().isAutoConfirmListing() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_autoConfirm_name(),
                    autoConfirmLore));
        } else {
            inv.setItem(10, ItemBuilder.build(user.getUserSettings().isAlertCreateListings() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_created_name(),
                    createdLore));
            inv.setItem(12, ItemBuilder.build(user.getUserSettings().isAlertNearExpire() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_expire_name(),
                    expireLore));
            inv.setItem(21, ItemBuilder.build(Material.PAPER, 1, m.getSt_expireTime_name(),
                    expireTimeLore));
            inv.setItem(14, ItemBuilder.build(user.getUserSettings().isAlertListingBought() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_bought_name(),
                    boughtLore));
            inv.setItem(16, ItemBuilder.build(user.getUserSettings().isAutoConfirmListing() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_autoConfirm_name(),
                    autoConfirmLore));
        }

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, isAdmin ? 45 : 36, chat.format("&eUser Settings"));

        User user = AuctionHouse.getInstance().getUserManager().getUser(player);
        Messages m = AuctionHouse.getInstance().getMessages();

        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        List<String> adminLore = new ArrayList<>();
        for (String s : m.getSt_admin_lore()) {
            if (s.contains("%status%")) {
                adminLore.add(s.replace("%status%", (user.getUserSettings().isOpenAdminMode() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                adminLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }

        List<String> expireLore = new ArrayList<>();
        for (String s : m.getSt_expire_lore()) {
            if (s.contains("%status%")) {
                expireLore.add(s.replace("%status%", (user.getUserSettings().isAlertNearExpire() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                expireLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }

        List<String> expireTimeLore = new ArrayList<>();
        for (String s : m.getSt_expireTime_lore()) {
            if (s.contains("%time%")) {
                expireTimeLore.add(s.replace("%time%", chat.formatTime(user.getUserSettings().getAlertNearExpireTime())));
            } else {
                expireTimeLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }
        List<String> boughtLore = new ArrayList<>();
        for (String s : m.getSt_bought_lore()) {
            if (s.contains("%status%")) {
                boughtLore.add(s.replace("%status%", (user.getUserSettings().isAlertListingBought() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                boughtLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }
        List<String> createdLore = new ArrayList<>();
        for (String s : m.getSt_created_lore()) {
            if (s.contains("%status%")) {
                createdLore.add(s.replace("%status%", (user.getUserSettings().isAlertCreateListings() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                createdLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }

        List<String> autoConfirmLore = new ArrayList<>();
        for (String s : m.getSt_autoConfirm_lore()) {
            if (s.contains("%status%")) {
                autoConfirmLore.add(s.replace("%status%", (user.getUserSettings().isAutoConfirmListing() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                autoConfirmLore.add(PlaceholderAPI.setPlaceholders(player, s));
            }
        }

        if (isAdmin) {
            inv.setItem(10, ItemBuilder.build(user.getUserSettings().isOpenAdminMode() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_admin_name(),
                    adminLore));
            inv.setItem(13, ItemBuilder.build(user.getUserSettings().isAlertNearExpire() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_expire_name(),
                    expireLore));
            inv.setItem(22, ItemBuilder.build(Material.PAPER, 1, m.getSt_expireTime_name(),
                    expireTimeLore));
            inv.setItem(16, ItemBuilder.build(user.getUserSettings().isAlertListingBought() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_bought_name(),
                    boughtLore));
            inv.setItem(28, ItemBuilder.build(user.getUserSettings().isAlertCreateListings() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_created_name(),
                    createdLore));
            inv.setItem(34, ItemBuilder.build(user.getUserSettings().isAutoConfirmListing() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_autoConfirm_name(),
                    autoConfirmLore));
        } else {
            inv.setItem(10, ItemBuilder.build(user.getUserSettings().isAlertCreateListings() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_created_name(),
                    createdLore));
            inv.setItem(12, ItemBuilder.build(user.getUserSettings().isAlertNearExpire() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_expire_name(),
                    expireLore));
            inv.setItem(21, ItemBuilder.build(Material.PAPER, 1, m.getSt_expireTime_name(),
                    expireTimeLore));
            inv.setItem(14, ItemBuilder.build(user.getUserSettings().isAlertListingBought() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_bought_name(),
                    boughtLore));
            inv.setItem(16, ItemBuilder.build(user.getUserSettings().isAutoConfirmListing() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_autoConfirm_name(),
                    autoConfirmLore));
        }

        inv.setItem(17, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        inv.setItem(18, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));

        if (isAdmin) {
            inv.setItem(26, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
            inv.setItem(27, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        for (int i = (isAdmin ? 35 : 26); i <= (isAdmin ? 44 : 35); i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        return inv;
    }
}
