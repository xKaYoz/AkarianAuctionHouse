package net.akarian.auctionhouse.commands.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.commands.CommandManager;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpSubCommand extends AkarianCommand {

    public HelpSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        int page = 1;
        Chat chat = AuctionHouse.getInstance().getChat();

        if(args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                chat.sendMessage(sender, "&cThe second argument must be an integer.");
                return;
            }
        }

        List<AkarianCommand> commands = new ArrayList<>();

        for(AkarianCommand command : CommandManager.getInstance().getCommands().values()) {
            if (sender.hasPermission(command.getPermission()) || sender.isOp()) commands.add(command);
        }

        if(commands.size() == 0) {
            chat.sendRawMessage(sender, "&8&m----------------------------------------");
            chat.sendRawMessage(sender, "&c&l  AuctionHouse Help Menu");
            chat.sendRawMessage(sender, "");
            chat.sendRawMessage(sender, "&cYou do not have any AuctionHouse permissions.");
            chat.sendRawMessage(sender, "&8&m----------------------------------------");
            return;
        }

        chat.sendRawMessage(sender, "&8&m----------------------------------------");
        chat.sendRawMessage(sender, "&c&l  AuctionHouse Help Menu &7(" + (page) + "/" + (commands.size() % 10 == 0 ? commands.size() / 10 : (commands.size() / 10) + 1) + ")");
        chat.sendRawMessage(sender, "");
        chat.sendRawMessage(sender, "&f  <> &8- &fRequired Commands");
        chat.sendRawMessage(sender, "&7  [] &8- &fOptional Commands.");
        chat.sendRawMessage(sender, "");

        int to = page * 10;
        int from = to - 10;

        if(commands.size() >= 10) {
            for (int i = from; i < to; i++) {
                if(commands.size() == i) break;
                AkarianCommand command = (AkarianCommand) commands.toArray()[i];

                if(command == null) break;

                chat.sendRawMessage(sender, "  &c" + command.getUsage() + " &8- &7" + command.getDescription());

            }
        } else {
            for(AkarianCommand command : commands) {
                chat.sendRawMessage(sender, "  &c" + command.getUsage() + " &8- &7" + command.getDescription());
            }
        }
        chat.sendRawMessage(sender, "&8&m----------------------------------------");
    }
}
