package com.i0dev.commands.discord.tempApplications;

import com.i0dev.cache.ApplicationCache;
import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.object.objects.Application;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.object.discordLinking.DPlayerEngine;
import com.i0dev.utility.*;
import com.i0dev.utility.util.MessageUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CommandApply extends ListenerAdapter {
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.apply.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.apply.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.apply.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.apply.format");
    public static final boolean ENABLED = Configuration.getBoolean("commands.apply.enabled");
    public static final String alreadyCreating = Configuration.getString("commands.apply.alreadyCreating");

    private static final List<String> initialQuestions = Configuration.getStringList("commands.apply.Questions");

    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.apply.enabled");

    public static final String applicationTitle = Configuration.getString("commands.apply.applicationTitle");
    public static final String applicationDesc = Configuration.getString("commands.apply.applicationDesc");
    public static final String submitMessage = Configuration.getString("commands.apply.submitMessage");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Apply")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.APPLY_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }


        if (ApplicationCache.get().getMap().containsKey(e.getAuthor()) || ApplicationCache.get().getResponseMap().containsKey(e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(alreadyCreating, e.getAuthor())).build()).queue();
            return;
        }
        ApplicationCache.get().getMap().put(e.getAuthor(), 0);
        ApplicationCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + Configuration.getLong("general.creatorTimeouts"));
        e.getAuthor().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed(initialQuestions.get(0)).build()).queue();
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();


    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (DPlayerEngine.getInstance().isBlacklisted(e.getAuthor())) return;
        if (!ApplicationCache.get().getMap().containsKey(e.getAuthor())) return;

        if (!COMMAND_ENABLED) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", "Apply"), e.getAuthor())).build()).queue();
            return;
        }
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, GlobalConfig.GENERAL_MAIN_GUILD, e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", "Apply"), e.getAuthor())).build()).queue();
            return;
        }

        if (e.getMessage().getContentRaw().equalsIgnoreCase(GlobalConfig.GENERAL_BOT_PREFIX + "cancel")) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed("You have canceled your current application.").build()).queue();
            ApplicationCache.get().removeUser(e.getAuthor());

            return;
        }
        ApplicationCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + Configuration.getLong("general.creatorTimeouts"));

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
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Your response cannot be larger than 1024 characters!").build()).queue();
                return;
            }
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            ApplicationCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            ApplicationCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(CurrentQuestion)).build()).queue();
            return;
        }

        if (CurrentQuestion == Questions.size() - 1) {
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            ApplicationCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            ApplicationCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.createEmbed(submitMessage.replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX)).build()).queue();
            return;
        }
        if (CurrentQuestion == Questions.size() && !messageContent.equalsIgnoreCase("submit")) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(submitMessage.replace("{prefix}", GlobalConfig.GENERAL_BOT_PREFIX)).build()).queue();
            return;
        }

        if (CurrentQuestion == Questions.size() && messageContent.equalsIgnoreCase("submit")) {
            ArrayList<String> responsesInOrder = new ArrayList<>();
            ((LinkedHashMap<String, String>) ApplicationCache.get().getResponseMap().get(e.getAuthor())).forEach((key, value) -> {
                responsesInOrder.add(value);
            });

            String time = "[" + ZonedDateTime.now().getMonth().getValue() + "/" + ZonedDateTime.now().getDayOfMonth() + "/" + ZonedDateTime.now().getYear() + " " + ZonedDateTime.now().getHour() + ":" + ZonedDateTime.now().getMinute() + ":" + ZonedDateTime.now().getMinute() + "]";

            EmbedBuilder embed = new EmbedBuilder()
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                    .setThumbnail(e.getAuthor().getEffectiveAvatarUrl())
                    .setDescription(Placeholders.convert(applicationDesc.replace("{time}", time), e.getAuthor()))
                    .setTitle(Placeholders.convert(applicationTitle, e.getAuthor()))
                    .setFooter(GlobalConfig.EMBED_FOOTER);
            EmbedBuilder secondPage = new EmbedBuilder()
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                    .setFooter(GlobalConfig.EMBED_FOOTER);
            EmbedBuilder thirdPage = new EmbedBuilder()
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE))
                    .setFooter(GlobalConfig.EMBED_FOOTER);

            int TotalLength = (applicationTitle.length() + applicationDesc.length() + GlobalConfig.EMBED_FOOTER.length());
            int TotalLengthPage2 = (GlobalConfig.EMBED_FOOTER.length());
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


            Message FullChannel = MessageUtil.sendMessage(GlobalConfig.APPLICATIONS_CHANNEL, embed.build());
            Message DmMessage = e.getChannel().sendMessage(embed.build()).complete();
            if (secondPage.getFields().size() > 0) {
                Message FullChannelPage2 = MessageUtil.sendMessage(GlobalConfig.APPLICATIONS_CHANNEL, secondPage.build());
                Message DmMessagePage2 = e.getChannel().sendMessage(secondPage.build()).complete();
            }
            if (thirdPage.getFields().size() > 0) {
                Message FullChannelPage3 = MessageUtil.sendMessage(GlobalConfig.APPLICATIONS_CHANNEL, thirdPage.build());
                Message DmMessagePage3 = e.getChannel().sendMessage(thirdPage.build()).complete();
            }
            ApplicationCache.get().removeUser(e.getAuthor());

            Application application = new Application(e.getAuthor());
            application.setAnswers(responsesInOrder);
            application.setQuestions(Questions);
            application.addToCache();
        }
    }

}
