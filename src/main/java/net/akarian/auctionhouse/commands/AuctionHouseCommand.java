package net.akarian.auctionhouse.commands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuctionHouseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String str, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();
        long start = System.currentTimeMillis();

        if (args.length == 0) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                p.openInventory(new AuctionHouseGUI(p, SortType.TIME_LEFT, true,  1).getInventory());
                return false;
            }
            CommandManager.getInstance().find("help").execute(sender, args);
            log(start, sender, CommandManager.getInstance().find("help"));
            return false;
        }

        AkarianCommand subCommand = CommandManager.getInstance().find(args[0]);

        if (subCommand == null) {

            for(String s : CommandManager.getInstance().getCommands().keySet()){
                AkarianCommand sc = CommandManager.getInstance().getCommands().get(s);

                for(String aliases : CommandManager.getInstance().getCommands().get(s).getAliases()) {
                    if(aliases.equalsIgnoreCase(args[0])) {
                        if (sender.hasPermission(sc.getPermission()) || sender.isOp()) {
                            sc.execute(sender, args);
                        } else {
                            chat.noPermission(sender);
                        }
                        log(start, sender, sc);
                        return false;
                    }
                }
            }

            chat.sendMessage(sender, "&cInvalid Command. Use /ah help for more info.");
            log(start, sender, null);
            return false;
        }

        if (sender.hasPermission(subCommand.getPermission()) || sender.isOp()) {
            subCommand.execute(sender, args);
        } else {
            chat.noPermission(sender);
        }
        log(start, sender, subCommand);

        return false;
    }

    private void log(long time, CommandSender sender, AkarianCommand command) {
        String str = command == null ? "UNKNOWN" : command.getName();
        AuctionHouse.getInstance().getChat().log(sender.getName() + " executed " + str + " in " + (System.currentTimeMillis() - time) + "ms.");
    }


}
