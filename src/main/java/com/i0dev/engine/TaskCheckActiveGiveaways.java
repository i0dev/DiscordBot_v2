package main.java.com.i0dev.engine;

import main.java.com.i0dev.command.polls.PollCache;
import main.java.com.i0dev.entity.Giveaway;
import main.java.com.i0dev.util.Placeholders;
import main.java.com.i0dev.util.Prettify;
import main.java.com.i0dev.util.conf;
import main.java.com.i0dev.util.getConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;

import javax.annotation.concurrent.ThreadSafe;
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

    public TimerTask TaskGiveawayTimeout = new TimerTask() {
        public void run() {
            if (Giveaway.get().getCache().isEmpty()) return;
            for (JSONObject object : Giveaway.get().getCache()) {
                Long endTime = (Long) object.get("endTime");
                if (System.currentTimeMillis() > endTime) {

                    String ChannelID = object.get("channelID").toString();
                    String MessageID = object.get("messageID").toString();
                    String HostID = object.get("hostID").toString();
                    String Prize = object.get("prize").toString();
                    Long EndTime = (Long) object.get("endTime");
                    String WinnerAmount = object.get("winnerAmount").toString();

                    TextChannel Channel = conf.GENERAL_MAIN_GUILD.getTextChannelById(ChannelID);
                    User Host = conf.GENERAL_MAIN_GUILD.getJDA().getUserById(HostID);
                    Message Message = Channel.retrieveMessageById(MessageID).complete();

                    ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(EndTime), ZoneId.of("America/New_York"));
                    List<User> UsersReacted = Message.retrieveReactionUsers(Emoji).complete();
                    List<User> selectedWinners = new ArrayList<>();
                    UsersReacted.removeIf(User::isBot);

                    for (int i = 0; i < Integer.parseInt(WinnerAmount); i++) {
                        selectedWinners.add(UsersReacted.get(ThreadLocalRandom.current().nextInt(UsersReacted.size())));
                    }
                    String winnersFormatted = Prettify.FormatListUser(selectedWinners);

                    EmbedBuilder editEmbed = new EmbedBuilder()
                            .setTimestamp(time)
                            .setThumbnail(conf.EMBED_THUMBNAIL)
                            .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                            .setDescription(Placeholders.convert(endedGiveawayContent
                                    .replace("{hostMention}", Host.getAsMention())
                                    .replace("{totalEntries}", UsersReacted.size() + "")
                                    .replace("{winners}", winnersFormatted)
                                    .replace("{prize}", Prize), Host))
                            .setTitle(endedGiveawayTitle)
                            .setFooter(endedGiveawayFooter);

                    EmbedBuilder newEmbed = new EmbedBuilder()
                            .setTimestamp(time)
                            .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                            .setThumbnail(conf.EMBED_THUMBNAIL)
                            .setDescription(winnersMessageContent
                                    .replace("{winners}", winnersFormatted)
                                    .replace("{messageLink}", ("https://discordapp.com/channels/" + conf.GENERAL_MAIN_GUILD.getId() + "/" + ChannelID + "/" + MessageID))
                                    .replace("{prize}", Prize))
                            .setTitle(winnersMessageTitle)
                            .setFooter(winnersMessageFooter);

                    Channel.editMessageById(MessageID, editEmbed.build()).queue();
                    Channel.sendMessage(newEmbed.build()).queue();
                    //dm giveaway winners
                    Giveaway.get().deleteGiveaway(MessageID);
                    break;
                }
            }
        }
    };
}
