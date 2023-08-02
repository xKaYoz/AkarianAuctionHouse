package net.akarian.auctionhouse.utils;

import lombok.Getter;
import net.akarian.auctionhouse.AuctionHouse;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Messages {

    private final FileManager fm;
    @Getter
    private String gui_aha_title, gui_sound_title, gui_cl_title;

    @Getter
    private List<String> gui_aha_listing, gui_npc_lore, gui_cl_lore;
    @Getter
    private YamlConfiguration messagesFile;
    @Getter
    private int version;

    /*
    Naming Scheme

    A_B_C

    a = where used
    b = location
    c = tag

    a -> MESSAGE,BUTTON,GUI
    b ->
        MESSAGE -> SYNTAX,ERRORS,GEN
        BUTTON  -> PPAGE,NPAGE,CONFIRM,DENY,RETURN
        GUI     -> MAIN,MAINADMIN,BUY,LIST,RECLAIM,EDIT,SHULKER,SORT,NPC,SOUND

    A_B_C("FILE LOCATION", "FORMATTED NAME")
     */

    public Messages() {
        fm = AuctionHouse.getInstance().getFileManager();
        if (!fm.getFile("messages").exists()) {
            fm.createFile("messages");
        }
    }



}
