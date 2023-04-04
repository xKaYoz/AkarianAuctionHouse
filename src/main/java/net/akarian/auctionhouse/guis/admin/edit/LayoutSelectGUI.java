package net.akarian.auctionhouse.guis.admin.edit;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.AuctionHouseAdminGUI;
import net.akarian.auctionhouse.layouts.Layout;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.UUIDDataType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LayoutSelectGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    int page;
    List<Layout> cloned;
    private Inventory inv;

    public LayoutSelectGUI(Player player, int page) {
        this.player = player;
        this.page = page;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        if (slot == 8) {
            player.openInventory(new AuctionHouseAdminGUI().getInventory());
            return;
        }

        //Is listing
        if (slot >= 9 && slot <= 26) {
            NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "builder-uuid");
            ItemMeta itemMeta = item.getItemMeta();
            assert itemMeta != null;
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            UUID uuid = container.get(key, new UUIDDataType());
            if (type.isShiftClick()) {
                if (type.isLeftClick()) {
                    //Shift + Left click
                    if (AuctionHouse.getInstance().getLayoutManager().getActiveLayout() != null) {
                        AuctionHouse.getInstance().getLayoutManager().getActiveLayout().setActive(false);
                    }
                    AuctionHouse.getInstance().getLayoutManager().setActiveLayout(AuctionHouse.getInstance().getLayoutManager().getLayout(uuid));
                    AuctionHouse.getInstance().getLayoutManager().getLayout(uuid).setActive(true);
                } else if (type.isRightClick()) {
                    //Shift + Right Click
                    if (!AuctionHouse.getInstance().getLayoutManager().getLayout(uuid).isActive())
                        AuctionHouse.getInstance().getLayoutManager().unregister(AuctionHouse.getInstance().getLayoutManager().getLayout(uuid));
                }
            } else {
                if (type.isLeftClick()) {
                    p.openInventory(new LayoutEditGUI(p, AuctionHouse.getInstance().getLayoutManager().getLayout(uuid)).cloneInventory(true).getInventory());
                }
            }
        }

        if (slot == 31) {
            AuctionHouse.getInstance().getLayoutManager().createDefaultLayout(cloned.size() + 1 + "", false);
        } else if (slot == 34 && item.getType() == Material.NETHER_STAR) {
            page++;
            updateInventory();
        } else if (slot == 28 && item.getType() == Material.NETHER_STAR) {
            page--;
            updateInventory();
        }
    }

    @Override
    public void updateInventory() {

        cloned = new ArrayList<>(AuctionHouse.getInstance().getLayoutManager().getLayouts());

        int end = page * 18;
        int start = end - 18;
        int t = start;
        int slot = 9;

        for (int i = 9; i <= 26; i++) {
            inv.setItem(i, null);
        }

        for (int i = start; i <= end; i++) {
            if (cloned.size() == t || t >= end) {
                break;
            }
            inv.setItem(slot, ItemBuilder.build(cloned.get(i).getDisplayType(), 1, "&e" + cloned.get(i).getName(), Arrays.asList("&7Display Name: &f" + cloned.get(i).getInventoryName(), "&7Left click to edit", cloned.get(i).isActive() ? "&a&lACTIVE" : "&7Shift + Left click to set to active", cloned.get(i).isActive() ? null : "&7Shift + Right click to delete"), cloned.get(i).isActive() ? "shine" : "", "uuid_" + cloned.get(i).getUuid().toString()));
            slot++;
            t++;
        }

        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_ppn(), AuctionHouse.getInstance().getMessages().getGui_buttons_ppd());
            inv.setItem(28, previous);
        } else {
            inv.setItem(28, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        if (cloned.size() > 18 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_npn(), AuctionHouse.getInstance().getMessages().getGui_buttons_npd());
            inv.setItem(34, next);
        } else {
            inv.setItem(34, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 36, chat.format("&6Layout Selector"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_rt(), AuctionHouse.getInstance().getMessages().getGui_buttons_rd()));

        for (int i = 27; i <= 35; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

        inv.setItem(31, ItemBuilder.build(Material.DIAMOND, 1, "&aNew Layout", Collections.singletonList("&7Click to create a new layout.")));

        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_ppn(), AuctionHouse.getInstance().getMessages().getGui_buttons_ppd());
            inv.setItem(28, previous);
        }

        return inv;
    }
}
