package net.akarian.auctionhouse.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private File file;
    private YamlConfiguration config;
    private Plugin plugin;

    public FileManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void createFile(String name) {
        this.file = new File(plugin.getDataFolder(), name + ".yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!this.file.exists()) {
            try {
                this.file.getParentFile().mkdirs();
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.config = YamlConfiguration.loadConfiguration(file);
        } else {
            try {
                throw new IOException();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean saveFile(YamlConfiguration config, String name) {
        try {
            config.save(getFile(name));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getFile(String name) {
        this.file = new File(plugin.getDataFolder(), name + ".yml");

        return this.file;
    }

    public YamlConfiguration getConfig(String name) {
        this.file = new File(plugin.getDataFolder(), name + ".yml");

        this.config = YamlConfiguration.loadConfiguration(this.file);
        return this.config;
    }

}

