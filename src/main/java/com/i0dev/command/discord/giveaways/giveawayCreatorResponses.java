package main.java.com.i0dev.command.discord.giveaways;

import main.java.com.i0dev.cache.GiveawayCache;
import main.java.com.i0dev.object.Blacklist;
import main.java.com.i0dev.object.Giveaway;
import main.java.com.i0dev.utility.*;
import main.java.com.i0dev.utility.util.FormatUtil;
import main.java.com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class giveawayCreatorResponses extends ListenerAdapter {

    private final String Identifier = "Giveaway Creator";
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.gcreate.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.gcreate.permissionLiteMode");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.gcreate.enabled");
    private final String createdGiveawayTitle = getConfig.get().getString("commands.gcreate.createdGiveawayTitle");
    private final String createdGiveawayContent = getConfig.get().getString("commands.gcreate.createdGiveawayContent");
    private final String createdGiveawayFooter = getConfig.get().getString("commands.gcreate.createdGiveawayFooter");
    private final String Emoji = getConfig.get().getString("commands.gcreate.giveawayEmoji");
    private final String giveawayEmojiText = getConfig.get().getString("commands.gcreate.giveawayEmojiText");


    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
        if (!GiveawayCache.get().getMap().containsKey(e.getAuthor())) return;

        if (!COMMAND_ENABLED) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, GlobalConfig.GENERAL_MAIN_GUILD, e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }

        if (e.getMessage().getContentRaw().equalsIgnoreCase(GlobalConfig.GENERAL_BOT_PREFIX + "cancel")) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("You have canceled your current giveaway creator.").build()).queue();
            GiveawayCache.get().removeUser(e.getAuthor());

            return;
        }
        GiveawayCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + getConfig.get().getLong("general.creatorTimeouts"));

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
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Cannot find that channel. Please try again").build()).queue();
                return;
            }
            previousResponses.put(Questions.get(CurrentQuestion), giveawayChannel.getId());
            GiveawayCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            GiveawayCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail(Questions.get(1)).build()).queue();
            return;

        }
        if (CurrentQuestion > 0 && CurrentQuestion < Questions.size() - 1) {
            if (CurrentQuestion == 2) {
                if (!FormatUtil.isInt(messageContent)) {
                    e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Incorrect input. Please enter a number.").build()).queue();
                    return;
                } else if (Integer.parseInt(messageContent) <= 0) {
                    e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Incorrect input. Please enter a number greater than 0.").build()).queue();
                    return;
                }
            }

            if (CurrentQuestion == 3) {
                if (FormatUtil.getTimeMilis(messageContent) == -1) {
                    e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Incorrect input. Please enter a proper date in the format stated.").build()).queue();
                    return;
                }
            }
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            GiveawayCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail(Questions.get(CurrentQuestion)).build()).queue();
            GiveawayCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            return;
        }

        if (CurrentQuestion == Questions.size() - 1) {
            if (!messageContent.equalsIgnoreCase("submit")) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Unknown message, to create your giveaway, type `submit`, to cancel type `" + GlobalConfig.GENERAL_BOT_PREFIX + "cancel`").build()).queue();
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
            String GiveawayTime = responsesInOrder.get(3);
            long endTimeMillis = System.currentTimeMillis() + FormatUtil.getTimeMilis(responsesInOrder.get(3));
            ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endTimeMillis), ZoneId.of("America/New_York"));

            desc.append(Placeholders.convert(createdGiveawayContent
                    .replace("{emoji}", giveawayEmojiText)
                    .replace("{winnerCount}", WinnerAmount)
                    .replace("{length}", GiveawayTime)
                    .replace("{prize}", Prize), e.getAuthor()));

            EmbedBuilder embed = new EmbedBuilder()
                    .setTimestamp(time)
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                    .setThumbnail(GlobalConfig.EMBED_THUMBNAIL)
                    .setDescription(desc.toString())
                    .setTitle(Placeholders.convert(createdGiveawayTitle, e.getAuthor()))
                    .setFooter(Placeholders.convert(createdGiveawayFooter, e.getAuthor()));

            Message FullChannel = Giveawaychannel.sendMessage(embed.build()).complete();
            Message DmMessage = e.getChannel().sendMessage(embed.build()).complete();
            FullChannel.addReaction(Emoji).queue();
            DmMessage.addReaction(Emoji).queue();
            Giveaway.get().createGiveaway(e.getAuthor(), Prize, Giveawaychannel, FullChannel, endTimeMillis, Integer.parseInt(WinnerAmount));

            GiveawayCache.get().removeUser(e.getAuthor());
        }
    }
}