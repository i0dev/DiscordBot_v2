package main.java.com.i0dev.command.applications;

import main.java.com.i0dev.cache.ApplicationCache;
import main.java.com.i0dev.entity.Application;
import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class applyResponses extends ListenerAdapter {

    private final String Identifier = "Apply";
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.apply.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.apply.permissionLiteMode");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.apply.enabled");

    private final String applicationTitle = getConfig.get().getString("commands.apply.applicationTitle");
    private final String applicationDesc = getConfig.get().getString("commands.apply.applicationDesc");
    private final List<String> initialQuestions = getConfig.get().getStringList("commands.apply.Questions");


    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
        if (!ApplicationCache.get().getMap().containsKey(e.getAuthor())) return;

        if (!COMMAND_ENABLED) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }
        if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, conf.GENERAL_MAIN_GUILD, e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }

        if (e.getMessage().getContentRaw().equalsIgnoreCase(conf.GENERAL_BOT_PREFIX + "cancel")) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("You have canceled your current application.").build()).queue();
            ApplicationCache.get().removeUser(e.getAuthor());

            return;
        }
        ApplicationCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + getConfig.get().getLong("general.creatorTimeouts"));

        Integer CurrentQuestion = (Integer) ApplicationCache.get().getMap().get(e.getAuthor());
        String messageContent = e.getMessage().getContentRaw();
        LinkedHashMap<String, String> previousResponses = (LinkedHashMap<String, String>) ApplicationCache.get().getResponseMap().get(e.getAuthor());
        if (previousResponses == null) {
            previousResponses = new LinkedHashMap<>();
        }
        ArrayList<String> Questions = (ArrayList<String>) ApplicationCache.get().getQuestionMap().get(e.getAuthor());
        if (Questions == null) {
            Questions = new ArrayList<>(initialQuestions);
            ApplicationCache.get().getQuestionMap().put(e.getAuthor(), Questions);

        }
        if (CurrentQuestion < Questions.size() - 1) {
            if (messageContent.length() >= 1024) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Your response cannot be larger than 1024 characters!").build()).queue();
                return;
            }
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            ApplicationCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            ApplicationCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail(Questions.get(CurrentQuestion)).build()).queue();
            return;
        }

        if (CurrentQuestion == Questions.size() - 1) {

            if (!messageContent.equalsIgnoreCase("submit")) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Type `submit`, to cancel type `" + conf.GENERAL_BOT_PREFIX + "cancel`").build()).queue();
                previousResponses.put(Questions.get(CurrentQuestion), messageContent);
                ApplicationCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
                return;
            }
            ArrayList<String> responsesInOrder = new ArrayList<>();
            ((LinkedHashMap<String, String>) ApplicationCache.get().getResponseMap().get(e.getAuthor())).forEach((key, value) -> {
                responsesInOrder.add(value);
            });


            String time = "[" + ZonedDateTime.now().getMonth().getValue() + "/" + ZonedDateTime.now().getDayOfMonth() + "/" + ZonedDateTime.now().getYear() + " " + ZonedDateTime.now().getHour() + ":" + ZonedDateTime.now().getMinute() + ":" + ZonedDateTime.now().getMinute() + "]";

            EmbedBuilder embed = new EmbedBuilder()
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setThumbnail(e.getAuthor().getEffectiveAvatarUrl())
                    .setDescription(Placeholders.convert(applicationDesc.replace("{time}", time), e.getAuthor()))
                    .setTitle(Placeholders.convert(applicationTitle, e.getAuthor()))
                    .setFooter(conf.EMBED_FOOTER);
            EmbedBuilder secondPage = new EmbedBuilder()
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setFooter(conf.EMBED_FOOTER);
            EmbedBuilder thirdPage = new EmbedBuilder()
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(Color.decode(conf.EMBED_COLOR_HEX_CODE))
                    .setFooter(conf.EMBED_FOOTER);

            int TotalLength = (applicationTitle.length() + applicationDesc.length() + conf.EMBED_FOOTER.length());
            int TotalLengthPage2 = (conf.EMBED_FOOTER.length());
            for (int i = 0; i < responsesInOrder.size(); i++) {
                TotalLength += (responsesInOrder.get(i).length() + Questions.get(i).length());
                if (TotalLength >= 5999) {
                    TotalLengthPage2 += (responsesInOrder.get(i).length() + Questions.get(i).length());
                    if (TotalLengthPage2 >= 5999) {
                        thirdPage.addField(Questions.get(i), responsesInOrder.get(i), false);
                    } else {
                        secondPage.addField(Questions.get(i), responsesInOrder.get(i), false);
                    }
                } else {
                    embed.addField(Questions.get(i), responsesInOrder.get(i), false);
                }
            }


            Message FullChannel = MessageUtil.sendMessageComplete(conf.APPLICATIONS_CHANNEL, embed.build());
            Message DmMessage = e.getChannel().sendMessage(embed.build()).complete();
            if (secondPage.getFields().size() > 0) {
                Message FullChannelPage2 = MessageUtil.sendMessageComplete(conf.APPLICATIONS_CHANNEL, secondPage.build());
                Message DmMessagePage2 = e.getChannel().sendMessage(secondPage.build()).complete();
            }
            if (thirdPage.getFields().size() > 0) {
                Message FullChannelPage3 = MessageUtil.sendMessageComplete(conf.APPLICATIONS_CHANNEL, thirdPage.build());
                Message DmMessagePage3 = e.getChannel().sendMessage(thirdPage.build()).complete();
            }
            ApplicationCache.get().removeUser(e.getAuthor());
            Application.get().addUser(e.getAuthor(), Questions, responsesInOrder);
        }
    }
}