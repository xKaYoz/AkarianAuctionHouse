package net.akarian.auctionhouse.layouts;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.FileManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.UUID;

public class Layout {

    //Inventory variables
    @Getter @Setter
    private int adminButton, exitButton, previousPageButton, nextPageButton, searchButton, infoButton, expiredItemsButton, sortButton, inventorySize;
    @Getter @Setter
    private List<Integer> listingItems, spacerItems;
    @Getter @Setter
    private String inventoryName;

    private final Chat chat = AuctionHouse.getInstance().getChat();
    private final FileManager fm = AuctionHouse.getInstance().getFileManager();
    @Getter @Setter
    private String name;
    @Getter @Setter
    private boolean active;
    @Getter @Setter
    private Material displayType;
    @Getter
    private final UUID uuid;
    @Getter @Setter
    private boolean spacerPageItems;

    public Layout(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    public void loadLayout() {
        YamlConfiguration config = fm.getConfig("/layouts/" + uuid.toString());

        name = config.getString("Name");
        inventoryName = config.getString("Inventory Name");
        inventorySize = config.getInt("Inventory Size");

        adminButton = config.getInt("Admin Button");
        exitButton = config.getInt("Exit Button");
        previousPageButton = config.getInt("Previous Page Button");
        nextPageButton = config.getInt("Next Page Button");
        searchButton = config.getInt("Search Button");
        infoButton = config.getInt("Info Button");
        expiredItemsButton = config.getInt("Expired Items Button");
        sortButton = config.getInt("Sort Button");

        listingItems = config.getIntegerList("Listing Item Slots");
        spacerItems = config.getIntegerList("Spacer Item Slots");
        active = config.getBoolean("Active");
        displayType = Material.valueOf(config.getString("Display Type"));

        spacerPageItems = config.getBoolean("Spacer Page Items");
    }

    public void saveLayout() {
        if(!fm.getFile("/layouts/" + uuid.toString()).exists()) {
            fm.createFile("/layouts/" + uuid.toString());
        }
        YamlConfiguration config = fm.getConfig(uuid.toString());

        config.set("Name", name);
        config.set("Active", active);
        config.set("Inventory Name", inventoryName);
        config.set("Inventory Size", inventorySize);
        config.set("Admin Button", adminButton);
        config.set("Exit Button", exitButton);
        config.set("Previous Page Button", previousPageButton);
        config.set("Next Page Button", nextPageButton);
        config.set("Search Button", searchButton);
        config.set("Info Button", infoButton);
        config.set("Expired Items Button", expiredItemsButton);
        config.set("Sort Button", sortButton);

        config.set("Listing Item Slots", listingItems);
        config.set("Spacer Item Slots", spacerItems);
        config.set("Display Type", displayType.name());
        config.set("Spacer Page Items", spacerPageItems);

        fm.saveFile(config, "/layouts/" + uuid.toString());
    }

    public void delete() {
        if(fm.getFile("/layouts/" + uuid.toString()).delete()) {
            chat.log("Deleted layout " + name + " (" + uuid.toString() + ").", true);
        }
    }

}
