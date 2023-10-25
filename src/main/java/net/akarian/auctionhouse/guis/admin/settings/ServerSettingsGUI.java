package net.akarian.auctionhouse.guis.admin.settings;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
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

public class ServerSettingsGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;
    @Getter
    private static final HashMap<UUID, ServerSettingsGUI> timeMap = new HashMap<>();
    @Getter
    private static final HashMap<UUID, ServerSettingsGUI> feeMap = new HashMap<>();
    @Getter
    private static final HashMap<UUID, ServerSettingsGUI> taxMap = new HashMap<>();

    public ServerSettingsGUI() {

    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                p.openInventory(new MainSettingsGUI().getInventory());
                break;
            case 10:
                feeMap.put(p.getUniqueId(), this);
                p.closeInventory();
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SETTINGS_LISTINGFEE_MESSAGE));
                break;
            case 12:
                taxMap.put(p.getUniqueId(), this);
                p.closeInventory();
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SETTINGS_SALESTAX_MESSAGE));
                break;
            case 14:
                AuctionHouse.getInstance().getConfigFile().setCreativeListing(!AuctionHouse.getInstance().getConfigFile().isCreativeListing());
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                updateInventory();
                break;
            case 16:
                timeMap.put(p.getUniqueId(), this);
                p.closeInventory();
                chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SETTINGS_LISTINGTIME_MESSAGE));
                break;
        }
    }

    @Override
    public void updateInventory() {
        MessageManager mm = AuctionHouse.getInstance().getMessageManager();

        String creativeLore = (AuctionHouse.getInstance().getConfigFile().isCreativeListing() ? "&a&lEnabled" : "&c&lDisabled");

        inv.setItem(10, ItemBuilder.build(Material.PAPER, 1, mm.getMessage(MessageType.GUI_SETTINGS_LISTINGFEE_NAME), mm.getLore(MessageType.GUI_SETTINGS_LISTINGFEE_LORE, "%fee%;" + AuctionHouse.getInstance().getConfigFile().getListingFee())));
        inv.setItem(12, ItemBuilder.build(Material.PAPER, 1, mm.getMessage(MessageType.GUI_SETTINGS_SALESTAX_NAME), mm.getLore(MessageType.GUI_SETTINGS_SALESTAX_LORE, "%tax%;" + AuctionHouse.getInstance().getConfigFile().getListingTax())));
        inv.setItem(14, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isCreativeListing() ? Material.LIME_DYE : Material.GRAY_DYE, 1, mm.getMessage(MessageType.GUI_SETTINGS_CREATIVELISTING_NAME), mm.getLore(MessageType.GUI_SETTINGS_CREATIVELISTING_LORE, "%status%;" + creativeLore)));
        inv.setItem(16, ItemBuilder.build(Material.PAPER, 1, mm.getMessage(MessageType.GUI_SETTINGS_LISTINGTIME_NAME), mm.getLore(MessageType.GUI_SETTINGS_LISTINGTIME_LORE, "%time%;" + chat.formatTime(AuctionHouse.getInstance().getConfigFile().getListingTime()))));

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format("&6&lAuctionHouse Server Settings"));
        for (int i = 0; i <= 8; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));
        updateInventory();
        return inv;
    }
}
