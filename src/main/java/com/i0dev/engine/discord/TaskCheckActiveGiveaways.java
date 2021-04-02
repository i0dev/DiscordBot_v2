package main.java.com.i0dev.engine.discord;

import main.java.com.i0dev.object.Giveaway;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;

import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class TaskCheckActiveGiveaways {
    private static TaskCheckActiveGiveaways instance = new TaskCheckActiveGiveaways();

    public static TaskCheckActiveGiveaways get() {
        return instance;
    }

    private final String Emoji = getConfig.get().getString("commands.gcreate.giveawayEmoji");
    private final String endedGiveawayTitle = getConfig.get().getString("commands.gcreate.endedGiveawayTitle");
    private final String endedGiveawayContent = getConfig.get().getString("commands.gcreate.endedGiveawayContent");
    private final String endedGiveawayFooter = getConfig.get().getString("commands.gcreate.endedGiveawayFooter");
    private final String winnersMessageTitle = getConfig.get().getString("commands.gcreate.winnersMessageTitle");
    private final String winnersMessageContent = getConfig.get().getString("commands.gcreate.winnersMessageContent");
    private final String winnersMessageFooter = getConfig.get().getString("commands.gcreate.winnersMessageFooter");
    private final String winnerDmMessage = getConfig.get().getString("commands.gcreate.winnerDmMessage");
    private final String newWinnersTitle = getConfig.get().getString("commands.giveawayReroll.newWinnersTitle");


    public void endGiveawayFull(JSONObject object, boolean byPassTime, boolean byPassEnding, boolean reroll, User reroller) {
        Long endTime = (Long) object.get("endTime");
        if (System.currentTimeMillis() > endTime || byPassTime) {
            if (!byPassEnding) {
                boolean ended = ((boolean) object.get("ended"));
                if (ended) return;
            }
            String ChannelID = object.get("channelID").toString();
            String MessageID = object.get("messageID").toString();
            String HostID = object.get("hostID").toString();
            String Prize = object.get("prize").toString();
            Long EndTime = (Long) object.get("endTime");
            String WinnerAmount = object.get("winnerAmount").toString();
            TextChannel Channel = GlobalConfig.GENERAL_MAIN_GUILD.getTextChannelById(ChannelID);
            User Host = GlobalConfig.GENERAL_MAIN_GUILD.getJDA().getUserById(HostID);
            Message Message = null;
            try {
                Message = Channel.retrieveMessageById(MessageID).complete();
            } catch (Exception ignored) {
                if (Message == null) {
                    return;
                }
            }
            ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(EndTime), ZoneId.of("America/New_York"));
            String newEmoji = Message.getReactions().get(0).getReactionEmote().getEmoji();
            List<User> UsersReacted = Message.retrieveReactionUsers(newEmoji).complete();
            List<User> selectedWinners = new ArrayList<>();
            UsersReacted.removeIf(User::isBot);

            for (int i = 0; i < Integer.parseInt(WinnerAmount); i++) {
                selectedWinners.add(UsersReacted.get(ThreadLocalRandom.current().nextInt(UsersReacted.size())));
            }
            String winnersFormatted = FormatUtil.FormatDoubleListUser(selectedWinners);

            EmbedBuilder editEmbed = new EmbedBuilder()
                    .setTimestamp(time)
                    .setThumbnail(GlobalConfig.EMBED_THUMBNAIL)
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                    .setDescription(Placeholders.convert(endedGiveawayContent
                            .replace("{hostMention}", Host.getAsMention())
                            .replace("{totalEntries}", UsersReacted.size() + "")
                            .replace("{winners}", winnersFormatted)
                            .replace("{prize}", Prize), Host))
                    .setTitle(endedGiveawayTitle)
                    .setFooter(endedGiveawayFooter);
            EmbedBuilder newEmbed;
            if (reroll) {
                newEmbed = new EmbedBuilder()
                        .setTimestamp(time)
                        .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                        .setThumbnail(GlobalConfig.EMBED_THUMBNAIL)
                        .setDescription(winnersMessageContent
                                .replace("{winners}", winnersFormatted)
                                .replace("{messageLink}", ("https://discordapp.com/channels/" + GlobalConfig.GENERAL_MAIN_GUILD.getId() + "/" + ChannelID + "/" + MessageID))
                                .replace("{prize}", Prize))
                        .setTitle(Placeholders.convert(newWinnersTitle, reroller))
                        .setFooter(winnersMessageFooter);
            } else {
                newEmbed = new EmbedBuilder()
                        .setTimestamp(time)
                        .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                        .setThumbnail(GlobalConfig.EMBED_THUMBNAIL)
                        .setDescription(winnersMessageContent
                                .replace("{winners}", winnersFormatted)
                                .replace("{messageLink}", ("https://discordapp.com/channels/" + GlobalConfig.GENERAL_MAIN_GUILD.getId() + "/" + ChannelID + "/" + MessageID))
                                .replace("{prize}", Prize))
                        .setTitle(winnersMessageTitle)
                        .setFooter(winnersMessageFooter);
            }
            Channel.editMessageById(MessageID, editEmbed.build()).queue();
            Channel.sendMessage(newEmbed.build()).queue();
            for (User winner : selectedWinners) {
                try {
                    winner.openPrivateChannel().complete().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(winnerDmMessage
                                    .replace("{prize}", Prize)
                                    .replace("{messageLink}", ("https://discordapp.com/channels/" + GlobalConfig.GENERAL_MAIN_GUILD.getId() + "/" + ChannelID + "/" + MessageID))
                            , winner)).build()).queue();
                } catch (Exception ignored) {

                }
            }
            Giveaway.get().endGiveaway(MessageID);
        }
    }


    public TimerTask TaskGiveawayTimeout = new TimerTask() {
        public void run() {
            if (Giveaway.get().getCache().isEmpty()) return;
            for (JSONObject object : Giveaway.get().getCache()) {
                endGiveawayFull(object, false, false, false, null);
            }
        }
    };
}
