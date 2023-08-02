package net.akarian.auctionhouse.guis.admin.sounds;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.AkarianInventory;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.ItemBuilder;
import net.akarian.auctionhouse.utils.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SoundSelectGUI implements AkarianInventory {

    @Getter
    private static final HashMap<UUID, SoundSelectGUI> searchMap = new HashMap<>();
    @Getter
    private final Player player;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    ArrayList<Sound> sounds;
    Sound selected;
    @Getter
    private Inventory inv;
    @Getter
    private int page;
    @Getter
    private String searchStr = "";
    private String changeSound;
    private final MainSoundGUI previousGUI;

    public SoundSelectGUI(Player player, String str, MainSoundGUI gui) {
        this.player = player;
        sounds = new ArrayList<>();
        searchSounds();
        page = 1;
        changeSound = str;
        previousGUI = gui;
    }

    public SoundSelectGUI changeSound(String str) {
        changeSound = str;
        return this;
    }

    public SoundSelectGUI search(String searchStr) {
        this.searchStr = searchStr;
        this.page = 1;
        searchSounds();
        return this;
    }

    public SoundSelectGUI select(Sound sound) {
        selected = sound;

        int location = sounds.indexOf(sound);
        if (location <= 45) {
            page = 1;
            return this;
        }

        this.page = (sounds.indexOf(sound) + 45) / 45;

        return this;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        if (slot <= 44 && item != null) {
            selected = Sound.valueOf(ChatColor.stripColor(Objects.requireNonNull(item.getItemMeta()).getDisplayName()));
            p.playSound(p.getLocation(), selected, 10, 5);
            return;
        }

        switch (slot) {
            case 45:
                player.openInventory(previousGUI.getInventory());
                break;
            //Search Button
            case 46:
                player.closeInventory();
                searchMap.put(player.getUniqueId(), this);
                chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_SEARCH_LEFT));
                chat.sendMessage(player, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_SEARCH_RIGHT));
                break;
            case 49:
                if (selected == null) break;
                switch (changeSound) {
                    case "Create Listing":
                        AuctionHouse.getInstance().getConfigFile().setCreateListingSound(selected);
                        break;
                    case "Listing Bought":
                        AuctionHouse.getInstance().getConfigFile().setListingBoughtSound(selected);
                        break;
                }
                player.openInventory(previousGUI.getInventory());
                chat.sendMessage(player, "&eSaved sound.");
                AuctionHouse.getInstance().getConfigFile().saveConfig();
                break;
            case 52:
                if (page == 1) break;
                page--;
                updateInventory();
                break;
            case 53:
                if (!(sounds.size() > 45 * page)) break;
                page++;
                updateInventory();
                break;

        }
    }

    public void searchSounds() {
        sounds.clear();
        if (searchStr.equalsIgnoreCase("")) {
            sounds.addAll(Arrays.asList(Sound.values()));
            return;
        }
        ArrayList<Sound> searchedSounds = new ArrayList<>();
        for (Sound sound : Sound.values()) {
            if (sound.name().contains(searchStr.toUpperCase())) searchedSounds.add(sound);
        }
        sounds = searchedSounds;
    }

    @Override
    public void updateInventory() {

        int end = page * 45;
        int display = end - 45;

        for (int i = 0; i < 45; i++) {
            if (sounds.size() == 0 || display >= end || sounds.size() == display) {
                break;
            }
            Sound sound = sounds.get(display);
            inv.setItem(i, ItemBuilder.build(Material.MUSIC_DISC_CAT, 1, sound.name(), Collections.singletonList("&7Click to hear sound"), selected != null && selected == sound ? "shine" : ""));
            display++;
        }

        if (selected == null) {
            inv.setItem(49, ItemBuilder.build(Material.GRAY_DYE, 1, "&cSelect a sound to confirm", Collections.singletonList("&eYou must select a sound before you can confirm it.")));
        } else {
            inv.setItem(49, ItemBuilder.build(Material.LIME_DYE, 1, "&cConfirm", Collections.singletonList("&eClick to confirm the use of &6" + selected.name() + "&e.")));
        }

        //Previous Page
        if (page != 1) {
            ItemStack previous = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_PPAGE_NAME, "%previous%;" + (page - 1), "%max%;" + (sounds.size() % 45 == 0 ? String.valueOf(sounds.size() / 45) : String.valueOf((sounds.size() / 45) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_PPAGE_LORE));
            inv.setItem(52, previous);
        } else {
            inv.setItem(52, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        //Next Page
        if (sounds.size() > 45 * page) {
            ItemStack next = ItemBuilder.build(Material.NETHER_STAR, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_NPAGE_NAME, "%next%;" + (page + 1), "%max%;" + (sounds.size() % 45 == 0 ? String.valueOf(sounds.size() / 45) : String.valueOf((sounds.size() / 45) + 1))),
                    AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_NPAGE_LORE));
            inv.setItem(53, next);
        } else {
            inv.setItem(53, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

    }

    @NotNull
    @Override
    public Inventory getInventory() {

        inv = Bukkit.createInventory(this, 54, chat.format("&eSelect " + changeSound + " Sound"));

        //Spacer Items
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.emptyList()));
        }

        updateInventory();

        inv.setItem(45, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));
        inv.setItem(46, ItemBuilder.build(Material.HOPPER, 1, "&6Filter sounds", Collections.singletonList("&eClick to filter the name of the sounds.")));
        inv.setItem(47, ItemBuilder.build(Material.MAGENTA_DYE, 1, "&5Change Volume and Pitch", Collections.singletonList("&7Click to change volume and pitch")));
        return inv;
    }
}
