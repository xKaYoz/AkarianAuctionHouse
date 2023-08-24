package net.akarian.auctionhouse.commands.admin.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.blacklist.BlacklistAdminGUI;
import net.akarian.auctionhouse.guis.admin.blacklist.BlacklistMainGUI;
import net.akarian.auctionhouse.guis.admin.blacklist.BlacklistViewSelectGUI;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlacklistSubCommand extends AkarianCommand {
    public BlacklistSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();

        if (sender instanceof Player) {
            Player p = (Player) sender;


            if (args.length == 1) {
                p.openInventory(new BlacklistMainGUI(p).getInventory());
            } else if (args[1].equalsIgnoreCase("view"))
                p.openInventory(new BlacklistViewSelectGUI().getInventory());
            else if (args[1].equalsIgnoreCase("add")) {
                ItemStack itemStack = p.getInventory().getItemInMainHand();
                //Check if the player is holding an item
                if (itemStack.getType().isAir() || !itemStack.getType().isItem()) {
                    chat.sendMessage(p, AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.MESSAGE_ERRORS_NOITEM));
                    return;
                }
                p.openInventory(new BlacklistAdminGUI(itemStack).getInventory());
            }
        }

    }
}
