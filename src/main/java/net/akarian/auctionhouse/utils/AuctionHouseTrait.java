package net.akarian.auctionhouse.utils;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.SortType;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.event.EventHandler;

@TraitName("auctioneer")
public class AuctionHouseTrait extends Trait {

    public AuctionHouseTrait() {
        super("auctioneer");
    }

    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event) {
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!
        if (event.getNPC() != this.getNPC()) return;

        event.getClicker().openInventory(new AuctionHouseGUI(event.getClicker(), SortType.TIME_LEFT, true, 1).getInventory());

        AuctionHouse.getInstance().getChat().log(event.getClicker().getName() + " opened the AuctionHouse via NPC.");

    }
}
