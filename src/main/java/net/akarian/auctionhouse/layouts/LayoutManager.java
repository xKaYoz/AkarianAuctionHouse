package net.akarian.auctionhouse.layouts;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.MessageType;
import org.bukkit.Material;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LayoutManager {

    @Getter @Setter
    public Layout activeLayout;

    @Getter
    private final List<Layout> layouts;
    private final Chat chat = AuctionHouse.getInstance().getChat();

    public LayoutManager() {
        this.layouts = new ArrayList<>();
        loadAllLayouts();
    }

    private void loadAllLayouts() {
        if(!(new File(AuctionHouse.getInstance().getDataFolder() + File.separator + "layouts").isDirectory())) {
            if(new File(AuctionHouse.getInstance().getDataFolder() + File.separator + "layouts").mkdirs()) {
                chat.log("Created layouts folder.", AuctionHouse.getInstance().isDebug());
                activeLayout = createDefaultLayout("default", true);
                return;
            }
        }
        int loaded = 0;
        for (File file : new File(AuctionHouse.getInstance().getDataFolder() + File.separator + "layouts").listFiles()) {
            Layout layout = new Layout(UUID.fromString(file.getName().replace(".yml", "")), "");
            layout.loadLayout();
            register(layout);
            if (layout.isActive()) {
                activeLayout = layout;
            }
            chat.log("Loaded layout " + layout.getName() + ".", AuctionHouse.getInstance().isDebug());
            loaded++;
        }
        if (loaded == 0) {
            setActiveLayout(createDefaultLayout("default", true));
            chat.log("Found no layouts so a default one was created.", true);
            loaded++;
        }
        if (activeLayout == null) {
            setActiveLayout(createDefaultLayout("default", true));
            chat.log("Found no active layouts so a default one was created.", true);
            loaded++;
        }
        chat.log("Loaded " + loaded + " layouts.", AuctionHouse.getInstance().isDebug());
    }

    public void saveAllLayouts() {
        int saved = 0;
        for(Layout layout : layouts) {
            layout.saveLayout();
            chat.log("Saved layout " + layout.getName() + ".", false);
            saved++;
        }
        chat.log("Saved " + saved + " layouts.", true);
    }

    public Layout getLayout(UUID uuid) {
        for(Layout l : layouts) {
            if(l.getUuid().toString().equalsIgnoreCase(uuid.toString())) return l;
        }
        return null;
    }

    public void register(Layout layout) {
        layouts.add(layout);
    }

    public void unregister(Layout layout) {
        layout.delete();
        layouts.remove(layout);
    }

    public Layout createDefaultLayout(String name, boolean active) {
        Layout layout = new Layout(UUID.randomUUID(), name);

        List<Integer> listingSlots = new ArrayList<>();
        List<Integer> spacerSlots = new ArrayList<>();
        List<Integer> previousPage = new ArrayList<>();
        List<Integer> nextPage = new ArrayList<>();
        for (int i = 9; i <= 44; i++) {
            listingSlots.add(i);
        }
        for (int i = 0; i <= 7; i++) {
            spacerSlots.add(i);
        }
        for (int i = 45; i <= 53; i++) {
            spacerSlots.add(i);
        }
        previousPage.add(45);
        nextPage.add(53);
        layout.setListingItems(listingSlots);
        layout.setSpacerItems(spacerSlots);

        layout.setInventoryName(AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_TITLE));
        layout.setInventorySize(54);

        layout.setAdminButton(1);
        layout.setExitButton(8);
        layout.setPreviousPageButtons(previousPage);
        layout.setNextPageButtons(nextPage);
        layout.setSearchButton(46);
        layout.setInfoButton(48);
        layout.setExpiredItemsButton(50);
        layout.setSortButton(52);

        layout.setActive(active);
        layout.setDisplayType(Material.PAPER);

        layout.setSpacerPageItems(true);

        layout.saveLayout();

        register(layout);
        return layout;
    }

}
