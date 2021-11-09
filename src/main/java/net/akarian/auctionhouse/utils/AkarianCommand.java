package net.akarian.auctionhouse.utils;

import lombok.Getter;
import org.bukkit.command.CommandSender;

public abstract class AkarianCommand {

    @Getter
    private final String name;
    @Getter
    private final String permission;
    @Getter
    private final String usage;
    @Getter
    private final String description;
    @Getter
    private final String[] aliases;

    public AkarianCommand(String name, String permission, String usage, String description, String... aliases) {
        this.name = name;
        this.permission = permission;
        this.usage = usage;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void execute(CommandSender sender, String[] args);





}
