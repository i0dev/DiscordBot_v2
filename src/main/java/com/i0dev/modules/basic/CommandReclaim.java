package com.i0dev.modules.basic;

import com.i0dev.modules.DiscordCommandManager;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.object.objects.ReclaimOption;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommandReclaim extends DiscordCommand {

    public static boolean REQUIRE_PERMISSIONS;
    public static boolean REQUIRE_LITE_PERMISSIONS;
    public static String MESSAGE_CONTENT;
    public static String MESSAGE_FORMAT;
    public static String alreadyClaimed;
    public static String noReclaim;
    public static boolean COMMAND_ENABLED;

    @Override
    public void init() {
        REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.reclaim.requirePermission");
        REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.reclaim.permissionLiteMode");
        MESSAGE_CONTENT = Configuration.getString("commands.reclaim.messageContent");
        MESSAGE_FORMAT = Configuration.getString("commands.reclaim.format");
        COMMAND_ENABLED = Configuration.getBoolean("commands.reclaim.enabled");
        alreadyClaimed = Configuration.getString("commands.reclaim.alreadyClaimed");
        noReclaim = Configuration.getString("commands.reclaim.noReclaim");
        registerOptions();
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Reclaim")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.RECLAIM_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        DPlayer dPlayer = DPlayerEngine.getObject(e.getAuthor().getIdLong());

        if (!dPlayer.getLinkInfo().isLinked()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), com.i0dev.utility.GlobalConfig.NOT_LINKED, e.getAuthor());
            return;
        }

        if (dPlayer.isClaimedReclaim()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), alreadyClaimed, e.getAuthor());
            return;
        }

        List<ReclaimOption> options = getUsersReclaim(dPlayer);

        if (options.size() == 0) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), noReclaim, e.getAuthor());
            return;
        }

        dPlayer.setClaimedReclaim(true);
        dPlayer.save();

        for (ReclaimOption option : options) {
            for (String command : option.getCommands()) {
                org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(com.i0dev.DiscordBot.get(), () -> com.i0dev.DiscordBot.get().runCommand(org.bukkit.Bukkit.getConsoleSender(), command.replace("{ign}", dPlayer.getCachedData().getMinecraftIGN()))
                );

            }
        }

        List<String> dnames = new ArrayList<>();
        options.forEach(reclaimOption -> dnames.add(reclaimOption.getDisplayName()));


        String description = MESSAGE_CONTENT
                .replace("{dnames}", FormatUtil.FormatListString(dnames));

        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(description, e.getAuthor())).build()).queue();

    }

    static List<ReclaimOption> options = new ArrayList<>();

    public static void registerOptions() {
        options = new ArrayList<>();
        for (JSONObject jsonObject : Configuration.getObjectList("commands.reclaim.options")) {
            ReclaimOption option = new ReclaimOption();
            option.setDisplayName(((String) jsonObject.get("displayName")));
            option.setPermission(((String) jsonObject.get("permission")));
            option.setCommands(((ArrayList<String>) jsonObject.get("commands")));
            options.add(option);
        }
    }

    public static List<ReclaimOption> getUsersReclaim(DPlayer dPlayer) {
        List<ReclaimOption> ret = new ArrayList<>();
        org.bukkit.entity.Player player = org.bukkit.Bukkit.getPlayer(dPlayer.getCachedData().getMinecraftIGN());
        if (player == null) {
            System.out.println("null");
            return ret;
        }
        for (ReclaimOption option : options) {
            String permission = option.getPermission();
            if (player.hasPermission(permission)) {
                ret.add(option);
            }
        }
        return ret;
    }

}

