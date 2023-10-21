package net.akarian.auctionhouse.guis.admin.blacklist;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.AuctionHouseAdminGUI;
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

import java.util.Collections;

public class BlacklistMainGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;
    private final Player player;

    public BlacklistMainGUI(Player player) {
        this.player = player;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                p.openInventory(new AuctionHouseAdminGUI().getInventory());
                break;
            case 11:
                ItemStack hand = p.getInventory().getItemInMainHand();
                if (hand != null)
                    p.openInventory(new BlacklistAdminGUI(hand).getInventory());
                else {
                    chat.sendMessage(p, "&cYou must be holding an item in your hand to add to the blacklist.");
                    return;
                }
                break;
            case 15:
                p.openInventory(new BlacklistViewSelectGUI().getInventory());
                break;

        }

    }

    @Override
    public void updateInventory() {

        inv.setItem(11, ItemBuilder.build(player.getInventory().getItemInMainHand().getType() == Material.AIR ? Material.STONE : player.getInventory().getItemInMainHand().getType(), 1, "&6Add to the Blacklist", Collections.singletonList("&7Click to add an item to the blacklist.")));
        inv.setItem(15, ItemBuilder.build(Material.BOOKSHELF, 1, "&6View and Edit the Blacklist", Collections.singletonList("&7Click to view or remove an item from the blacklist.")));

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 27, chat.format("&6Blacklist Main Menu"));

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
