package net.akarian.auctionhouse.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryHandler {

    public static boolean canCarryItem(Player player, ItemStack item, boolean compareItemMeta) {
        int amount = item.getAmount();
        for (int i = 0; i < 36; i++) {
            if (amount <= 0) {
                return true;
            }
            ItemStack localItemStack = player.getInventory().getItem(i);
            if ((localItemStack == null) || (localItemStack.getType().isAir())) {
                amount -= item.getMaxStackSize();
            } else if (InventoryHandler.compareItemStacks(localItemStack, item, compareItemMeta)) {
                amount -= item.getMaxStackSize() - localItemStack.getAmount();
            }
        }

        return amount <= 0;
    }

    public static void addItem(Player player, ItemStack itemStack) {
        if(canCarryItem(player, itemStack, true)) {
            if(player.getInventory().getItemInMainHand().getType().isAir()) {
                player.getInventory().setItemInMainHand(itemStack);
            } else {
                player.getInventory().addItem(itemStack);
            }
        }
    }

    public static void removeItemFromPlayer(Player player, ItemStack item, int amount, boolean checkItemMeta) {
        int i = amount;
        for (int j = 0; j < player.getInventory().getSize(); j++) {
            ItemStack localItemStack = player.getInventory().getItem(j);
            if (InventoryHandler.compareItemStacks(localItemStack, item, checkItemMeta)) {
                if (i >= localItemStack.getAmount()) {
                    player.getInventory().clear(j);
                    i -= localItemStack.getAmount();
                } else {
                    if (i <= 0) {
                        break;
                    }
                    localItemStack.setAmount(localItemStack.getAmount() - i);
                    i = 0;
                }
            }
        }
        player.updateInventory();
    }

    public static boolean compareItemStacks(ItemStack paramItemStack1, ItemStack paramItemStack2, boolean paramBoolean) {
        if ((paramItemStack1 == null) || (paramItemStack2 == null) ||

                (paramItemStack1.getType() != paramItemStack2.getType()) ||
                (paramItemStack1.getDurability() != paramItemStack2.getDurability())) {
            return false;
        }
        if ((paramBoolean) && (!paramItemStack1.getItemMeta().equals(paramItemStack2.getItemMeta()))) {
            return false;
        }
        return true;
    }

    public static int getPlayersItemAmount(Player paramPlayer, ItemStack paramItemStack, boolean paramBoolean) {
        int i = 0;
        for (int j = 0; j < paramPlayer.getInventory().getSize(); j++) {
            ItemStack localItemStack = paramPlayer.getInventory().getItem(j);
            if (InventoryHandler.compareItemStacks(localItemStack, paramItemStack, paramBoolean)) {
                i += localItemStack.getAmount();
            }
        }
        return i;
    }

}
