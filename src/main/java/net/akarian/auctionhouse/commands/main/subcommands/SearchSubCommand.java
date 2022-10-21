package net.akarian.auctionhouse.commands.main.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.AuctionHouseGUI;
import net.akarian.auctionhouse.guis.SortType;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SearchSubCommand extends AkarianCommand {

    public SearchSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();

        //  /ah search <searchStr>

        if (!(sender instanceof Player)) {
            chat.sendMessage(sender, AuctionHouse.getInstance().getMessages().getError_player());
            return;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            chat.usage(player, AuctionHouse.getInstance().getMessages().getSearch_syntax());
            return;
        }

        StringBuilder builder = new StringBuilder();
        String query;

        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        query = builder.toString().trim();

        player.openInventory(new AuctionHouseGUI(player, SortType.TIME_LEFT, true, 1).search(query).getInventory());


    }
}
