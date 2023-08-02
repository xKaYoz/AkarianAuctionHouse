package net.akarian.auctionhouse.guis.admin.blacklist;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlacklistViewMaterialGUI implements AkarianInventory {


    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;
    private int page;
    private List<Material> blacklistedMaterials;

    public BlacklistViewMaterialGUI(int page) {
        this.page = page;
        this.blacklistedMaterials = new ArrayList<>();
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {
        switch (slot) {
            case 8:
                p.openInventory(new BlacklistViewSelectGUI().getInventory());
            case 45:
                if (page == 1) return;
                page--;
                updateInventory();
                return;
            case 53:
                if (blacklistedMaterials.size() > 36 * page) {
                    page++;
                    updateInventory();
                }
                return;
        }

        if (slot >= 8 && slot <= 45) {
            AuctionHouse.getInstance().getListingManager().getBlacklistedMaterials().remove(item.getType());
            updateInventory();
        }
    }

    @Override
    public void updateInventory() {
        blacklistedMaterials = AuctionHouse.getInstance().getListingManager().getBlacklistedMaterials();

        int amountToDisplay;
        int amountCanDisplay = 36;
        int end = page * amountCanDisplay;
        int displayStart = end - amountCanDisplay;
        int tick = 0;
        amountToDisplay = Math.min(blacklistedMaterials.size(), amountCanDisplay);
        ArrayList<ItemStack> displayItems = getDisplays(displayStart, amountToDisplay);
        for (int i = displayStart + 9; i <= end; i++) {
            if (displayItems.size() <= tick) {
                inv.setItem(i, null);
            } else
                inv.setItem(i, displayItems.get(tick));
            tick++;
        }


        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_PPAGE_NAME, "%previous%;" + (page - 1), "%max%;" + (blacklistedMaterials.size() % 36 == 0 ? String.valueOf(blacklistedMaterials.size() / 36) : String.valueOf((blacklistedMaterials.size() / 36) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_PPAGE_LORE));
            inv.setItem(45, previous);
        }

        //Next Page
        if (blacklistedMaterials.size() > 36 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_NPAGE_NAME, "%next%;" + (page + 1), "%max%;" + (blacklistedMaterials.size() % 36 == 0 ? String.valueOf(blacklistedMaterials.size() / 36) : String.valueOf((blacklistedMaterials.size() / 36) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_NPAGE_LORE));
            inv.setItem(53, next);
        }

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format("&cBlacklisted Materials"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

        return inv;
    }


    public ArrayList<ItemStack> getDisplays(int start, int amount) {
        ArrayList<ItemStack> displays = new ArrayList<>();
        List<Material> blacklisted = blacklistedMaterials;


        for (int i = start; i < start + amount; i++) {

            if (i >= blacklisted.size()) break;
            Material s = blacklisted.get(i);

            displays.add(ItemBuilder.build(s, 1, chat.format(s.name()), Collections.singletonList("&7Click to remove.")));
        }

        return displays;
    }
}
