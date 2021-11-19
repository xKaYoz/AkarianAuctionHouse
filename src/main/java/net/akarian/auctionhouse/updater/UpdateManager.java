package net.akarian.auctionhouse.updater;


import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.command.CommandSender;


public class UpdateManager {

    private final int spigotID = 97504;
    private final AuctionHouse plugin;
    private final Chat chat;
    @Getter
    private Updater updater;

    public UpdateManager(AuctionHouse plugin) {
        this.plugin = plugin;
        this.chat = plugin.getChat();

        if (plugin.isUpdate()) {
            reloadUpdater();
            if (isUpdate() == 2) {
                chat.alert("&eAn update has been found.");
                chat.log("Update found");
            }
        } else {
            chat.log("Updates are disabled. Disabling Update Manager");
        }
    }

    private void reloadUpdater() {
        if (plugin.isUpdate())
            updater = new Updater(plugin, spigotID, plugin.getDataFolder(), Updater.UpdateType.VERSION_CHECK, false, false);
    }

    public int isUpdate() {
        if (!plugin.isUpdate()) return -1;
        reloadUpdater();
        if (updater.getResult() == Updater.Result.BAD_ID) {
            return 0;
        } else if (updater.getResult() == Updater.Result.NO_UPDATE) {
            return 1;
        } else if (updater.getResult() == Updater.Result.UPDATE_FOUND) {
            return 2;
        } else {
            return 3;
        }
    }

    public void update(CommandSender sender, boolean restart) {

        if (restart) {
            chat.sendRawMessage(sender, "&8&m&l------------------------------------");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "  &6&lAuctionHouse &fUpdater");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "&aThe plugin is updating. When complete, the server will restart.");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "&8&m&l------------------------------------");
        } else {
            chat.sendRawMessage(sender, "&8&m&l------------------------------------");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "  &6&lAuctionHouse &fUpdater");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "&8(&c&l!&8) &6The plugin is updating. When complete, please restart your server.");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "&8&m&l------------------------------------");
        }

        updater = new Updater(plugin, spigotID, plugin.getDataFolder(), Updater.UpdateType.CHECK_DOWNLOAD, true, restart);

        if (updater.getResult() == Updater.Result.SUCCESS && !restart) {
            chat.sendRawMessage(sender, "&8&m&l------------------------------------");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "  &6&lAuctionHouse &fUpdater");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "&8(&c&l!&8) &eThe plugin is finished updating. Please restart the server.");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "&8&m&l------------------------------------");
        } else if (updater.getResult() == Updater.Result.FAILED) {
            chat.alert("&cAn error has occurred whilst trying to update and failed.");
        }
    }

}
