package net.akarian.auctionhouse.commands.main.subcommands;

import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.AkarianCommand;
import net.akarian.auctionhouse.utils.Chat;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class NPCSubCommand extends AkarianCommand {

    public NPCSubCommand(String name, String permission, String usage, String description, String... aliases) {
        super(name, permission, usage, description, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        Chat chat = AuctionHouse.getInstance().getChat();

        if (!(sender instanceof Player)) {
            chat.sendMessage(sender, AuctionHouse.getInstance().getMessages().getError_player());
            return;
        }

        Player p = (Player) sender;

        if (Bukkit.getPluginManager().getPlugin("Citizens") == null) {
            chat.sendMessage(sender, "&cYou do not have Citizens installed.");
            return;
        }

        if (args[1].equalsIgnoreCase("create")) {
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "Auctioneer");
            npc.spawn(p.getLocation(), SpawnReason.CREATE);
            npc.getEntity().setMetadata("Auctioneer", new FixedMetadataValue(AuctionHouse.getInstance(), true));
            return;
        } else if (args[1].equalsIgnoreCase("delete")) {
            for (Entity entity : p.getNearbyEntities(2, 0, 2)) {
                if (p.hasLineOfSight(entity) && entity.hasMetadata("Auctioneer") && entity.hasMetadata("NPC")) {
                    NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
                    npc.destroy(p);
                }
            }
        }

    }
}
