package com.i0dev.modules.boosting;

import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Retrieve {
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.boosting.parts.list.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.boosting.parts.list.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.boosting.parts.list.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.boosting.parts.list.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.boosting.parts.list.message.general");
    private static final String MESSAGE_LIST_FORMAT = Configuration.getString("modules.boosting.parts.list.message.format");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Booster List")) {
            return;
        }

        List<String> lines = new ArrayList<>();
        List<DPlayer> orderedDPlayer = new ArrayList<>();
        GlobalConfig.GENERAL_MAIN_GUILD.getBoosters().forEach(member -> orderedDPlayer.add(DPlayerEngine.getObject(member.getIdLong())));
        Collections.reverse(orderedDPlayer);

        orderedDPlayer.sort(Comparator.comparing(DPlayer::getBoostCount));

        for (DPlayer booster : orderedDPlayer) {
            User user = InternalJDA.getJda().getUserById(booster.getDiscordID());
            if (user == null) continue;
            lines.add(Placeholders.convert(MESSAGE_LIST_FORMAT, user));
        }

        String desc = MESSAGE_CONTENT.replace("{boosters}", FormatUtil.FormatListString(lines));

        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());
    }
}
