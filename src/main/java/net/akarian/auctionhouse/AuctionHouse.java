package net.akarian.auctionhouse;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.commands.AuctionHouseCommand;
import net.akarian.auctionhouse.commands.CommandManager;
import net.akarian.auctionhouse.events.AuctionHouseGUIEvents;
import net.akarian.auctionhouse.events.ExpireJoinEvent;
import net.akarian.auctionhouse.listings.ListingManager;
import net.akarian.auctionhouse.updater.UpdateManager;
import net.akarian.auctionhouse.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

public final class AuctionHouse extends JavaPlugin {

    @Getter
    private Economy econ;
    @Getter
    private static AuctionHouse instance;
    @Getter
    private ListingManager listingManager;
    @Getter
    private Chat chat;
    @Getter
    @Setter
    private DatabaseType databaseType;
    @Getter
    private NameManager nameManager;
    @Getter
    private MySQL mySQL;
    @Getter
    private FileManager fileManager;
    @Getter
    private GUIManager guiManager;
    @Getter
    private UpdateManager updateManager;
    @Getter
    private boolean update;
    @Getter
    private Messages messages;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        chat = new Chat(this, getConfig().getString("Prefix"));
        nameManager = new NameManager();
        this.fileManager = new FileManager(this);
        this.messages = new Messages();
        mySQL = new MySQL();
        update = getConfig().getBoolean("updates");
        updateManager = new UpdateManager(this);
        guiManager = new GUIManager();

        if (!setupEconomy()) {
            chat.alert("&cAuctionHouse has Failed to detect an economy.");
            chat.log("AuctionHouse disabled due to no found economy.");
            setEnabled(false);
            return;
        }

        // Set the database storage type
        switch (Objects.requireNonNull(getConfig().getString("database")).toUpperCase(Locale.ROOT)) {
            case "FILE":
                databaseType = DatabaseType.FILE;
                if(!fileManager.getFile("/database/listings").exists()) {
                    fileManager.createFile("/database/listings");
                }
                if(!fileManager.getFile("/database/expired").exists()) {
                    fileManager.createFile("/database/expired");
                }
                if(!fileManager.getFile("/database/completed").exists()) {
                fileManager.createFile("/database/completed");
                }

                break;
            case "FILE2MYSQL":
                databaseType = DatabaseType.FILE2MYSQL;
                mySQL.setup();
                break;
            case "MYSQL":
                databaseType = DatabaseType.MYSQL;
                mySQL.setup();
                break;
            case "MYSQL2FILE":
                databaseType = DatabaseType.MYSQL2FILE;
                mySQL.setup();
                break;

        }

        this.listingManager = new ListingManager();

        registerCommands();
        registerEvents();

    }

    private void registerCommands() {
        new CommandManager();
        this.getCommand("auctionhouse").setExecutor(new AuctionHouseCommand());
    }
    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AuctionHouseGUIEvents(), this);
        pm.registerEvents(new ExpireJoinEvent(), this);
        pm.registerEvents(guiManager, this);
    }

    @Override
    public void onDisable() {
        if(databaseType != DatabaseType.FILE) {
            mySQL.shutdown();
        }
        guiManager.closeAllInventories();
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public String encode(ItemStack itemStack, boolean asOne) {
        ItemStack local = itemStack;
        if(asOne) {
            local = local.asOne();
        }
        return new String(Base64.getEncoder().encode(local.serializeAsBytes()));
    }

    /**
     * Decode a String to an ItemStack with Base64
     *
     * @param string - String of ItemStack encoded with Base64
     * @return - Decoded ItemStack
     */
    public ItemStack decode(String string) {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(string.getBytes()));
    }

}
