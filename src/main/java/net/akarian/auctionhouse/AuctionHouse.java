package net.akarian.auctionhouse;

import lombok.Getter;
import lombok.Setter;
import net.akarian.auctionhouse.commands.admin.AHAdminCommand;
import net.akarian.auctionhouse.commands.admin.AdminCommandManager;
import net.akarian.auctionhouse.commands.main.AuctionHouseCommand;
import net.akarian.auctionhouse.commands.main.CommandManager;
import net.akarian.auctionhouse.cooldowns.CooldownManager;
import net.akarian.auctionhouse.events.AuctionHouseGUIEvents;
import net.akarian.auctionhouse.events.ExpireJoinEvent;
import net.akarian.auctionhouse.events.UpdateJoinEvent;
import net.akarian.auctionhouse.listings.ListingManager;
import net.akarian.auctionhouse.updater.UpdateManager;
import net.akarian.auctionhouse.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    @Setter
    private boolean update;
    @Getter
    private Messages messages;
    @Getter
    private Configuration configFile;
    @Getter
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "========== Akarian Auction House ==========");
        getLogger().log(Level.INFO, " ");
        getLogger().log(Level.INFO, "Loading Akarian Auction House v" + getDescription().getVersion() + "...");
        instance = this;
        this.fileManager = new FileManager(this);
        this.configFile = new Configuration();
        chat = new Chat(this, getConfigFile().getPrefix());
        nameManager = new NameManager();
        this.messages = new Messages();
        mySQL = new MySQL();
        update = getConfigFile().isUpdates();
        updateManager = new UpdateManager(this);
        guiManager = new GUIManager();
        cooldownManager = new CooldownManager();
        getLogger().log(Level.INFO, "Setting up Economy...");
        if (!setupEconomy()) {
            chat.alert("&cAuctionHouse has Failed to detect an economy.");
            chat.log("AuctionHouse disabled due to no found economy.");
            setEnabled(false);
            return;
        }
        getLogger().log(Level.INFO, "Successfully hooked into " + econ.getName() + ".");
        getLogger().log(Level.INFO, "Loading database...");
        // Set the database storage type
        switch (Objects.requireNonNull(getConfigFile().getDatabaseType().getStr()).toUpperCase(Locale.ROOT)) {
            case "FILE":
                databaseType = DatabaseType.FILE;
                if (!fileManager.getFile("/database/listings").exists()) {
                    fileManager.createFile("/database/listings");
                }
                if (!fileManager.getFile("/database/expired").exists()) {
                    fileManager.createFile("/database/expired");
                }
                if (!fileManager.getFile("/database/completed").exists()) {
                    fileManager.createFile("/database/completed");
                }
                break;
            case "MYSQL":
                databaseType = DatabaseType.MYSQL;
                mySQL.setup();
                break;

        }
        getLogger().log(Level.INFO, "Successfully connected to " + databaseType.getStr() + " database.");
        if (databaseType != DatabaseType.FILE) {
            if (mySQL.isConnected()) getLogger().log(Level.INFO, "Successfully loaded MySQL.");
            else getLogger().log(Level.SEVERE, "Failed loaded MySQL.");
        }
        getLogger().log(Level.INFO, "Loading listings...");
        this.listingManager = new ListingManager();
        getLogger().log(Level.INFO, "Listing loaded successfully.");
        registerCommands();
        registerEvents();

        if (getServer().getPluginManager().getPlugin("Citizens") != null && getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {
            net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(AuctionHouseTrait.class));
        }

        int pluginId = 15488;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.SingleLineChart("active_listings", () -> listingManager.getActive().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("expired_listings", () -> listingManager.getExpired().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("completed_listings", () -> listingManager.getCompleted().size()));
        metrics.addCustomChart(new Metrics.SimplePie("database_type", () -> databaseType.getStr()));
        getLogger().log(Level.INFO, "=================================================");
    }

    private void registerCommands() {
        new CommandManager();
        new AdminCommandManager();
        this.getCommand("auctionhouse").setExecutor(new AuctionHouseCommand());
        this.getCommand("auctionhouse").setTabCompleter(new AuctionHouseCommand());
        this.getCommand("ahadmin").setExecutor(new AHAdminCommand());
        this.getCommand("ahadmin").setTabCompleter(new AHAdminCommand());


    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AuctionHouseGUIEvents(), this);
        pm.registerEvents(new ExpireJoinEvent(), this);
        pm.registerEvents(new UpdateJoinEvent(), this);
        pm.registerEvents(guiManager, this);
    }

    @Override
    public void onDisable() {
        listingManager.cancelExpireTimer();
        listingManager.cancelRefreshTimer();
        if (databaseType != DatabaseType.FILE) {
            mySQL.shutdown();
        }
        guiManager.closeAllInventories();
        cooldownManager.saveCooldowns();
        zipLog();
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
        if (asOne) {
            local.setAmount(1);
        }
        String encodedObj;

        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
            os.writeObject(local);
            os.flush();

            byte[] serializedObj = io.toByteArray();

            encodedObj = Base64.getEncoder().encodeToString(serializedObj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return encodedObj;
    }

    /**
     * Decode a String to an ItemStack with Base64
     *
     * @param string - String of ItemStack encoded with Base64
     * @return - Decoded ItemStack
     */
    public ItemStack decode(String string) {
        try {
            byte[] serializedObj = Base64.getDecoder().decode(string);
            ByteArrayInputStream in = new ByteArrayInputStream(serializedObj);
            BukkitObjectInputStream is = new BukkitObjectInputStream(in);

            return (ItemStack) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void zipLog() {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();
            String formatted = String.format("%03d", 1);
            String cName = dtf.format(now) + "-" + formatted;
            String dateFile = dtf.format(now);
            if (!(new File(getDataFolder() + File.separator + "logs/" + dateFile).isDirectory()))
                new File(getDataFolder() + File.separator + "logs/" + dateFile).mkdir();
            //Setting the number after the log date
            if (new File(getDataFolder() + File.separator + "logs/" + dateFile + "/" + cName + ".zip").exists()) {
                for (int i = 2; i <= 100; i++) {
                    formatted = String.format("%03d", i);
                    cName = dtf.format(now) + "-" + formatted;
                    if (!(new File(getDataFolder() + File.separator + "logs/" + dateFile + "/" + cName + ".zip").exists())) {
                        break;
                    }
                }
            }

            String sourceFile = "logs/log.txt";
            FileOutputStream fos = new FileOutputStream(getDataFolder() + File.separator + "logs/" + dateFile + "/" + cName + ".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(getDataFolder() + File.separator + sourceFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            zipOut.close();
            fis.close();
            fos.close();
            if (fileToZip.delete()) AuctionHouse.getInstance().getLogger().log(Level.INFO, "Zipped log " + cName + ".");
        } catch (IOException ex) {
            chat.log("Error while zipping latest log.");
        }
    }

}
