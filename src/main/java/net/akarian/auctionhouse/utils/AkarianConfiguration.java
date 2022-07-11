package net.akarian.auctionhouse.utils;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AkarianConfiguration extends YamlConfiguration {

    List<String> test = new ArrayList<>();

    public static AkarianConfiguration loadConfiguration(File file) {
        Validate.notNull(file, "File cannot be null");

        AkarianConfiguration config = new AkarianConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException ignored) {
        } catch (IOException | InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }

        return config;
    }

    @Override
    protected String buildHeader() {

        StringBuilder sb = new StringBuilder();
        boolean startedHeader = false;

        for (int i = test.size() - 1; i >= 0; i--) {
            sb.insert(0, "\n");
            if ((startedHeader) || test.size() != 0) {
                sb.insert(0, test.get(i));
                sb.insert(0, "# ");
                startedHeader = true;
            }
        }
        return sb.toString();
    }

    public void setHeader(List<String> list) {
        test = list;
    }
}
