package net.akarian.auctionhouse.utils;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.edit.LayoutEditGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GUIManager implements Listener {

    public ConcurrentHashMap<String, AkarianInventory> getGui() {
        return gui;
    }
    public ConcurrentHashMap<String, AkarianInventory> gui = new ConcurrentHashMap<>();

    Chat chat = AuctionHouse.getInstance().getChat();

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        if(e.getInventory().getHolder() instanceof AkarianInventory) {
            getGui().put(e.getPlayer().getUniqueId().toString(), (AkarianInventory) e.getInventory().getHolder());
            //chat.alert("PUT " + e.getPlayer().getUniqueId() + " with " + ((AkarianInventory) e.getInventory().getHolder()).getClass());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if(e.getInventory().getHolder() instanceof AkarianInventory) {

            if(e.getInventory().getHolder() instanceof LayoutEditGUI) {
                LayoutEditGUI gui = (LayoutEditGUI) e.getInventory().getHolder();

                gui.restoreInventory(true);
            }

            getGui().remove(e.getPlayer().getUniqueId().toString(), (AkarianInventory) e.getInventory().getHolder());
            //chat.alert("REMOVED " + e.getPlayer().getUniqueId() + " with " + ((AkarianInventory) e.getInventory().getHolder()).getClass());
        }
    }

    public void closeAllInventories() {
        for(String uuid : getGui().keySet()) {
            Player p = Bukkit.getPlayer(UUID.fromString(uuid));
            if(p != null) {
                if(p.getOpenInventory().getTopInventory().getHolder() instanceof LayoutEditGUI) {
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
        if((e.getInventory().getHolder() instanceof AkarianInventory)) {
            if(getGui().get(p.getUniqueId().toString()) instanceof LayoutEditGUI) {
                if(e.getCursor() == null) return;
                if(e.getCursor().getType() == Material.MAGENTA_CONCRETE || e.getCursor().getType() == Material.GRAY_STAINED_GLASS_PANE) {
                    for(Integer i : e.getRawSlots()) {
                        if(e.getInventory().getItem(i) == null)
                            e.getInventory().setItem(i, e.getCursor());
                    }
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        chat.alert("hit click " + e.getRawSlot());

        int slot = e.getRawSlot();
        if((e.getInventory().getHolder() instanceof AkarianInventory)) {

            if(getGui().get(p.getUniqueId().toString()) instanceof LayoutEditGUI) {
                 if(e.getClickedInventory() == null) {
                     e.setCancelled(true);
                     p.setItemOnCursor(null);
                     return;
                } else if(e.getClickedInventory().getType() == InventoryType.CHEST) {
                     e.setCancelled(true);
                     if(e.getClick() == ClickType.RIGHT) {
                         e.getInventory().setItem(e.getRawSlot(), null);
                         return;
                     }
                     if(e.getCurrentItem() == null && e.getCursor() != null) {
                         e.getInventory().setItem(e.getRawSlot(), e.getCursor());
                         p.setItemOnCursor(null);
                     } else if(e.getCurrentItem() != null && e.getCursor() == null) {
                         p.setItemOnCursor(e.getCurrentItem());
                         e.getInventory().setItem(e.getRawSlot(), null);
                     } else if(e.getCurrentItem() != null && e.getCursor() != null) {
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
        }
    }

}
