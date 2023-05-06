package net.akarian.auctionhouse.events.aahEvents;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.commands.admin.subcommands.EditSubCommand;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class AdminEditEvents implements Listener {

    HashMap<Player, String> newMessage = new HashMap<>();

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        Chat chat = AuctionHouse.getInstance().getChat();
        String input = e.getMessage();
        FileManager fm = AuctionHouse.getInstance().getFileManager();
        YamlConfiguration messages = fm.getConfig("messages");

        if (EditSubCommand.getEditing().containsKey(p)) {
            e.setCancelled(true);

            if (input.equalsIgnoreCase("confirm") && newMessage.containsKey(p)) {
                messages.set(EditSubCommand.getEditing().get(p), newMessage.get(p));
                EditSubCommand.getEditing().remove(p);
                newMessage.remove(p);
                fm.saveFile(messages, "messages");
                AuctionHouse.getInstance().getMessages().reloadMessages();
                chat.sendMessage(p, "Your edit has been saved.");
                return;
            }

            newMessage.remove(p);

            newMessage.put(p, input);
            chat.sendMessage(p, "&7You have inputted \"" + input + "\"&7.");
            chat.sendMessage(p, "&7Type \"confirm\" to confirm this change.");
        }

    }

}
