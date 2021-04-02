package main.java.com.i0dev.command.discord.tebex;

import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.APIUtil;
import main.java.com.i0dev.utility.util.FormatUtil;
import main.java.com.i0dev.utility.util.MessageUtil;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.List;

public class tebexGiftcardCreate extends ListenerAdapter {

    private final String Identifier = "Tebex Giftcard create";
    private final List<String> COMMAND_ALIASES = getConfig.get().getStringList("commands.tebexGiftcardCreate.aliases");
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.tebexGiftcardCreate.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.tebexGiftcardCreate.permissionLiteMode");
    private final String messageContent = getConfig.get().getString("commands.tebexGiftcardCreate.messageContent");
    private final String MESSAGE_FORMAT = getConfig.get().getString("commands.tebexGiftcardCreate.format");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.tebexGiftcardCreate.enabled");
    private final boolean dmCodeInsteadOfSameChannel = getConfig.get().getBoolean("commands.tebexGiftcardCreate.dmCodeInsteadOfSameChannel");

    private final String error = getConfig.get().getString("commands.tebexGiftcardCreate.error");

    private final boolean LOGS_ENABLED = getConfig.get().getBoolean("commands.tebexGiftcardCreate.log");
    private final String LOGS_MESSAGE = getConfig.get().getString("commands.tebexGiftcardCreate.logMessage");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!e.getGuild().equals(GlobalConfig.GENERAL_MAIN_GUILD)) return;
        if (MessageAliases.isMessageACommand(e.getMessage(), COMMAND_ALIASES)) {
            if (Blacklist.get().isBlacklisted(e.getAuthor())) return;

            if (GlobalConfig.GENERAL_DELETE_COMMAND) e.getMessage().delete().queue();

            if (!COMMAND_ENABLED) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, e.getGuild(), e.getAuthor())) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
                return;
            }
            String[] message = e.getMessage().getContentRaw().split(" ");
            if (message.length == 1) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + COMMAND_ALIASES.get(0)), e.getAuthor())).build()).queue();
                return;
            }
            String amount = message[1];
            String note = FormatUtil.remainingArgFormatter(message, 2);
            JSONObject json = null;
            json = APIUtil.createGiftcard(amount, note);

            if (json == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(error).build()).queue();
                return;
            }
            System.out.println(json.toJSONString());
            if ((json.containsKey("error") && json.get("error").equals("2")) || json.isEmpty()) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(error).build()).queue();
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

            String desc = messageContent
                    .replace("{code}", code)
                    .replace("{value}", value + " " + currency)
                    .replace("{note}", note);
            String LOGS_MESSAGE2 = LOGS_MESSAGE
                    .replace("{code}", code)
                    .replace("{value}", value + " " + currency)
                    .replace("{note}", note);
            if (LOGS_ENABLED) {
                MessageUtil.sendMessage(GlobalConfig.GENERAL_MAIN_LOGS_CHANNEL, EmbedFactory.get().createSimpleEmbed(Placeholders.convert(LOGS_MESSAGE2, e.getAuthor())).build());
            }
            if (dmCodeInsteadOfSameChannel) {
                e.getAuthor().openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(desc, e.getAuthor())).build()).queue();

            } else {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(desc, e.getAuthor())).build()).queue();
            }
        }
    }
}