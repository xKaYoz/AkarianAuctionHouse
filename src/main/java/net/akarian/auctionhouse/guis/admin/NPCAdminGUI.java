package net.akarian.auctionhouse.guis.admin;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.*;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NPCAdminGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final Player player;
    private final List<NPC> npcs;
    private int page;
    @Getter
    private Inventory inv;

    public NPCAdminGUI(Player player, int page) {
        this.player = player;
        this.page = page;
        this.npcs = new ArrayList<>();
    }

    public NPCAdminGUI setPage(int page) {
        this.page = page;
        return this;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 8:
                p.openInventory(new AuctionHouseAdminGUI().getInventory());
                return;
            case 45:
                p.openInventory(this.setPage(page - 1).getInventory());
                return;
            case 53:
                p.openInventory(this.setPage(page + 1).getInventory());
                return;
        }

        //Is a head
        if (slot >= 8 && slot <= 45 && item != null) {

            NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "id");
            ItemMeta itemMeta = item.getItemMeta();
            assert itemMeta != null;
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            NPC npc = CitizensAPI.getNPCRegistry().getById(container.get(key, new IntegerDataType()));
            if (container.has(key, new IntegerDataType())) {

                if (type.isLeftClick()) {

                    if (type.isShiftClick()) {
                        npc.removeTrait(AuctionHouseTrait.class);
                        chat.sendMessage(p, "&eYou have remove the \"auctioneer\" trait from this npc.");
                        return;
                    }

                    p.teleport(npc.getStoredLocation());
                    p.closeInventory();
                } else if (type.isRightClick() && type.isShiftClick()) {
                    npc.destroy(p);
                    chat.sendMessage(p, "&eYou have deleted this NPC.");
                }
            }
        }
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 54, chat.format("&6&lManage NPCs"));

        //Top Lining
        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        //Close Button
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_CLOSE_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_CLOSE_LORE)));

        //NPCs
        updateInventory();

        //Bottom Lining
        for (int i = 45; i <= 53; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_PPAGE_NAME, "%previous%;" + (page - 1), "%max%;" + (npcs.size() % 36 == 0 ? String.valueOf(npcs.size() / 36) : String.valueOf((npcs.size() / 36) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_PPAGE_LORE));
            inv.setItem(45, previous);
        }

        //Next Page
        if (npcs.size() > 36 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_NPAGE_NAME, "%next%;" + (page + 1), "%max%;" + (npcs.size() % 54 == 0 ? String.valueOf(npcs.size() / 54) : String.valueOf((npcs.size() / 54) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_NPAGE_LORE));
            inv.setItem(53, next);
        }

        return inv;
    }

    public void updateInventory() {

        List<NPC> tempnpcs = new ArrayList<>();

        for (NPC npc : CitizensAPI.getNPCRegistry()) {
            if (npc.hasTrait(AuctionHouseTrait.class)) {
                tempnpcs.add(npc);
            }
        }

        if (!npcs.equals(tempnpcs)) {
            npcs.clear();
            npcs.addAll(tempnpcs);
        } else {
            return;
        }

        int end = page * 36;
        int start = end - 36;
        int t = start;
        int slot = 9;

        for (int i = start; i <= end; i++) {
            if (npcs.size() == t || t >= end) {
                break;
            }
            NPC npc = npcs.get(i);
            ItemStack itemStack;

            try {
                itemStack = new ItemStack(Material.valueOf("PLAYER_HEAD"));
            } catch (IllegalArgumentException e) {
                itemStack = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
            }

            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(npc.getOrAddTrait(SkinTrait.class).getSkinName() == null ? npc.getName() : npc.getOrAddTrait(SkinTrait.class).getSkinName()));
            NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "id");
            meta.getPersistentDataContainer().set(key, new IntegerDataType(), npc.getId());

            List<String> lore = new ArrayList<>();

            for (String s : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_NPC_LORE)) {
                lore.add(s.replace("%x%", String.valueOf(npc.getStoredLocation().getBlockX())).replace("%y%", String.valueOf((int) npc.getStoredLocation().getY())).replace("%z%", String.valueOf(npc.getStoredLocation().getBlockZ())));
            }

            meta.setLore(chat.formatList(lore));

            itemStack.setItemMeta(meta);
            inv.setItem(slot, itemStack);



            slot++;
            t++;
        }
    }
}
