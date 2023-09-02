package net.akarian.auctionhouse.guis;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class ListingBiddingGUI implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, ListingBiddingGUI> startingBidMap = new HashMap<>();
    @Getter
    private static final HashMap<UUID, ListingBiddingGUI> minIncMap = new HashMap<>();
    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final ListingMainGUI mainGUI;
    @Getter
    private Inventory inv;
    @Getter
    @Setter
    private double startingBid;
    @Getter
    @Setter
    private double minimumIncrement;

    public ListingBiddingGUI(ListingMainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                p.openInventory(mainGUI.getInventory());
                break;
            case 11:
                startingBidMap.put(p.getUniqueId(), this);
                p.closeInventory();
                chat.sendMessage(p, "&eEnter the amount for the starting bid...");
                chat.sendMessage(p, "&eType \"cancel\" or left click to exit.");
                break;
            case 15:
                minIncMap.put(p.getUniqueId(), this);
                p.closeInventory();
                chat.sendMessage(p, "&eEnter the amount for the minimum increment...");
                chat.sendMessage(p, "&eType \"cancel\" or left click to exit.");
                break;
        }

    }

    @Override
    public void updateInventory() {
        inv.setItem(11, ItemBuilder.build(Material.GOLD_INGOT, 1, "&eStarting Bid", Arrays.asList("&fCurrent: &2" + chat.formatMoney(startingBid), "", "&7Click to set a starting bid.")));
        inv.setItem(15, ItemBuilder.build(Material.PAPER, 1, "&eMinimum Increment", Arrays.asList("&fCurrent: &2" + chat.formatMoney(minimumIncrement), "", "&7Click to set a minimum increment for bids.")));
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format("&6&lBidding Options"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));
        for (int i = 18; i <= 26; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

        return inv;
    }
}
