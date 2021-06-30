package com.i0dev.modules.linking;

import com.i0dev.object.discordLinking.CodeCache;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.discordLinking.From_DiscordCodeLinker;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Generate extends DiscordCommand {

    private static boolean PERMISSION_STRICT;
    private static boolean PERMISSION_LITE;
    private static boolean PERMISSION_ADMIN;
    private static boolean ENABLED;

    private static String MESSAGE_CONTENT;
    private static String MESSAGE_CODE_MESSAGE;


    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.link.parts.generate.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.link.parts.generate.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.link.parts.generate.permission.admin");
        ENABLED = Configuration.getBoolean("modules.link.parts.generate.enabled");

        MESSAGE_CONTENT = Configuration.getString("modules.link.parts.generate.message.general");
        MESSAGE_CODE_MESSAGE = Configuration.getString("modules.link.parts.generate.message.codeMessage");

    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Link Generate")) {
            return;
        }

        DPlayer dPlayer = DPlayerEngine.getObject(e.getAuthor().getIdLong());
        if (dPlayer != null && dPlayer.getLinkInfo().isLinked()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), LinkManager.MESSAGE_ALREADY_LINKED.replace("{ign}", dPlayer.getCachedData().getMinecraftIGN()), e.getAuthor());
            return;
        }

        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());

        String randomString = FormatUtil.GenerateRandomString();


        From_DiscordCodeLinker from_discordCodeLinker = new From_DiscordCodeLinker(e.getAuthor(), randomString);
        CodeCache.getInstance().getFrom_Discord_cache().add(from_discordCodeLinker);

        try {
            MessageUtil.sendMessagePrivateChannel(e.getAuthor().getIdLong(), MESSAGE_CODE_MESSAGE.replace("{code}", randomString), null, e.getAuthor(), null);
        } catch (Exception eee) {
            eee.printStackTrace();
        }
    }
}