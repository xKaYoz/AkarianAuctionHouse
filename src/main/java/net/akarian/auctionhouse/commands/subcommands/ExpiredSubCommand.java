package net.akarian.auctionhouse.commands.subcommands;

import net.akarian.auctionhouse.guis.ExpireReclaimGUI;
import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.utils.AkarianCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpiredSubCommand extends AkarianCommand {

    public ExpiredSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.openInventory(new ExpireReclaimGUI(p, SortType.TIME_LEFT, true, 1, "", 1).getInventory());
            return;
        }
    }
}
