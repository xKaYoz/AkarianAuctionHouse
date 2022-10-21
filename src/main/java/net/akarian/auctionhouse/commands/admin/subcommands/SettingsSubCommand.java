package net.akarian.auctionhouse.commands.admin.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.SettingsGUI;
import net.akarian.auctionhouse.guis.admin.settings.MainSettingsGUI;
import net.akarian.auctionhouse.users.User;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsSubCommand extends AkarianCommand {

    public SettingsSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();


        if(!(sender instanceof Player)) {
            chat.sendMessage(sender, AuctionHouse.getInstance().getMessages().getError_player());
            return;
        }

        Player p = (Player) sender;

        //  /ah settings

        User user = AuctionHouse.getInstance().getUserManager().getUser(p);

        if(user == null) {
            return;
        }
        p.openInventory(new MainSettingsGUI().getInventory());
    }
}
