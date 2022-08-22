package net.akarian.auctionhouse.commands.main;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AuctionHouseCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String str, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();
        long start = System.currentTimeMillis();

        if (args.length == 0) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.openInventory(new AuctionHouseGUI(p, SortType.TIME_LEFT, true,  1).getInventory());
                return false;
            }
            CommandManager.getInstance().find("help").execute(sender, args);
            log(start, sender, CommandManager.getInstance().find("help"), args);
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
                        log(start, sender, sc, args);
                        return false;
                    }
                }
            }

            chat.sendMessage(sender, "&cInvalid Command. Use /ah help for more info.");
            log(start, sender, null, args);
            return false;
        }

        if (sender.hasPermission(subCommand.getPermission()) || sender.isOp()) {
            subCommand.execute(sender, args);
        } else {
            chat.noPermission(sender);
        }
        log(start, sender, subCommand, args);

        return false;
    }

    private void log(long time, CommandSender sender, AkarianCommand command, String[] args) {
        String str = command == null ? "UNKNOWN" : command.getName();
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s).append(" ");
        }
        AuctionHouse.getInstance().getChat().log(sender.getName() + " executed " + str + " in " + (System.currentTimeMillis() - time) + "ms. (" + builder.toString().trim() + ")");
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.add("expired");
            result.add("help");
            result.add("list");
            result.add("search");
            result.add(" ");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list")) {
                if (sender instanceof Player)
                    result.add(AuctionHouse.getInstance().getEcon().getBalance(((Player) sender)) + "");
            }
            if (args[0].equalsIgnoreCase("search")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    result.add(AuctionHouse.getInstance().getMessages().getGui_ah_st() + ":" + p.getName());
                }
            }
        }
        return result;
    }
}
