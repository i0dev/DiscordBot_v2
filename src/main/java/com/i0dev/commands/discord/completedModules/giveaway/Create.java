package com.i0dev.commands.discord.completedModules.giveaway;

import com.i0dev.commands.discord.completedModules.giveaway.cache.GiveawayCache;
import com.i0dev.object.objects.Giveaway;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.TimeUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;


public class Create extends ListenerAdapter {
    private static final String Identifier = "Giveaway Create";
    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.giveaway.parts.create.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.giveaway.parts.create.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.giveaway.parts.create.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.giveaway.parts.create.enabled");

    private static final String MESSAGE_CONTENT = Configuration.getString("modules.giveaway.parts.create.message.general");
    private static final String MESSAGE_ALREADY_CREATING = Configuration.getString("modules.giveaway.parts.create.message.alreadyCreating");

    public static final String createdGiveawayTitle = Configuration.getString("modules.giveaway.message.createdGiveawayTitle");
    public static final String createdGiveawayContent = Configuration.getString("modules.giveaway.message.createdGiveawayDesc");
    public static final String createdGiveawayFooter = Configuration.getString("modules.giveaway.message.createdGiveawayFooter");

    public static final String Emoji = Configuration.getString("modules.giveaway.giveawayEmoji");
    public static final String giveawayEmojiText = Configuration.getString("modules.giveaway.giveawayEmojiText");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Giveaway Create")) {
            return;
        }
        if (GiveawayCache.get().getMap().containsKey(e.getAuthor()) || GiveawayCache.get().getResponseMap().containsKey(e.getAuthor())) {
            MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_ALREADY_CREATING, e.getAuthor());
            return;
        }

        GiveawayCache.get().getMap().put(e.getAuthor(), 0);
        GiveawayCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + Configuration.getLong("general.creatorTimeouts"));
        e.getAuthor().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed("Please enter the channel you would like the giveaway to be posted in.").build()).queue();
        MessageUtil.sendMessage(e.getChannel().getIdLong(), MESSAGE_CONTENT, e.getAuthor());
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (DPlayerEngine.getObject(e.getAuthor().getIdLong()).isBlacklisted()) return;
        if (!GiveawayCache.get().getMap().containsKey(e.getAuthor())) return;

        if (!ENABLED) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }

        if (e.getMessage().getContentRaw().equalsIgnoreCase(GlobalConfig.GENERAL_BOT_PREFIX + "cancel")) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed("You have canceled your current giveaway creator.").build()).queue();
            GiveawayCache.get().removeUser(e.getAuthor());

            return;
        }
        GiveawayCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + Configuration.getLong("general.creatorTimeouts"));

        Integer CurrentQuestion = (Integer) GiveawayCache.get().getMap().get(e.getAuthor());
        String messageContent = e.getMessage().getContentRaw();
        LinkedHashMap<String, String> previousResponses = (LinkedHashMap<String, String>) GiveawayCache.get().getResponseMap().get(e.getAuthor());
        if (previousResponses == null) {
            previousResponses = new LinkedHashMap<>();
        }
        ArrayList<String> Questions = (ArrayList<String>) GiveawayCache.get().getQuestionMap().get(e.getAuthor());
        if (Questions == null) {
            Questions = new ArrayList<>();
            Questions.add("Please enter the channel you would like the giveaway to be posted in.");
            Questions.add("What is the giveaway prize?");
            Questions.add("How many winners should there be in this giveaway?");
            Questions.add("How long do you want the giveaway to last? Please put in the format: `2w7d5h20m5s`");
            Questions.add("Type `\"submit\"` to submit your giveaway creator");
            GiveawayCache.get().getQuestionMap().put(e.getAuthor(), Questions);
        }

        if (CurrentQuestion == 0) {
            TextChannel giveawayChannel = FindFromString.get().getTextChannel(messageContent.split(" ")[0], e.getMessage());
            if (giveawayChannel == null) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Cannot find that channel. Please try again").build()).queue();
                return;
            }
            previousResponses.put(Questions.get(CurrentQuestion), giveawayChannel.getId());
            GiveawayCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            GiveawayCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(1)).build()).queue();
            return;

        }
        if (CurrentQuestion > 0 && CurrentQuestion < Questions.size() - 1) {
            if (CurrentQuestion == 2) {
                if (!FormatUtil.isInt(messageContent)) {
                    e.getChannel().sendMessage(EmbedFactory.createEmbed("Incorrect input. Please enter a number.").build()).queue();
                    return;
                } else if (Integer.parseInt(messageContent) <= 0) {
                    e.getChannel().sendMessage(EmbedFactory.createEmbed("Incorrect input. Please enter a number greater than 0.").build()).queue();
                    return;
                }
            }

            if (CurrentQuestion == 3) {
                if (FormatUtil.getTimeMilis(messageContent) == -1) {
                    e.getChannel().sendMessage(EmbedFactory.createEmbed("Incorrect input. Please enter a proper date in the format stated.").build()).queue();
                    return;
                }
            }
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            GiveawayCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(CurrentQuestion)).build()).queue();
            GiveawayCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            return;
        }

        if (CurrentQuestion == Questions.size() - 1) {
            if (!messageContent.equalsIgnoreCase("submit")) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Unknown message, to create your giveaway, type `submit`, to cancel type `" + GlobalConfig.GENERAL_BOT_PREFIX + "cancel`").build()).queue();
                return;
            }
            ArrayList<String> responsesInOrder = new ArrayList<>();
            ((LinkedHashMap<String, String>) GiveawayCache.get().getResponseMap().get(e.getAuthor())).forEach((key, value) -> {
                responsesInOrder.add(value);
            });
            StringBuilder desc = new StringBuilder();
            TextChannel Giveawaychannel = e.getJDA().getTextChannelById(responsesInOrder.get(0));
            String Prize = responsesInOrder.get(1);
            String WinnerAmount = responsesInOrder.get(2);
            long endTimeMillis = System.currentTimeMillis() + FormatUtil.getTimeMilis(responsesInOrder.get(3));
            ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTimeMillis), ZoneId.of("America/New_York"));

            desc.append(Placeholders.convert(createdGiveawayContent
                    .replace("{emoji}", giveawayEmojiText)
                    .replace("{winnerCount}", WinnerAmount)
                    .replace("{timeLeft}", TimeUtil.formatTime(endTimeMillis - System.currentTimeMillis()))
                    .replace("{prize}", Prize), e.getAuthor()));

            EmbedBuilder embed = new EmbedBuilder()
                    .setTimestamp(time)
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                    .setThumbnail(GlobalConfig.EMBED_THUMBNAIL.equals("") ? null : GlobalConfig.EMBED_THUMBNAIL)
                    .setDescription(desc.toString())
                    .setTitle(Placeholders.convert(createdGiveawayTitle, e.getAuthor()))
                    .setFooter(Placeholders.convert(createdGiveawayFooter, e.getAuthor()));

            Message FullChannel = Giveawaychannel.sendMessage(embed.build()).complete();
            Message DmMessage = e.getChannel().sendMessage(embed.build()).complete();
            FullChannel.addReaction(Emoji).queue();
            DmMessage.addReaction(Emoji).queue();

            Giveaway giveaway = new Giveaway();
            giveaway.setPrize(Prize);
            giveaway.setMessageID(FullChannel.getIdLong());
            giveaway.setHostID(e.getAuthor().getIdLong());
            giveaway.setChannelID(Giveawaychannel.getIdLong());
            giveaway.setEndTime(endTimeMillis);
            giveaway.setWinnerAmount(Long.parseLong(WinnerAmount));
            giveaway.setEnded(false);
            giveaway.addToCache();

            GiveawayCache.get().removeUser(e.getAuthor());
        }
    }
}
