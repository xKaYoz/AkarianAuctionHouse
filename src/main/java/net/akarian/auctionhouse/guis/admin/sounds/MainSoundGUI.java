package net.akarian.auctionhouse.guis.admin.sounds;

import lombok.Getter;
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

public class MainSoundGUI implements AkarianInventory {

    @Getter
    private final Player player;
    private final Chat chat = AuctionHouse.getInstance().getChat();
    @Getter
    private Inventory inv;
    private SoundSelectGUI selectGUI;

    public MainSoundGUI(Player player) {
        this.player = player;
        this.selectGUI = null;
    }

    @Override
    public void onGUIClick(Inventory inv, Player p, int slot, ItemStack item, ClickType type) {

        switch (slot) {
            case 0:
                if (selectGUI == null)
                    player.openInventory((selectGUI = new SoundSelectGUI(player, "Create Listing", this)).select(AuctionHouse.getInstance().getConfigFile().getListingBoughtSound()).getInventory());
                else
                    player.openInventory(selectGUI.changeSound("Create Listing").select(AuctionHouse.getInstance().getConfigFile().getCreateListingSound()).getInventory());
                break;
            case 1:
                if (selectGUI == null)
                    player.openInventory((selectGUI = new SoundSelectGUI(player, "Listing Bought", this)).select(AuctionHouse.getInstance().getConfigFile().getListingBoughtSound()).getInventory());
                else
                    player.openInventory(selectGUI.changeSound("Listing Bought").select(AuctionHouse.getInstance().getConfigFile().getListingBoughtSound()).getInventory());
                break;
        }

    }

    @Override
    public void updateInventory() {

    }

    @NotNull
    @Override
    public Inventory getInventory() {
        inv = Bukkit.createInventory(this, 36, chat.format(AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_SOUND_TITLE)));

        inv.setItem(0, ItemBuilder.build(Material.MUSIC_DISC_FAR, 1, "Create Listing Sound", Arrays.asList("&7Sound played when player creates a listing.", "&7Current: &e" + AuctionHouse.getInstance().getConfigFile().getCreateListingSound())));
        inv.setItem(1, ItemBuilder.build(Material.MUSIC_DISC_FAR, 1, "Listing Bought Sound", Arrays.asList("&7Sound played on creator when listing is bought.", "&7Current: &e" + AuctionHouse.getInstance().getConfigFile().getListingBoughtSound())));

        return inv;
    }
}
