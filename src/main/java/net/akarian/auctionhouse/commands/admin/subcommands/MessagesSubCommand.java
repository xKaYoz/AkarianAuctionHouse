package net.akarian.auctionhouse.commands.admin.subcommands;

import net.akarian.auctionhouse.guis.admin.messages.MainMessageGUI;
import net.akarian.auctionhouse.utils.AkarianCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessagesSubCommand extends AkarianCommand {

    public MessagesSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 1 && sender instanceof Player) {
            ((Player) sender).openInventory(new MainMessageGUI().getInventory());
        }

    }
}
