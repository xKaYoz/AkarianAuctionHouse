package net.akarian.auctionhouse.guis.admin.settings;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
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
            case 34:
                AuctionHouse.getInstance().getConfigFile().setDps_autoConfirm(!AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm());
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                updateInventory();
                break;

        }

    }

    @Override
    public void updateInventory() {
        Messages m = AuctionHouse.getInstance().getMessages();

        List<String> adminLore = new ArrayList<>();
        for (String s : m.getSt_admin_lore()) {
            if (s.contains("%status%")) {
                adminLore.add(s.replace("%status%", (AuctionHouse.getInstance().getConfigFile().isDps_adminMode() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                adminLore.add(s);
            }
        }

        List<String> expireLore = new ArrayList<>();
        for (String s : m.getSt_expire_lore()) {
            if (s.contains("%status%")) {
                expireLore.add(s.replace("%status%", (AuctionHouse.getInstance().getConfigFile().isDps_expire() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                expireLore.add(s);
            }
        }

        List<String> expireTimeLore = new ArrayList<>();
        for (String s : m.getSt_expireTime_lore()) {
            if (s.contains("%time%")) {
                expireTimeLore.add(s.replace("%time%", chat.formatTime(AuctionHouse.getInstance().getConfigFile().getDps_expireTime())));
            } else {
                expireTimeLore.add(s);
            }
        }
        List<String> boughtLore = new ArrayList<>();
        for (String s : m.getSt_bought_lore()) {
            if (s.contains("%status%")) {
                boughtLore.add(s.replace("%status%", (AuctionHouse.getInstance().getConfigFile().isDps_bought() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                boughtLore.add(s);
            }
        }
        List<String> createdLore = new ArrayList<>();
        for (String s : m.getSt_created_lore()) {
            if (s.contains("%status%")) {
                createdLore.add(s.replace("%status%", (AuctionHouse.getInstance().getConfigFile().isDps_create() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                createdLore.add(s);
            }
        }

        List<String> autoConfirmLore = new ArrayList<>();
        for (String s : m.getSt_autoConfirm_lore()) {
            if (s.contains("%status%")) {
                autoConfirmLore.add(s.replace("%status%", (AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                autoConfirmLore.add(s);
            }
        }

        inv.setItem(10, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_adminMode() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_admin_name(),
                adminLore));
        inv.setItem(13, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_expire() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_expire_name(),
                expireLore));
        inv.setItem(22, ItemBuilder.build(Material.PAPER, 1, m.getSt_expireTime_name(),
                expireTimeLore));
        inv.setItem(16, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_bought() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_bought_name(),
                boughtLore));
        inv.setItem(28, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_create() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_created_name(),
                createdLore));
        inv.setItem(34, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_autoConfirm_name(),
                autoConfirmLore));
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 45, chat.format("&eUser Settings"));

        //Spacers
        for (int i = 0; i <= 9; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_rt(), AuctionHouse.getInstance().getMessages().getGui_buttons_rd()));


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
