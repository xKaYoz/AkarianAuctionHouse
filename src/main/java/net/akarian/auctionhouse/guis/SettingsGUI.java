package net.akarian.auctionhouse.guis;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.messages.MessageManager;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class SettingsGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    private final boolean isAdmin;
    @Getter
    private Inventory inv;
    @Getter
    private static final HashMap<UUID, SettingsGUI> timeMap = new HashMap<>();
    @Getter
    private boolean edited;

    public SettingsGUI(Player player, boolean admin) {
        this.player = player;
        this.isAdmin = admin;
        edited = false;
    }


    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        User user = AuctionHouse.getInstance().getUserManager().getUser(player);

        if(isAdmin) {
            switch (slot) {
                case 10:
                    user.getUserSettings().setOpenAdminMode(!user.getUserSettings().isOpenAdminMode());
                    updateInventory();
                    edited = true;
                    break;
                case 13:
                    user.getUserSettings().setAlertNearExpire(!user.getUserSettings().isAlertNearExpire());
                    updateInventory();
                    edited = true;
                    break;
                case 16:
                    user.getUserSettings().setAlertListingBought(!user.getUserSettings().isAlertListingBought());
                    updateInventory();
                    edited = true;
                    break;
                case 22:
                    chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SETTINGS_EXPIRATIONTIME_MESSAGE));
                    timeMap.put(player.getUniqueId(), this);
                    player.closeInventory();
                    edited = true;
                    break;
                case 28:
                    user.getUserSettings().setAlertCreateListings(!user.getUserSettings().isAlertCreateListings());
                    updateInventory();
                    edited = true;
                    break;
                case 31:
                    user.getUserSettings().setSounds(!user.getUserSettings().isSounds());
                    updateInventory();
                    edited = true;
                    break;
                case 34:
                    user.getUserSettings().setAutoConfirmListing(!user.getUserSettings().isAutoConfirmListing());
                    updateInventory();
                    edited = true;
                    break;
            }
        } else {
            switch (slot) {
                case 10:
                    user.getUserSettings().setAlertCreateListings(!user.getUserSettings().isAlertCreateListings());
                    updateInventory();
                    edited = true;
                    break;
                case 12:
                    user.getUserSettings().setAlertNearExpire(!user.getUserSettings().isAlertNearExpire());
                    updateInventory();
                    edited = true;
                    break;
                case 14:
                    user.getUserSettings().setAlertListingBought(!user.getUserSettings().isAlertListingBought());
                    updateInventory();
                    edited = true;
                    break;
                case 16:
                    user.getUserSettings().setAutoConfirmListing(!user.getUserSettings().isAutoConfirmListing());
                    updateInventory();
                    edited = true;
                    break;
                case 21:
                    chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SETTINGS_EXPIRATIONTIME_MESSAGE));
                    timeMap.put(player.getUniqueId(), this);
                    player.closeInventory();
                    edited = true;
                    break;
                case 23:
                    user.getUserSettings().setSounds(!user.getUserSettings().isSounds());
                    updateInventory();
                    edited = true;
                    break;
            }
        }

    }

    @Override
    public void updateInventory() {

        User user = AuctionHouse.getInstance().getUserManager().getUser(player);
        MessageManager mm = AuctionHouse.getInstance().getMessageManager();

        String adminStatus = user.getUserSettings().isOpenAdminMode() ? "&a&lEnabled" : "&c&lDisabled";
        String expireStatus = user.getUserSettings().isAlertNearExpire() ? "&a&lEnabled" : "&c&lDisabled";
        String expireTime = chat.formatTime(user.getUserSettings().getAlertNearExpireTime());
        String boughtStatus = user.getUserSettings().isAlertListingBought() ? "&a&lEnabled" : "&c&lDisabled";
        String soundsStatus = user.getUserSettings().isSounds() ? "&a&lEnabled" : "&c&lDisabled";
        String createdStatus = user.getUserSettings().isAlertCreateListings() ? "&a&lEnabled" : "&c&lDisabled";
        String autoConfirmStatus = user.getUserSettings().isAutoConfirmListing() ? "&a&lEnabled" : "&c&lDisabled";

        if (isAdmin) {
            inv.setItem(10, ItemBuilder.build(user.getUserSettings().isOpenAdminMode() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_ADMINMODE_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_ADMINMODE_LORE, "%status%;" + adminStatus, "%papi%;" + player.getUniqueId())));
            inv.setItem(13, ItemBuilder.build(user.getUserSettings().isAlertNearExpire() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_EXPIRATION_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_EXPIRATION_LORE, "%status%;" + expireStatus, "%papi%;" + player.getUniqueId())));
            inv.setItem(22, ItemBuilder.build(Material.PAPER, 1, mm.getMessage(MessageType.GUI_SETTINGS_EXPIRATIONTIME_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_EXPIRATIONTIME_LORE, "%time%;" + expireTime, "%papi%;" + player.getUniqueId())));
            inv.setItem(16, ItemBuilder.build(user.getUserSettings().isAlertListingBought() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_LISTINGBOUGHT_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_LISTINGBOUGHT_LORE, "%status%;" + boughtStatus, "%papi%;" + player.getUniqueId())));
            inv.setItem(28, ItemBuilder.build(user.getUserSettings().isAlertCreateListings() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_LISTINGCREATED_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_LISTINGCREATED_LORE, "%status%;" + createdStatus, "%papi%;" + player.getUniqueId())));
            inv.setItem(31, ItemBuilder.build(user.getUserSettings().isSounds() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_SOUNDS_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_SOUNDS_LORE, "%status%;" + soundsStatus, "%papi%;" + player.getUniqueId())));
            inv.setItem(34, ItemBuilder.build(user.getUserSettings().isAutoConfirmListing() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_AUTOCONFIRM_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_AUTOCONFIRM_LORE, "%status%;" + autoConfirmStatus, "%papi%;" + player.getUniqueId())));
        } else {
            inv.setItem(10, ItemBuilder.build(user.getUserSettings().isAlertCreateListings() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_ADMINMODE_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_ADMINMODE_LORE, "%status%;" + adminStatus, "%papi%;" + player.getUniqueId())));
            inv.setItem(12, ItemBuilder.build(user.getUserSettings().isAlertNearExpire() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_EXPIRATION_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_EXPIRATION_LORE, "%status%;" + expireStatus, "%papi%;" + player.getUniqueId())));
            inv.setItem(14, ItemBuilder.build(user.getUserSettings().isAlertListingBought() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_LISTINGBOUGHT_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_LISTINGBOUGHT_LORE, "%status%;" + boughtStatus, "%papi%;" + player.getUniqueId())));
            inv.setItem(16, ItemBuilder.build(user.getUserSettings().isAutoConfirmListing() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_AUTOCONFIRM_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_AUTOCONFIRM_LORE, "%status%;" + autoConfirmStatus, "%papi%;" + player.getUniqueId())));
            inv.setItem(21, ItemBuilder.build(Material.PAPER, 1, mm.getMessage(MessageType.GUI_SETTINGS_EXPIRATIONTIME_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_EXPIRATIONTIME_LORE, "%time%;" + expireTime, "%papi%;" + player.getUniqueId())));
            inv.setItem(23, ItemBuilder.build(user.getUserSettings().isSounds() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_SOUNDS_NAME),
                    mm.getLore(MessageType.GUI_SETTINGS_SOUNDS_LORE, "%status%;" + soundsStatus, "%papi%;" + player.getUniqueId())));
        }

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, isAdmin ? 45 : 36, chat.format("&eUser Settings"));

        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

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
