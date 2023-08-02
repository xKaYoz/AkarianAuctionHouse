package net.akarian.auctionhouse.guis.admin.settings;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DefaultPlayerSettingsGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;

    @Getter
    private static final HashMap<UUID, DefaultPlayerSettingsGUI> timeMap = new HashMap<>();

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                p.openInventory(new MainSettingsGUI().getInventory());
                break;
            case 10:
                AuctionHouse.getInstance().getConfigFile().setDps_adminMode(!AuctionHouse.getInstance().getConfigFile().isDps_adminMode());
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                updateInventory();
                break;
            case 13:
                AuctionHouse.getInstance().getConfigFile().setDps_expire(!AuctionHouse.getInstance().getConfigFile().isDps_expire());
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                updateInventory();
                break;
            case 16:
                AuctionHouse.getInstance().getConfigFile().setDps_bought(!AuctionHouse.getInstance().getConfigFile().isDps_bought());
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                updateInventory();
                break;
            case 22:
                timeMap.put(p.getUniqueId(), this);
                p.closeInventory();
                break;
            case 28:
                AuctionHouse.getInstance().getConfigFile().setDps_create(!AuctionHouse.getInstance().getConfigFile().isDps_create());
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                updateInventory();
                break;
            case 31:
                AuctionHouse.getInstance().getConfigFile().setDps_sounds(!AuctionHouse.getInstance().getConfigFile().isDps_sounds());
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                updateInventory();
                break;
            case 34:
                AuctionHouse.getInstance().getConfigFile().setDps_autoConfirm(!AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm());
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                updateInventory();
                break;

        }

    }

    @Override
    public void updateInventory() {
        MessageManager mm = AuctionHouse.getInstance().getMessageManager();

        String adminStatus = AuctionHouse.getInstance().getConfigFile().isDps_adminMode() ? "&a&lEnabled" : "&c&lDisabled";
        String expireStatus = AuctionHouse.getInstance().getConfigFile().isDps_expire() ? "&a&lEnabled" : "&c&lDisabled";
        String expireTime = chat.formatTime(AuctionHouse.getInstance().getConfigFile().getDps_expireTime());
        String boughtStatus = AuctionHouse.getInstance().getConfigFile().isDps_bought() ? "&a&lEnabled" : "&c&lDisabled";
        String soundsStatus = AuctionHouse.getInstance().getConfigFile().isDps_sounds() ? "&a&lEnabled" : "&c&lDisabled";
        String createdStatus = AuctionHouse.getInstance().getConfigFile().isDps_create() ? "&a&lEnabled" : "&c&lDisabled";
        String autoConfirmStatus = AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm() ? "&a&lEnabled" : "&c&lDisabled";


        inv.setItem(10, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_adminMode() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_ADMINMODE_NAME),
                mm.getLore(MessageType.GUI_SETTINGS_ADMINMODE_LORE, "%status%;" + adminStatus)));
        inv.setItem(13, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_expire() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_EXPIRATION_NAME),
                mm.getLore(MessageType.GUI_SETTINGS_EXPIRATION_LORE, "%status%;" + expireStatus)));
        inv.setItem(22, ItemBuilder.build(Material.PAPER, 1, mm.getMessage(MessageType.GUI_SETTINGS_EXPIRATIONTIME_NAME),
                mm.getLore(MessageType.GUI_SETTINGS_EXPIRATIONTIME_LORE, "%time%;" + expireTime)));
        inv.setItem(16, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_bought() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_LISTINGBOUGHT_NAME),
                mm.getLore(MessageType.GUI_SETTINGS_LISTINGBOUGHT_LORE, "%status%;" + boughtStatus)));
        inv.setItem(28, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_create() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_LISTINGCREATED_NAME),
                mm.getLore(MessageType.GUI_SETTINGS_LISTINGCREATED_LORE, "%status%;" + createdStatus)));
        inv.setItem(31, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_sounds() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_SOUNDS_NAME),
                mm.getLore(MessageType.GUI_SETTINGS_SOUNDS_LORE, "%status%;" + soundsStatus)));
        inv.setItem(34, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_AUTOCONFIRM_NAME),
                mm.getLore(MessageType.GUI_SETTINGS_AUTOCONFIRM_LORE, "%status%;" + autoConfirmStatus)));
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 45, chat.format("&eUser Settings"));

        //Spacers
        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));


        inv.setItem(17, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        inv.setItem(18, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        inv.setItem(26, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        inv.setItem(27, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        for (int i = 35; i <= 44; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

        return inv;
    }
}
