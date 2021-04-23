package com.i0dev.commands.discord.creators;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.cache.PollCache;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PollCreator extends ListenerAdapter {

    public static final String Identifier = "Poll Creator";
    private final List<String> COMMAND_ALIASES = Configuration.getStringList("commands.pollCreator.aliases");
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.pollCreator.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.pollCreator.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.pollCreator.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.pollCreator.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.pollCreator.enabled");
    public static final String alreadyCreating = Configuration.getString("commands.pollCreator.alreadyCreating");
    public static final String createdPollTitle = Configuration.getString("commands.pollCreator.createdPollTitle");
    public static final String createdPollFormat = Configuration.getString("commands.pollCreator.createdPollFormat");

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Poll Creator")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.POLL_CREATOR_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        if (PollCache.get().getMap().containsKey(e.getAuthor()) || PollCache.get().getResponseMap().containsKey(e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(alreadyCreating, e.getAuthor())).build()).queue();
            return;
        }
        PollCache.get().getMap().put(e.getAuthor(), 0);
        PollCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + Configuration.getLong("general.creatorTimeouts"));
        e.getAuthor().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed("Please enter the channel you would like the poll to be posted in.").build()).queue();
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();


    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!PollCache.get().getMap().containsKey(e.getAuthor())) return;

        if (!COMMAND_ENABLED) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, GlobalConfig.GENERAL_MAIN_GUILD, e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }

        if (e.getMessage().getContentRaw().equalsIgnoreCase(GlobalConfig.GENERAL_BOT_PREFIX + "cancel")) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed("You have canceled your current poll creator.").build()).queue();
            PollCache.get().removeUser(e.getAuthor());

            return;
        }
        PollCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + Configuration.getLong("general.creatorTimeouts"));

        Integer CurrentQuestion = (Integer) PollCache.get().getMap().get(e.getAuthor());
        String messageContent = e.getMessage().getContentRaw();
        LinkedHashMap<String, String> previousResponses = (LinkedHashMap<String, String>) PollCache.get().getResponseMap().get(e.getAuthor());
        if (previousResponses == null) {
            previousResponses = new LinkedHashMap<>();
        }
        ArrayList<String> Questions = (ArrayList<String>) PollCache.get().getQuestionMap().get(e.getAuthor());
        if (Questions == null) {
            Questions = new ArrayList<>();
            Questions.add("Please enter the channel you would like the poll to be posted in.");
            Questions.add("What is the poll about? Overall description.");
            Questions.add("Please enter the amount of poll options you want to create.");
            PollCache.get().getQuestionMap().put(e.getAuthor(), Questions);

        }

        if (CurrentQuestion == 0) {
            TextChannel pollChannel = FindFromString.get().getTextChannel(messageContent.split(" ")[0], e.getMessage());
            if (pollChannel == null) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Cannot find that channel. Please try again").build()).queue();
                return;
            }
            previousResponses.put(Questions.get(CurrentQuestion), pollChannel.getId());
            PollCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            PollCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(1)).build()).queue();
            return;
        }
        if (CurrentQuestion == 1) {
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            PollCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            PollCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(2)).build()).queue();
            return;
        }
        if (CurrentQuestion == 2) {
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            PollCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            if (!FormatUtil.isInt(messageContent)) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Incorrect input. Please enter a number.").build()).queue();
                return;
            } else if (Integer.parseInt(messageContent) <= 0) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Incorrect input. Please enter a number greater than 0.").build()).queue();
                return;
            }
            for (int i = 0; i < Integer.parseInt(messageContent); i++) {
                Questions.add("Enter the **Emoji** for the `" + FormatUtil.formatNumber((i + 1)) + "` option in the poll.");
                Questions.add("Enter the **Content** for the `" + FormatUtil.formatNumber((i + 1)) + "` option in the poll.");
            }
            Questions.add("Type `\"submit\"` to submit your poll creator");
            PollCache.get().getQuestionMap().put(e.getAuthor(), Questions);

            CurrentQuestion++;
            PollCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(3)).build()).queue();

            return;
        }
        if (CurrentQuestion > 2 && CurrentQuestion < Questions.size() - 1) {
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            PollCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(CurrentQuestion)).build()).queue();
            PollCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            return;

        }
        if (CurrentQuestion == Questions.size() - 1) {
            if (!messageContent.equalsIgnoreCase("submit")) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Unknown message, to submit your poll, type `submit`, to cancel type `" + GlobalConfig.GENERAL_BOT_PREFIX + "cancel`").build()).queue();
                return;
            }
            ArrayList<String> responsesInOrder = new ArrayList<>();
            ((LinkedHashMap<String, String>) PollCache.get().getResponseMap().get(e.getAuthor())).forEach((key, value) -> {
                responsesInOrder.add(value);
            });
            StringBuilder options = new StringBuilder();
            TextChannel channel = e.getJDA().getTextChannelById(responsesInOrder.get(0));
            String desc = responsesInOrder.get(1);
            ArrayList<String> Emojis = new ArrayList<>();
            ArrayList<String> ContentList = new ArrayList<>();

            for (int i = 3; i < responsesInOrder.size(); i++) {
                if (i % 2 == 0) {
                    ContentList.add(responsesInOrder.get(i));
                } else {
                    Emojis.add(responsesInOrder.get(i));
                }
            }
            for (int i = 0; i < Emojis.size(); i++) {
                options.append(createdPollFormat
                        .replace("{content}", ContentList.get(i))
                        .replace("{emoji}", "" + Emojis.get(i)));
            }
            Message FullChannel = channel.sendMessage(EmbedFactory.createEmbed(Placeholders.convert(createdPollTitle, e.getAuthor()), desc + "\n\n" + options.toString()).build()).complete();
            Message DmMessage = e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(createdPollTitle, e.getAuthor()), desc + "\n\n" + options.toString()).build()).complete();
            for (String emoji : Emojis) {
                FullChannel.addReaction(emoji).queue();
                DmMessage.addReaction(emoji).queue();
            }
            PollCache.get().removeUser(e.getAuthor());
        }
    }
}
