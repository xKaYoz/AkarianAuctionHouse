package net.akarian.auctionhouse.commands.admin.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.settings.MainSettingsGUI;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.FileManager;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SettingsSubCommand extends AkarianCommand {

    public SettingsSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();


        if(!(sender instanceof Player)) {
            chat.sendMessage(sender, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_PLAYER));
            return;
        }

        Player p = (Player) sender;

        //  /ah settings

        User user = AuctionHouse.getInstance().getUserManager().getUser(p);

        if (user == null) {
            return;
        }

        //  /ah settings force
        if (args.length == 2 && args[1].equalsIgnoreCase("force")) {
            FileManager fm = AuctionHouse.getInstance().getFileManager();
            YamlConfiguration usersFile = fm.getConfig("/database/users");

            AuctionHouse.getInstance().getConfigFile().reloadConfig();

            int changed = 0;
            int active = 0;

            for (String s : usersFile.getKeys(false)) {
                usersFile.set(s + ".Alert Create Listings", AuctionHouse.getInstance().getConfigFile().isDps_create());
                usersFile.set(s + ".Open Admin Mode", AuctionHouse.getInstance().getConfigFile().isDps_adminMode());
                usersFile.set(s + ".Alert Near Expire.Status", AuctionHouse.getInstance().getConfigFile().isDps_expire());
                usersFile.set(s + ".Alert Near Expire.Time", AuctionHouse.getInstance().getConfigFile().getDps_expireTime());
                usersFile.set(s + ".Alert Listing Bought", AuctionHouse.getInstance().getConfigFile().isDps_bought());
                usersFile.set(s + ".Auto Confirm Listing", AuctionHouse.getInstance().getConfigFile().isDps_autoConfirm());
                changed++;
            }
            fm.saveFile(usersFile, "/database/users");
            for (User u : AuctionHouse.getInstance().getUserManager().getUsers()) {
                u.getUserSettings().load();
                active++;
            }
            chat.sendMessage(p, "&fForced update of &e" + changed + "&f total users, &e" + active + "&f active users.");
        } else {
            p.openInventory(new MainSettingsGUI().getInventory());
        }
    }
}
