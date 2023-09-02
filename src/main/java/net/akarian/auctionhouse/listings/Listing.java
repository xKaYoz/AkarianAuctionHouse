package net.akarian.auctionhouse.listings;

import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.akarian.auctionhouse.AuctionHouse;
import net.akarian.auctionhouse.utils.Chat;
import net.akarian.auctionhouse.utils.UUIDDataType;
import net.akarian.auctionhouse.utils.messages.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Listing {

    @Getter
    private final UUID id;
    @Getter
    private final UUID creator;
    @Getter
    private final long start;
    private final AuctionHouse plugin;
    private final Chat chat;
    @Getter
    @Setter
    private ItemStack itemStack;
    @Getter
    private final boolean isBiddable;
    @Getter
    @Setter
    private long end;
    @Getter
    @Setter
    private UUID buyer;
    @Getter
    @Setter
    private String endReason;
    @Getter
    @Setter
    private boolean reclaimed;
    @Getter
    @Setter
    private boolean expired, completed;
    @Getter
    public ArrayList<String> bids;
    @Getter
    public UUID currentBidder;
    @Getter
    public double currentBid;
    @Getter
    @Setter
    private double buyNowPrice;
    @Getter
    @Setter
    private double startingBid;
    @Getter
    @Setter
    private double minimumIncrement;

    //Buy Now Listing
    public Listing(UUID id, UUID creator, ItemStack itemStack, Double buyNowPrice, Long start) {
        plugin = AuctionHouse.getInstance();
        chat = plugin.getChat();
        this.id = id;
        this.creator = creator;
        this.itemStack = itemStack;
        this.buyNowPrice = buyNowPrice;
        this.start = start;
        this.reclaimed = false;
        this.expired = false;
        this.completed = false;
        this.isBiddable = false;
    }

    public Listing(UUID id, UUID creator, ItemStack itemStack, Double buyNowPrice, double startingBid, double minimumIncrement, Long start) {
        plugin = AuctionHouse.getInstance();
        chat = plugin.getChat();
        this.id = id;
        this.creator = creator;
        this.itemStack = itemStack;
        this.buyNowPrice = buyNowPrice;
        this.start = start;
        this.reclaimed = false;
        this.expired = false;
        this.completed = false;
        this.startingBid = startingBid;
        this.minimumIncrement = minimumIncrement;
        this.isBiddable = true;
        this.bids = new ArrayList<>();
    }

    public String formatBidders() {
        StringBuilder builder = new StringBuilder();
        for (String s : bids) {
            builder.append(s).append(":");
        }
        return builder.toString();
    }

    public void setBidders(String bidders) {
        String[] split = bidders.split(":");
        bids.addAll(Arrays.asList(split));
    }

    public void newCurrentBid(UUID bidder, Double newBid) {
        bids.add(bidder + ";" + newBid);
        Player currentBidderPlayer = Bukkit.getPlayer(currentBidder);
        if (currentBidderPlayer != null) {
            //TODO notify of outbid
            chat.sendMessage(currentBidderPlayer, "&cYou have been outbid for &e" + chat.formatItem(itemStack) + "&c for &2" + chat.formatMoney(newBid) + "&c.");
        }
        currentBidder = bidder;
        currentBid = newBid;
    }

    public ItemStack createAdminActiveListing(Player player) {
        ItemStack itemStack = getItemStack().clone();

        if (itemStack.getType() == Material.AIR) {
            chat.log("Error while loading listing " + id.toString() + " from " + getCreator().toString() + ".", AuctionHouse.getInstance().isDebug());
            chat.sendMessage(player, "&cThere was an error while loading a listing. Please contact an admin to check the logs.");
            AuctionHouse.getInstance().getListingManager().getActive().remove(this);
            return null;
        }

        List<String> tlore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            tlore = new ArrayList<>();
        }

        assert tlore != null;

        long seconds = ((getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) - System.currentTimeMillis()) / 1000;

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), getId());

        for (String s : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_MAINADMIN_LISTING)) {
            if (s.equalsIgnoreCase("%shulker%")) {
                if (itemStack.getType() == Material.SHULKER_BOX) {
                    BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                        int amount = 0;
                        for (ItemStack si : shulker.getInventory().getContents()) {
                            if (si != null) {
                                amount += si.getAmount();
                            }
                        }
                        for (String shulkers : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_SHULKER_LORE)) {
                            tlore.add(shulkers.replace("%amount%", String.valueOf(amount)));
                        }
                    }
                }
            } else {

                String buyNowPriceStr = AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_BUYNOW, "%price%;" + chat.formatMoney(getBuyNowPrice()));
                String bidStr = AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_CURRENTBID, "%price%;" + chat.formatMoney(currentBid));
                String minIncrementStr = AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_MININCREMENT, "%price%;" + chat.formatMoney(currentBid + minimumIncrement));

                tlore.add(s.replace("%time%", chat.formatTime(seconds)).replace("%creator%", plugin.getNameManager().getName(creator)).replace("%buyNow%", buyNowPriceStr).replace("%currentBid%", bidStr).replace("%minIncrement%", minIncrementStr));
            }
        }
        itemMeta.setLore(chat.formatList(tlore));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack createUnclaimedCompleteListing(Player player) {
        ItemStack itemStack = getItemStack().clone();

        if (itemStack.getType() == Material.AIR) {
            chat.log("Error while loading listing " + id.toString() + " from " + getCreator().toString() + ".", AuctionHouse.getInstance().isDebug());
            AuctionHouse.getInstance().getListingManager().getExpired().remove(this);
            return null;
        }

        List<String> lore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            lore = new ArrayList<>();
        }

        assert lore != null;

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        assert itemMeta != null;
        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), id);

        for (String s : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_MAIN_LISTING_COMPLETED)) {
            if (s.equalsIgnoreCase("%shulker%")) {
                if (itemStack.getType() == Material.SHULKER_BOX) {
                    BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                        int amount = 0;
                        for (ItemStack si : shulker.getInventory().getContents()) {
                            if (si != null) {
                                amount += si.getAmount();
                            }
                        }
                        for (String shulkers : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_SHULKER_LORE)) {
                            lore.add(shulkers.replace("%amount%", String.valueOf(amount)));
                        }
                    }
                }
            } else {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    lore.add(PlaceholderAPI.setPlaceholders(player, s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd())).replace("%price%", chat.formatMoney(buyNowPrice)).replace("%seller%", AuctionHouse.getInstance().getNameManager().getName(getCreator()))));
                else
                    lore.add(s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd())).replace("%price%", chat.formatMoney(buyNowPrice)).replace("%seller%", AuctionHouse.getInstance().getNameManager().getName(getCreator())));
            }
        }

        itemMeta.setLore(chat.formatList(lore));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack createAdminCompleteListing(Player player) {
        ItemStack itemStack = getItemStack().clone();

        if (itemStack.getType() == Material.AIR) {
            chat.log("Error while loading listing " + id.toString() + " from " + getCreator().toString() + ".", AuctionHouse.getInstance().isDebug());
            chat.sendMessage(player, "&cThere was an error while loading a listing. Please contact an admin to check the logs.");
            AuctionHouse.getInstance().getListingManager().getCompleted().remove(this);
            return null;
        }

        List<String> tlore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            tlore = new ArrayList<>();
        }

        assert tlore != null;

        long seconds = ((getStart() + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) - System.currentTimeMillis()) / 1000;

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), getId());

        for (String s : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_MAIN_LISTING_COMPLETED_ADMIN)) {
            if (s.equalsIgnoreCase("%shulker%")) {
                if (itemStack.getType() == Material.SHULKER_BOX) {
                    BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                        int amount = 0;
                        for (ItemStack si : shulker.getInventory().getContents()) {
                            if (si != null) {
                                amount += si.getAmount();
                            }
                        }
                        for (String shulkers : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_SHULKER_LORE)) {
                            tlore.add(shulkers.replace("%amount%", String.valueOf(amount)));
                        }
                    }
                }
            } else {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    tlore.add(PlaceholderAPI.setPlaceholders(player, s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd())).replace("%price%", chat.formatMoney(buyNowPrice))).replace("%buyer%", AuctionHouse.getInstance().getNameManager().getName(buyer)));
                else
                    tlore.add(s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd())).replace("%price%", chat.formatMoney(buyNowPrice)).replace("%buyer%", AuctionHouse.getInstance().getNameManager().getName(buyer)));
            }
        }
        itemMeta.setLore(chat.formatList(tlore));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack createExpiredListing(Player player) {
        ItemStack itemStack = getItemStack().clone();

        if (itemStack.getType() == Material.AIR) {
            chat.log("Error while loading listing " + id.toString() + " from " + getCreator().toString() + ".", AuctionHouse.getInstance().isDebug());
            AuctionHouse.getInstance().getListingManager().getExpired().remove(this);
            return null;
        }

        List<String> lore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            lore = new ArrayList<>();
        }

        assert lore != null;

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        assert itemMeta != null;
        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), id);

        for (String s : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_MAIN_LISTING_EXPIRED)) {
            if (s.equalsIgnoreCase("%shulker%")) {
                if (itemStack.getType() == Material.SHULKER_BOX) {
                    BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                        int amount = 0;
                        for (ItemStack si : shulker.getInventory().getContents()) {
                            if (si != null) {
                                amount += si.getAmount();
                            }
                        }
                        for (String shulkers : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_SHULKER_LORE)) {
                            lore.add(shulkers.replace("%amount%", String.valueOf(amount)));
                        }
                    }
                }
            } else {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
                    lore.add(PlaceholderAPI.setPlaceholders(player, s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd())).replace("%price%", chat.formatMoney(buyNowPrice))));
                else
                    lore.add(s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd())).replace("%price%", chat.formatMoney(buyNowPrice)));
            }
        }

        itemMeta.setLore(chat.formatList(lore));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack createAdminExpiredListing(Player player) {
        ItemStack itemStack = getItemStack().clone();

        if (itemStack.getType() == Material.AIR) {
            chat.log("Error while loading listing " + id.toString() + " from " + getCreator().toString() + ".", AuctionHouse.getInstance().isDebug());
            chat.sendMessage(player, "&cThere was an error while loading a listing. Please contact an admin to check the logs.");
            AuctionHouse.getInstance().getListingManager().getExpired().remove(this);
            return null;
        }

        List<String> lore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            lore = new ArrayList<>();
        }

        assert lore != null;

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.getItemMeta();

        assert itemMeta != null;
        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), id);

        for (String s : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_MAIN_LISTING_EXPIRED_ADMIN)) {
            if (s.equalsIgnoreCase("%shulker%")) {
                if (itemStack.getType() == Material.SHULKER_BOX) {
                    BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                        int amount = 0;
                        for (ItemStack si : shulker.getInventory().getContents()) {
                            if (si != null) {
                                amount += si.getAmount();
                            }
                        }
                        for (String shulkers : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_SHULKER_LORE)) {
                            lore.add(shulkers.replace("%amount%", String.valueOf(amount)));
                        }
                    }
                }
            } else {
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    lore.add(PlaceholderAPI.setPlaceholders(player, s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd())).replace("%price%", chat.formatMoney(buyNowPrice)).replace("%reclaimed%", isReclaimed() ? "&aTrue" : "&cFalse")));
                } else
                    lore.add(s.replace("%start%", chat.formatDate(getStart())).replace("%end%", chat.formatDate(getEnd())).replace("%price%", chat.formatMoney(buyNowPrice)).replace("%reclaimed%", isReclaimed() ? "&aTrue" : "&cFalse"));
            }
        }

        itemMeta.setLore(chat.formatList(lore));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public boolean isActive() {
        return AuctionHouse.getInstance().getListingManager().getActive().contains(this);
    }

    public void setComplete(UUID buyer, long end) {
        this.buyer = buyer;
        this.end = end;
        this.completed = true;
        AuctionHouse.getInstance().getListingManager().getActive().remove(this);
        AuctionHouse.getInstance().getListingManager().getCompleted().add(this);
    }

    public void setExpired(long end, boolean reclaimed) {
        this.end = end;
        this.expired = true;
        AuctionHouse.getInstance().getListingManager().getActive().remove(this);
        AuctionHouse.getInstance().getListingManager().getExpired().add(this);
        if (!reclaimed) AuctionHouse.getInstance().getListingManager().getUnclaimed().add(this);
    }

    public ItemStack createActiveListing(Player player) {
        ItemStack itemStack = getItemStack().clone();

        if (itemStack.getType() == Material.AIR) {
            chat.log("Error while loading listing " + id.toString() + " from " + getCreator().toString() + ".", AuctionHouse.getInstance().isDebug());
            AuctionHouse.getInstance().getListingManager().getActive().remove(this);
            return null;
        }

        List<String> tlore = itemStack.hasItemMeta() ? (itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore() : new ArrayList<>()) : new ArrayList<>();

        if (itemStack.getType() == Material.SHULKER_BOX) {
            tlore = new ArrayList<>();
        }

        assert tlore != null;

        long seconds = ((start + (AuctionHouse.getInstance().getConfigFile().getListingTime() * 1000L)) - System.currentTimeMillis()) / 1000;
        double tax = AuctionHouse.getInstance().getConfigFile().calculateListingTax(player, getBuyNowPrice());

        NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "listing-id");
        ItemMeta itemMeta = itemStack.hasItemMeta() ? itemStack.getItemMeta() : new ItemStack(getItemStack().getType()).getItemMeta();

        itemMeta.getPersistentDataContainer().set(key, new UUIDDataType(), id);

        for (String s : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_MAIN_LISTING_ACTIVE)) {
            if (s.equalsIgnoreCase("%shulker%")) {
                if (itemStack.getType() == Material.SHULKER_BOX) {
                    BlockStateMeta im = (BlockStateMeta) itemStack.getItemMeta();
                    if (im.getBlockState() instanceof ShulkerBox) {
                        ShulkerBox shulker = (ShulkerBox) im.getBlockState();
                        int amount = 0;
                        for (ItemStack si : shulker.getInventory().getContents()) {
                            if (si != null) {
                                amount += si.getAmount();
                            }
                        }
                        for (String shulkers : AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_SHULKER_LORE)) {
                            tlore.add(shulkers.replace("%amount%", String.valueOf(amount)));
                        }
                    }
                }
            } else if (s.equalsIgnoreCase("%self_info%")) {
                if (getCreator().toString().equals(player.getUniqueId().toString())) {
                    tlore.addAll(AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_MAIN_INFO_SELLER));
                } else
                    tlore.addAll(AuctionHouse.getInstance().getMessageManager().getLore(MessageType.GUI_MAIN_INFO_BUYER));
            } else {

                String buyNowPriceStr = AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_BUYNOW, "%price%;" + chat.formatMoney(buyNowPrice));
                String bidStr = AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_CURRENTBID, "%price%;" + chat.formatMoney(currentBid));
                String minIncrementStr = AuctionHouse.getInstance().getMessageManager().getMessage(MessageType.GUI_MAIN_MININCREMENT, "%price%;" + chat.formatMoney(currentBid + minimumIncrement));

                tlore.add(s.replace("%time%", chat.formatTime(seconds)).replace("%creator%", plugin.getNameManager().getName(creator)).replace("%buyNow%", buyNowPriceStr).replace("%currentBid%", bidStr).replace("%minIncrement%", minIncrementStr).replace("%tax%", chat.formatMoney(tax)).replace("%total%", chat.formatMoney((buyNowPrice + tax))));
            }
        }

        itemMeta.setLore(chat.formatList(tlore));
        itemMeta.setDisplayName(chat.formatItem(itemStack));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
