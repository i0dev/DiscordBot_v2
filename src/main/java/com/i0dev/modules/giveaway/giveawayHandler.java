package com.i0dev.modules.giveaway;

import com.i0dev.object.engines.GiveawayEngine;
import com.i0dev.object.objects.Giveaway;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class giveawayHandler {

    public static final String endedGiveawayTitle = Configuration.getString("modules.giveaway.message.endedGiveawayTitle");
    public static final String endedGiveawayContent = Configuration.getString("modules.giveaway.message.endedGiveawayDesc");
    public static final String endedGiveawayFooter = Configuration.getString("modules.giveaway.message.endedGiveawayFooter");

    public static final String winnersMessageTitle = Configuration.getString("modules.giveaway.message.winnersMessageTitle");
    public static final String winnersMessageContent = Configuration.getString("modules.giveaway.message.winnersMessageDesc");
    public static final String winnersMessageFooter = Configuration.getString("modules.giveaway.message.winnersMessageFooter");

    public static final String winnerDmMessage = Configuration.getString("modules.giveaway.message.winnerDirectMessageDesc");

    public static final String newWinnersTitle = Configuration.getString("modules.giveaway.message.giveawayRerollTitle");


    public static void endGiveawayFull(Giveaway giveaway, boolean byPassTime, boolean byPassEnding, boolean reroll, User reroller) {
        try {
            Long endTime = giveaway.getEndTime();
            if (System.currentTimeMillis() > endTime || byPassTime) {
                if (!byPassEnding) if (giveaway.isEnded()) return;
                Long ChannelID = giveaway.getChannelID();
                Long MessageID = giveaway.getMessageID();
                Long HostID = giveaway.getHostID();
                String Prize = giveaway.getPrize();
                Long EndTime = giveaway.getEndTime();
                Long WinnerAmount = giveaway.getWinnerAmount();
                TextChannel Channel = InternalJDA.getJda().getTextChannelById(ChannelID);
                User Host = GlobalConfig.GENERAL_MAIN_GUILD.getJDA().getUserById(HostID);
                Message Message;
                try {
                    Message = Channel.retrieveMessageById(MessageID).complete();
                } catch (Exception ignored) {
                    return;
                }
                ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(EndTime), ZoneId.of("America/New_York"));
                List<User> UsersReacted = new ArrayList<>();
                Message.getReactions().get(0).retrieveUsers().forEach(UsersReacted::add);

                List<User> selectedWinners = new ArrayList<>();
                UsersReacted.removeIf(User::isBot);

                for (int i = 0; i < WinnerAmount; i++) {
                    selectedWinners.add(UsersReacted.get(ThreadLocalRandom.current().nextInt(UsersReacted.size())));
                }
                String winnersFormatted = FormatUtil.FormatDoubleListUser(selectedWinners);

                EmbedBuilder editEmbed = new EmbedBuilder()
                        .setTimestamp(time)
                        .setThumbnail(GlobalConfig.EMBED_THUMBNAIL.equals("") ? null : GlobalConfig.EMBED_THUMBNAIL)
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
                            .setThumbnail(GlobalConfig.EMBED_THUMBNAIL.equals("") ? null : GlobalConfig.EMBED_THUMBNAIL)
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
                            .setThumbnail(GlobalConfig.EMBED_THUMBNAIL.equals("") ? null : GlobalConfig.EMBED_THUMBNAIL)
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
                        winner.openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(winnerDmMessage
                                        .replace("{prize}", Prize)
                                        .replace("{messageLink}", ("https://discordapp.com/channels/" + GlobalConfig.GENERAL_MAIN_GUILD.getId() + "/" + ChannelID + "/" + MessageID))
                                , winner)).build()).queue();
                    } catch (Exception ignored) {

                    }
                }
                GiveawayEngine.getInstance().setEnded(giveaway);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
