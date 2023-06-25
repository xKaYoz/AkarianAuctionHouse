package net.akarian.auctionhouse.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.edit.LayoutEditGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GUIManager implements Listener {

    public ConcurrentHashMap<String, AkarianInventory> gui = new ConcurrentHashMap<>();
    Chat chat = AuctionHouse.getInstance().getChat();

    public ConcurrentHashMap<String, AkarianInventory> getGui() {
        return gui;
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof AkarianInventory) {
            getGui().put(e.getPlayer().getUniqueId().toString(), (AkarianInventory) e.getInventory().getHolder());
            //chat.alert("PUT " + e.getPlayer().getUniqueId() + " with " + ((AkarianInventory) e.getInventory().getHolder()).getClass());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getInventory().getHolder() instanceof AkarianInventory) {

            if (e.getInventory().getHolder() instanceof LayoutEditGUI) {
                LayoutEditGUI gui = (LayoutEditGUI) e.getInventory().getHolder();
                if (LayoutEditGUI.getHelpMessage().containsKey(p.getUniqueId()) || LayoutEditGUI.getLayoutNameEdit().containsKey(p.getUniqueId()) || LayoutEditGUI.getInventorySizeEdit().containsKey(p.getUniqueId()) || LayoutEditGUI.getDisplayNameEdit().containsKey(p.getUniqueId())) {
                    Bukkit.getScheduler().runTaskLater(AuctionHouse.getInstance(), gui::giveEditorMenu, 1);
                } else {
                    if (!gui.isClosed())
                        Bukkit.getScheduler().runTaskLater(AuctionHouse.getInstance(), () -> gui.restoreInventory(false), 1);
                }
            }
            getGui().remove(e.getPlayer().getUniqueId().toString(), (AkarianInventory) e.getInventory().getHolder());

            //chat.alert("REMOVED " + e.getPlayer().getUniqueId() + " with " + ((AkarianInventory) e.getInventory().getHolder()).getClass());
        }
    }

    public void closeAllInventories() {
        for (String uuid : getGui().keySet()) {
            Player p = Bukkit.getPlayer(UUID.fromString(uuid));
            if (p != null) {
                if (p.getOpenInventory().getTopInventory().getHolder() instanceof LayoutEditGUI) {
                    LayoutEditGUI gui = (LayoutEditGUI) p.getOpenInventory().getTopInventory().getHolder();
                    gui.restoreInventory(true);
                }
                p.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInteract(InventoryDragEvent e) {
        Player p = (Player) e.getWhoClicked();
        if ((e.getInventory().getHolder() instanceof AkarianInventory) && (getGui().get(p.getUniqueId().toString()) instanceof LayoutEditGUI)) {
            if (e.getCursor() == null) return;
            if (e.getCursor().getType() == Material.MAGENTA_CONCRETE || e.getCursor().getType() == AuctionHouse.getInstance().getConfigFile().getSpacerItem()) {
                for (Integer i : e.getRawSlots()) {
                    if (e.getInventory().getItem(i) == null) e.getInventory().setItem(i, e.getCursor());
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        int slot = e.getRawSlot();
        if ((e.getInventory().getHolder() instanceof AkarianInventory)) {

            if (getGui().get(p.getUniqueId().toString()) instanceof LayoutEditGUI) {
                if (e.getClickedInventory() == null) {
                    e.setCancelled(true);
                    p.setItemOnCursor(null);
                    return;
                } else if (e.getClickedInventory().getType() == InventoryType.CHEST) {
                    e.setCancelled(true);
                    if (e.getClick() == ClickType.RIGHT) {
                        e.getInventory().setItem(e.getRawSlot(), null);
                        return;
                    }
                    if (e.getCurrentItem() == null && e.getCursor() != null) {
                        e.getInventory().setItem(e.getRawSlot(), e.getCursor());
                        p.setItemOnCursor(null);
                    } else if (e.getCurrentItem() != null && e.getCursor() == null) {
                        p.setItemOnCursor(e.getCurrentItem());
                        e.getInventory().setItem(e.getRawSlot(), null);
                    } else if (e.getCurrentItem() != null && e.getCursor() != null) {
                        if (e.getClick() == ClickType.MIDDLE) {
                            p.setItemOnCursor(e.getCurrentItem());
                            e.getInventory().setItem(e.getRawSlot(), e.getCurrentItem());
                            return;
                        }
                        ItemStack cursor = e.getCursor();
                        ItemStack current = e.getCurrentItem();
                        p.setItemOnCursor(current);
                        e.getInventory().setItem(e.getRawSlot(), cursor);
                    }
                    return;
                }
            }

            e.setCancelled(true);

            if (item == null || !item.hasItemMeta()) return;

            getGui().get(p.getUniqueId().toString()).onGUIClick(e.getInventory(), p, slot, item, e.getClick());

            if (getGui().get(p.getUniqueId().toString()) instanceof LayoutEditGUI) {
                LayoutEditGUI gui = (LayoutEditGUI) getGui().get(p.getUniqueId().toString());

                if (gui.isSpacerItem()) {
                    p.setItemOnCursor(ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.emptyList()));
                    gui.setSpacerItem(false);
                } else if (gui.isAdminButton()) {
                    p.setItemOnCursor(ItemBuilder.build(Material.LIME_DYE, 1, "&cAdmin Mode", Collections.singletonList("&aAdmin mode is enabled.")));
                    gui.setAdminButton(false);
                } else if (gui.isCloseButton()) {
                    p.setItemOnCursor(ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_cn(), AuctionHouse.getInstance().getMessages().getGui_ah_cd()));
                    gui.setCloseButton(false);
                } else if (gui.isListingItem()) {
                    p.setItemOnCursor(ItemBuilder.build(Material.MAGENTA_CONCRETE, 1, "&5Listing Item", Collections.singletonList("&7Place where you want listings to be placed.")));
                    gui.setListingItem(false);
                } else if (gui.isInformationButton()) {
                    List<String> infoDesc = new ArrayList<>();
                    for (String s : AuctionHouse.getInstance().getMessages().getGui_ah_id()) {
                        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                            infoDesc.add(PlaceholderAPI.setPlaceholders(p, s.replace("%balance%", chat.formatMoney(AuctionHouse.getInstance().getEcon().getBalance(p))).replace("%items%", String.valueOf(AuctionHouse.getInstance().getListingManager().getActive().size()))));
                        else
                            infoDesc.add(s.replace("%balance%", chat.formatMoney(AuctionHouse.getInstance().getEcon().getBalance(p))).replace("%items%", String.valueOf(AuctionHouse.getInstance().getListingManager().getActive().size())));
                    }
                    p.setItemOnCursor(ItemBuilder.build(Material.BOOK, 1, AuctionHouse.getInstance().getMessages().getGui_ah_in(), infoDesc));
                    gui.setInformationButton(false);
                } else if (gui.isNextPageButton()) {
                    p.setItemOnCursor(ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_npn(), AuctionHouse.getInstance().getMessages().getGui_buttons_npd()));
                    gui.setNextPageButton(false);
                } else if (gui.isPreviousPageButton()) {
                    p.setItemOnCursor(ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_ppn(), AuctionHouse.getInstance().getMessages().getGui_buttons_ppd()));
                    gui.setPreviousPageButton(false);
                } else if (gui.isSearchButton()) {
                    p.setItemOnCursor(ItemBuilder.build(Material.HOPPER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_sn(), AuctionHouse.getInstance().getMessages().getGui_ah_sd()));
                    gui.setSearchButton(false);
                } else if (gui.isSortButton()) {
                    p.setItemOnCursor(ItemBuilder.build(Material.PAPER, 1, AuctionHouse.getInstance().getMessages().getGui_ah_stn(), AuctionHouse.getInstance().getMessages().getGui_ah_std()));
                    gui.setSortButton(false);
                } else if (gui.isExpiredListingsButton()) {
                    p.setItemOnCursor(ItemBuilder.build(Material.CHEST, 1, AuctionHouse.getInstance().getMessages().getGui_ah_en(), AuctionHouse.getInstance().getMessages().getGui_ah_ed()));
                    gui.setExpiredListingsButton(false);
                }
            }

        }
    }

}
