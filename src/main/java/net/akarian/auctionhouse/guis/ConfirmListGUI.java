package net.akarian.auctionhouse.guis;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
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
                AuctionHouse.getInstance().getListingManager().create(p.getUniqueId(), itemStack, price);
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
        inv = Bukkit.createInventory(this, 9, chat.format(AuctionHouse.getInstance().getMessages().getGui_cl_title()));

        inv.setItem(0, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_cn(), AuctionHouse.getInstance().getMessages().getGui_buttons_cd()));
        inv.setItem(1, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_cn(), AuctionHouse.getInstance().getMessages().getGui_buttons_cd()));
        inv.setItem(2, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_cn(), AuctionHouse.getInstance().getMessages().getGui_buttons_cd()));
        inv.setItem(3, ItemBuilder.build(Material.LIME_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_cn(), AuctionHouse.getInstance().getMessages().getGui_buttons_cd()));

        List<String> lore = new ArrayList<>();
        for(String s : AuctionHouse.getInstance().getMessages().getGui_cl_lore()) {
            lore.add(s.replace("%amount%", chat.formatMoney(price)).replace("%fee%", chat.formatMoney(AuctionHouse.getInstance().getConfigFile().calculateListingFee(price))));
        }

        inv.setItem(4, ItemBuilder.build(itemStack.getType(), 1, chat.formatItem(itemStack), lore));

        inv.setItem(5, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_dn(), AuctionHouse.getInstance().getMessages().getGui_buttons_dd()));
        inv.setItem(6, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_dn(), AuctionHouse.getInstance().getMessages().getGui_buttons_dd()));
        inv.setItem(7, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_dn(), AuctionHouse.getInstance().getMessages().getGui_buttons_dd()));
        inv.setItem(8, ItemBuilder.build(Material.RED_STAINED_GLASS_PANE, 1, AuctionHouse.getInstance().getMessages().getGui_buttons_dn(), AuctionHouse.getInstance().getMessages().getGui_buttons_dd()));


        return inv;
    }
}
