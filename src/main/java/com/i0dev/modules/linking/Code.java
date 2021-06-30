package com.i0dev.modules.linking;

import com.i0dev.object.discordLinking.CodeCache;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.discordLinking.From_IngameCodeLinker;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Code extends DiscordCommand {

    private static boolean PERMISSION_STRICT;
    private static boolean PERMISSION_LITE;
    private static boolean PERMISSION_ADMIN;
    private static boolean ENABLED;

    private static String MESSAGE_CONTENT;
    private static String MESSAGE_LOG_MESSAGE;
    private static String MESSAGE_CODE_NOT_VALID;

    private static List<String> MESSAGE_INGAME_CONTENT;

    private static boolean OPTION_LOG;


    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.link.parts.code.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.link.parts.code.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.link.parts.code.permission.admin");
        ENABLED = Configuration.getBoolean("modules.link.parts.code.enabled");

        MESSAGE_CONTENT = Configuration.getString("modules.link.parts.code.message.general");
        MESSAGE_LOG_MESSAGE = Configuration.getString("modules.link.parts.code.message.logMessage");
        MESSAGE_CODE_NOT_VALID = Configuration.getString("modules.link.parts.code.message.codeNotValid");

        MESSAGE_INGAME_CONTENT = Configuration.getStringList("modules.link.parts.code.message.ingameGeneral");

        OPTION_LOG = Configuration.getBoolean("modules.link.parts.code.option.log");
    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Link Code")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), LinkManager.code, e.getAuthor());
            return;
        }

        DPlayer dPlayer = DPlayerEngine.getObject(e.getAuthor().getIdLong());
        if (dPlayer != null && dPlayer.getLinkInfo().isLinked()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), LinkManager.MESSAGE_ALREADY_LINKED.replace("{ign}", dPlayer.getCachedData().getMinecraftIGN()), e.getAuthor());
            return;
        }

        String code = message[2];

        From_IngameCodeLinker codeLinker = CodeCache.getInstance().getObjectIngame(code);
        if (codeLinker == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CODE_NOT_VALID.replace("{code}", code), e.getAuthor());
            return;
        }

        DPlayerEngine.setLinked(e.getAuthor().getIdLong(), code, codeLinker.getPlayer().getName(), codeLinker.getPlayer().getUniqueId().toString());

        List<String> formattedMsg = new ArrayList<>();
        for (String msg : MESSAGE_INGAME_CONTENT) {
            formattedMsg.add(Placeholders.convert(msg.replace("{code}", code).replace("{ign}", codeLinker.getPlayer().getName()), e.getAuthor()));
        }
        MessageUtil.sendMessageIngame(codeLinker.getPlayer(), formattedMsg);


        String desc = MESSAGE_CONTENT
                .replace("{code}", code)
                .replace("{ign}", codeLinker.getPlayer().getName())
                .replace("{uuid}", codeLinker.getPlayer().getUniqueId().toString());
        MessageUtil.sendMessage(e.getChannel().getIdLong(), desc, e.getAuthor());

        if (OPTION_LOG) {
            String logMsg = MESSAGE_LOG_MESSAGE
                    .replace("{code}", code)
                    .replace("{ign}", codeLinker.getPlayer().getName())
                    .replace("{uuid}", codeLinker.getPlayer().getUniqueId().toString());
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, logMsg, e.getAuthor());
        }
        RoleRefreshHandler.RefreshUserRank(dPlayer);

    }
}