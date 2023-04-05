package net.akarian.auctionhouse.commands.admin.subcommands;

import net.akarian.auctionhouse.guis.admin.NPCAdminGUI;
import net.akarian.auctionhouse.utils.AkarianCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCSubCommand extends AkarianCommand {

    public NPCSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("auctionhouse.admin.npc")) {
                player.openInventory(new NPCAdminGUI(player, 1).getInventory());
            }
        }
    }
}
