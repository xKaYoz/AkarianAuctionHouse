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

public class ServerSettingsGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;
    @Getter
    private static final HashMap<UUID, ServerSettingsGUI> timeMap = new HashMap<>();
    @Getter
    private static final HashMap<UUID, ServerSettingsGUI> feeMap = new HashMap<>();

    public ServerSettingsGUI() {

    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 11:
                feeMap.put(p.getUniqueId(), this);
                p.closeInventory();
                chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getSt_listingFee_message());
                break;
            case 13:
                AuctionHouse.getInstance().getConfigFile().setCreativeListing(!AuctionHouse.getInstance().getConfigFile().isCreativeListing());
                updateInventory();
                break;
            case 15:
                timeMap.put(p.getUniqueId(), this);
                p.closeInventory();
                chat.sendMessage(p, AuctionHouse.getInstance().getMessages().getSt_listingTime_message());
                break;
        }
    }

    @Override
    public void updateInventory() {

        for (int i = 0; i <= 8; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
        Messages m = AuctionHouse.getInstance().getMessages();


        List<String> feeLore = new ArrayList<>();
        for (String s : m.getSt_listingFee_lore()) {
            if (s.contains("%fee%")) {
                feeLore.add(s.replace("%fee%", AuctionHouse.getInstance().getConfigFile().getListingFee()));
            } else {
                feeLore.add(s);
            }
        }

        List<String> creativeLore = new ArrayList<>();
        for (String s : m.getSt_creativeListing_lore()) {
            if (s.contains("%status%")) {
                creativeLore.add(s.replace("%status%", (AuctionHouse.getInstance().getConfigFile().isCreativeListing() ? "&a&lEnabled" : "&c&lDisabled")));
            } else {
                creativeLore.add(s);
            }
        }

        List<String> timeLore = new ArrayList<>();
        for (String s : m.getSt_listingTime_lore()) {
            if (s.contains("%time%")) {
                timeLore.add(s.replace("%time%", chat.formatTime(AuctionHouse.getInstance().getConfigFile().getListingTime())));
            } else {
                timeLore.add(s);
            }
        }

        inv.setItem(11, ItemBuilder.build(Material.PAPER, 1, m.getSt_listingFee_name(), feeLore));
        inv.setItem(13, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().isCreativeListing() ? Material.LIME_DYE : Material.GRAY_DYE, 1, m.getSt_creativeListing_name(), creativeLore));
        inv.setItem(15, ItemBuilder.build(Material.PAPER, 1, m.getSt_listingTime_name(), timeLore));

        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(Material.GRAY_STAINED_GLASS_PANE, 1, " ", Collections.EMPTY_LIST));
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format("&6&lAuctionHouse Admin Settings"));
        updateInventory();
        return inv;
    }
}
