package net.akarian.auctionhouse.commands.admin.subcommands;

import net.akarian.auctionhouse.guis.admin.edit.LayoutSelectGUI;
import net.akarian.auctionhouse.utils.AkarianCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LayoutSubCommand extends AkarianCommand {


    public LayoutSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("auctionhouse.admin.edit")) {
                player.openInventory(new LayoutSelectGUI(player, 1).getInventory());
            }
        }
    }
}
