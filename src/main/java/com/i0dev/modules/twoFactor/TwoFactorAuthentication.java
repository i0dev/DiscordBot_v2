package com.i0dev.modules.twoFactor;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.Encrypt;
import com.i0dev.utility.InternalJDA;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;

import java.util.*;

public class TwoFactorAuthentication implements Listener {
    @Setter
    @Getter
    private static Map<UUID, String> ipCache = new HashMap<>();

    public static boolean isCodeValid(String code) {

        for (TwoFactor twoFactor : Cache.getInstance().getTwoFactorCache()) {
            if (twoFactor.getCode().equalsIgnoreCase(code)) return true;
        }
        return false;
    }

    public static TwoFactor getObject(Player player) {
        for (TwoFactor twoFactor : Cache.getInstance().getTwoFactorCache()) {
            if (twoFactor.getPlayer().equals(player)) return twoFactor;
        }
        return null;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        TwoFactor twoFactor = getObject(e.getPlayer());
        if (twoFactor == null) return;
        Cache.getInstance().getTwoFactorCache().remove(twoFactor);
        Cache.getInstance().getCache().remove(e.getPlayer());

    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        TwoFactor twoFactor = getObject(e.getPlayer());
        if (twoFactor == null) return;
        Cache.getInstance().getTwoFactorCache().remove(twoFactor);
        Cache.getInstance().getCache().remove(e.getPlayer());
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!ENABLED) return;
        if (!e.getPlayer().hasPermission(PERMISSION_STRING)) return;
        if (getIpCache().containsKey(e.getPlayer().getUniqueId())
                && Encrypt.encrypt(Arrays.toString(e.getPlayer().getAddress().getAddress().getAddress()), e.getPlayer().getUniqueId().toString()).equalsIgnoreCase(getIpCache().get(e.getPlayer().getUniqueId()))) {
            return;
        }
        DPlayer dPlayer = DPlayerEngine.getObjectFromIGN(e.getPlayer().getName());
        if (dPlayer == null) return;
        TwoFactor preTwoF = getObject(e.getPlayer());
        MessageUtil.sendMessageIngame(e.getPlayer(), INGAME_MESSAGE);
        Cache.getInstance().getCache().add(e.getPlayer());
        if (preTwoF != null) {
            User user = InternalJDA.getJda().getUserById(dPlayer.getDiscordID());
            MessageUtil.sendMessagePrivateChannel(user.openPrivateChannel().complete().getIdLong(), MESSAGE_2FA_MESSAGE.replace("{code}", preTwoF.getCode()), null, user, null);
        } else {
            TwoFactor twoFactor = new TwoFactor();
            twoFactor.setCode(FormatUtil.GenerateRandomString(6));
            twoFactor.setPlayer(e.getPlayer());
            User user = InternalJDA.getJda().getUserById(dPlayer.getDiscordID());
            twoFactor.setUser(user);
            twoFactor.addToCache();
            MessageUtil.sendMessagePrivateChannel(user.getIdLong(), MESSAGE_2FA_MESSAGE.replace("{code}", twoFactor.getCode()), null, user, null);
        }
    }


    boolean isOnList(Player player) {
        return Cache.getInstance().getCache().contains(player);
    }

    public static final List<String> INGAME_MESSAGE = Configuration.getStringList("events.twoFactorAuth.ingame2faMessage");
    public static final List<String> INGAME_SUCSESS_MESSAGE = Configuration.getStringList("events.twoFactorAuth.ingamePass2fa");
    public static final String MESSAGE_2FA_MESSAGE = Configuration.getString("events.twoFactorAuth.2faMessage");
    public static final boolean ENABLED = Configuration.getBoolean("events.twoFactorAuth.enabled");
    public static final String PERMISSION_STRING = Configuration.getString("events.twoFactorAuth.permission");
    public static final String INGAME_INVALID_CODE = Configuration.getString("events.twoFactorAuth.invalidCode");

    //restrict movement
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (isOnList(p) && (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ())) {
            MessageUtil.sendMessageIngame(e.getPlayer(), INGAME_MESSAGE);
            e.setTo(e.getFrom());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.LEFT_CLICK_AIR)) return;
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        if (!isOnList(e.getPlayer())) return;
        MessageUtil.sendMessageIngame(e.getPlayer(), INGAME_MESSAGE);
        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!isOnList(e.getPlayer())) return;
        e.setCancelled(true);
        MessageUtil.sendMessageIngame(e.getPlayer(), INGAME_MESSAGE);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!isOnList(e.getPlayer())) return;
        e.setCancelled(true);
        MessageUtil.sendMessageIngame(e.getPlayer(), INGAME_MESSAGE);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if (!isOnList(player)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Player)) return;
        if (!isOnList((Player) e.getEntity())) return;
        e.setCancelled(true);
        MessageUtil.sendMessageIngame(((Player) e.getEntity()), INGAME_MESSAGE);
    }

    @EventHandler
    public void onFrozenHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!isOnList((Player) e.getDamager())) return;
        e.setCancelled(true);
        MessageUtil.sendMessageIngame(((Player) e.getDamager()), INGAME_MESSAGE);
    }


    @EventHandler
    public void onHungerLoose(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if (!isOnList(player)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void dropItemEvent(PlayerDropItemEvent e) {
        if (!isOnList(e.getPlayer())) return;
        e.setCancelled(true);
        MessageUtil.sendMessageIngame(e.getPlayer(), INGAME_MESSAGE);
    }

    @EventHandler
    public void pickItem(PlayerPickupItemEvent e) {
        if (!isOnList(e.getPlayer())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void inventoryEvent(InventoryClickEvent e) {
        if (!isOnList((Player) e.getWhoClicked())) return;
        MessageUtil.sendMessageIngame(((Player) e.getWhoClicked()), INGAME_MESSAGE);
        e.setCancelled(true);
    }

    @EventHandler
    public void inventoryEvent(InventoryDragEvent e) {
        if (!isOnList((Player) e.getWhoClicked())) return;
        MessageUtil.sendMessageIngame(((Player) e.getWhoClicked()), INGAME_MESSAGE);
        e.setCancelled(true);
    }


    @EventHandler
    public void commandSay(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String command = e.getMessage().split(" ")[0].toLowerCase();
        if (isOnList(p)) {
            List<String> whitelistedCommands = new ArrayList<>();
            whitelistedCommands.add("/2fa");
            if (!whitelistedCommands.contains(command)) {
                e.setCancelled(true);
                MessageUtil.sendMessageIngame((e.getPlayer()), INGAME_MESSAGE);
            }
        }
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (isOnList(p)) {
            e.setCancelled(true);
            MessageUtil.sendMessageIngame((e.getPlayer()), INGAME_MESSAGE);
        }
    }
}
