package net.akarian.auctionhouse.commands.admin.subcommands;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.guis.admin.sounds.MainSoundGUI;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EditSubCommand extends AkarianCommand {

    @Getter
    private static final HashMap<Player, String> editing = new HashMap<>();


    public EditSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        player.openInventory(new MainSoundGUI(player).getInventory());
    }
}
