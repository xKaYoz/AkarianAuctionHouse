package net.akarian.auctionhouse.guis.admin.messages;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class LanguageFileGUI implements AkarianInventory {

    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        if (slot == 8) {
            p.openInventory(new MainMessageGUI().getInventory());
            return;
        }

        if (slot >= 9 && slot <= 35 && item != null) {

            AuctionHouse plugin = AuctionHouse.getInstance();

            String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
            FileManager fm = AuctionHouse.getInstance().getFileManager();

            try {
                File messagesFile = fm.getFile("messages");
                fm.copyInputStreamToFile(Objects.requireNonNull(plugin.getResource(name + ".yml")), messagesFile);
            } catch (IOException ex) {
                plugin.getChat().log("Failed to load " + name + ".yml lang file.", true);
                return;
            }

            YamlConfiguration config = fm.getConfig("config");
            config.set("Language", name);
            plugin.getMessageManager().setFileName(name);
            fm.saveFile(config, "config");

            p.closeInventory();
            chat.sendMessage(p, "&7Changed to &e" + name + "&7.");

        }

    }

    @Override
    public void updateInventory() {

        int start = 9;
        for (String s : getLanguageFiles()) {
            inv.setItem(start, ItemBuilder.build(Material.PAPER, 1, "&e" + s, Collections.singletonList("&7Click to select")));
            start++;
        }

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 45, chat.format("&6Language Selector"));

        for (int i = 0; i <= 7; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }
        inv.setItem(8, ItemBuilder.build(Material.BARRIER, 1, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.BUTTONS_RETURN_NAME), AuctionHouse.getInstance().getMessageManager().getLore(MessageType.BUTTONS_RETURN_LORE)));
        for (int i = 36; i <= 44; i++) {
            inv.setItem(i, ItemBuilder.build(AuctionHouse.getInstance().getConfigFile().getSpacerItem(), 1, " ", Collections.EMPTY_LIST));
        }

        updateInventory();

        return inv;
    }

    private ArrayList<String> getLanguageFiles() {
        ArrayList<String> list = new ArrayList<>();

        File langFolder = new File(AuctionHouse.getInstance().getDataFolder().getPath() + "/lang");
        for (File file : langFolder.listFiles()) {
            list.add(file.getName().replace(".yml", ""));
        }

        return list;
    }
}
