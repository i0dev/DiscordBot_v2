package com.i0dev.commands.discord.completedModules.linking;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.CodeCache;
import com.i0dev.object.discordLinking.DPlayer;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.object.discordLinking.From_IngameCodeLinker;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.GlobalConfig;
import com.i0dev.utility.Placeholders;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class Code {
    private static final String Identifier = "Link Code";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.link.parts.code.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.link.parts.code.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.link.parts.code.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.link.parts.code.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.link.parts.code.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.link.parts.code.message.logMessage");
    private static final String MESSAGE_CODE_NOT_VALID = Configuration.getString("modules.link.parts.code.message.codeNotValid");

    private static final List<String> MESSAGE_INGAME_CONTENT = Configuration.getStringList("modules.link.parts.code.message.ingameGeneral");

    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.link.parts.code.option.log");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Link Code")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), LinkManager.code, e.getAuthor());
            return;
        }

        DPlayer dPlayer = DPlayerEngine.getInstance().getObject(e.getAuthor());
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

        DPlayerEngine.getInstance().setLinked(e.getAuthor(), code, codeLinker.getPlayer().getName(), codeLinker.getPlayer().getUniqueId().toString());


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
    }
}