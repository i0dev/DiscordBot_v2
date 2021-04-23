package com.i0dev.commands.discord.completedModules.tebex.giftcard;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.APIUtil;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

public class Create {
    private static final String Identifier = "Tebex GiftCard Create";

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.tebex.parts.giftcard.parts.create.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.tebex.parts.giftcard.parts.create.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.tebex.parts.giftcard.parts.create.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.tebex.parts.giftcard.parts.create.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.tebex.parts.giftcard.parts.create.message.general");
    private static final String MESSAGE_LOG_MESSAGE = Configuration.getString("modules.tebex.parts.giftcard.parts.create.message.logMessage");

    private static final boolean OPTION_PRIVATE_MESSAGE_CODE = Configuration.getBoolean("modules.tebex.parts.giftcard.parts.create.option.privateMessageCode");
    private static final boolean OPTION_LOG = Configuration.getBoolean("modules.tebex.parts.giftcard.parts.create.option.log");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Tebex GiftCard Create")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 3) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GiftcardMod.create, e.getAuthor());
            return;
        }
        String amount = message[3];
        String note = FormatUtil.remainingArgFormatter(message, 4);
        JSONObject json = APIUtil.createGiftcard(amount, note);
        if (json == null) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GiftcardMod.MESSAGE_NO_PERMISSION, e.getAuthor());
            return;
        }
        System.out.println(json.toJSONString());
        if ((json.containsKey("error") && json.get("error").equals("2")) || json.isEmpty()) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), GiftcardMod.MESSAGE_NO_PERMISSION, e.getAuthor());
            return;
        }
        json = ((JSONObject) json.get("data"));
        String code = json.get("code").toString();
        json = ((JSONObject) json.get("balance"));
        String value = json.get("starting").toString();
        String currency = json.get("currency").toString();
        if (note.equals("")) {
            note = "No note provided";
        }

        String desc = MESSAGE_CONTENT
                .replace("{code}", code)
                .replace("{value}", value + " " + currency)
                .replace("{note}", note);
        String LOGS_MESSAGE2 = MESSAGE_LOG_MESSAGE
                .replace("{code}", code)
                .replace("{value}", value + " " + currency)
                .replace("{note}", note);
        if (OPTION_LOG) {
            MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.createEmbed(Placeholders.convert(LOGS_MESSAGE2, e.getAuthor())).build());
        }
        if (OPTION_PRIVATE_MESSAGE_CODE) {
            e.getAuthor().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(desc, e.getAuthor())).build()).queue();
        } else {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(desc, e.getAuthor())).build()).queue();

        }
    }
}
