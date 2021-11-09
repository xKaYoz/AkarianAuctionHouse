package net.akarian.auctionhouse.commands.subcommands;

import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.guis.admin.AuctionHouseAdminGUI;
import net.akarian.auctionhouse.utils.AkarianCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminSubCommand extends AkarianCommand {

    public AdminSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(sender instanceof Player) {
            Player p = (Player) sender;
            p.openInventory(new AuctionHouseAdminGUI(p, SortType.TIME_LEFT, true,  1).getInventory());
        }

    }
}
