package net.akarian.auctionhouse.guis;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfirmListGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    private final ItemStack itemStack;
    private final double price;
    private Inventory inv;

    public ConfirmListGUI(Player player, ItemStack itemStack, double price) {
        this.player = player;
        this.itemStack = itemStack;
        this.price = price;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (item.getType()) {
            case LIME_STAINED_GLASS_PANE:
                player.closeInventory();

                String encoded = AuctionHouse.getInstance().encode(itemStack, false);
                ItemStack decoded = AuctionHouse.getInstance().decode(encoded);
                if (decoded == null || decoded.getType() == Material.AIR) {
                    chat.sendMessage(player, "There was an error creating this listing. Please try again.");
                    player.closeInventory();
                    return;
                }
                //Remove item from Inventory
                InventoryHandler.removeItemFromPlayer(p, itemStack, itemStack.getAmount(), true);
                AuctionHouse.getInstance().getListingManager().create(p.getUniqueId(), encoded, price);
                player.playSound(player.getLocation(), AuctionHouse.getInstance().getConfigFile().getCreateListingSound(), 5, 1);

                break;
            case RED_STAINED_GLASS_PANE:
                player.closeInventory();
                break;
        }
    }

    @Override
    public void updateInventory() {

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 9, chat.format(AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_CONFIRMLIST_TITLE)));

        for (int i = 0; i < 4; i++) {
            inv.setItem(i, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_CONFIRM_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_CONFIRM_LORE)));
        }


        List<String> lore = new ArrayList<>();
        for (String s : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_CONFIRMLIST_LORE)) {
            lore.add(s.replace("%amount%", chat.formatMoney(price)).replace("%fee%",
                    chat.formatMoney(AuctionHouse.getInstance().getConfigFile().calculateListingFee(price))));
        }

        inv.setItem(4, ItemBuilder.build(itemStack.getType(), 1, chat.formatItem(itemStack), lore));

        for (int i = 5; i < 9; i++) {
            inv.setItem(i, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_DENY_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_DENY_LORE)));
        }

        return inv;
    }
}
