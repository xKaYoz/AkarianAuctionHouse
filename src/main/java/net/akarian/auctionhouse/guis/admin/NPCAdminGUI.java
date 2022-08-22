package net.akarian.auctionhouse.guis.admin;

import net.akarian.auctionhouse.utils.AkarianInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NPCAdminGUI implements AkarianInventory {
    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return null;
    }
}
