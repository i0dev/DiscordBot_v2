package main.java.com.i0dev.command.reactionroles;

import main.java.com.i0dev.entity.Blacklist;
import main.java.com.i0dev.entity.ReactionRoles;
import main.java.com.i0dev.util.*;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class reactionRoleResponses extends ListenerAdapter {

    private final String Identifier = "Reaction Role Creator";
    private final boolean REQUIRE_PERMISSIONS = getConfig.get().getBoolean("commands.reactionRoles.requirePermission");
    private final boolean REQUIRE_LITE_PERMISSIONS = getConfig.get().getBoolean("commands.reactionRoles.permissionLiteMode");
    private final boolean COMMAND_ENABLED = getConfig.get().getBoolean("commands.reactionRoles.enabled");
    private final String createdReactionRolesTitle = getConfig.get().getString("commands.reactionRoles.createdReactionRolesTitle");
    private final String createdReactionRolesFormat = getConfig.get().getString("commands.reactionRoles.createdReactionRolesFormat");


    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (Blacklist.get().isBlacklisted(e.getAuthor())) return;
        if (!COMMAND_ENABLED) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }
        if (!InternalPermission.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, conf.GENERAL_MAIN_GUILD, e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(conf.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }

        if (!ReactionRoleCache.get().getMap().containsKey(e.getAuthor())) return;
        if (e.getMessage().getContentRaw().equalsIgnoreCase(conf.GENERAL_BOT_PREFIX + "cancel")) {
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("You have canceled your current reaction roles creator.").build()).queue();
            ReactionRoleCache.get().removeUser(e.getAuthor());

            return;
        }
        ReactionRoleCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + getConfig.get().getLong("general.creatorTimeouts"));

        Integer CurrentQuestion = (Integer) ReactionRoleCache.get().getMap().get(e.getAuthor());
        String messageContent = e.getMessage().getContentRaw();
        LinkedHashMap<String, String> previousResponses = (LinkedHashMap<String, String>) ReactionRoleCache.get().getResponseMap().get(e.getAuthor());
        if (previousResponses == null) {
            previousResponses = new LinkedHashMap<>();
        }
        ArrayList<String> Questions = (ArrayList<String>) ReactionRoleCache.get().getQuestionMap().get(e.getAuthor());
        if (Questions == null) {
            Questions = new ArrayList<>();
            Questions.add("Please enter the channel you would like the reaction role message to go into.");
            Questions.add("What is the reaction panel description?");
            Questions.add("Please enter the amount of reaction options you want to create.");
            ReactionRoleCache.get().getQuestionMap().put(e.getAuthor(), Questions);

        }

        if (CurrentQuestion == 0) {
            TextChannel channel = FindFromString.get().getTextChannel(messageContent.split(" ")[0], e.getMessage());
            if (channel == null) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Cannot find that channel. Please try again").build()).queue();
                return;
            }
            previousResponses.put(Questions.get(CurrentQuestion), channel.getId());
            ReactionRoleCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            ReactionRoleCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail(Questions.get(1)).build()).queue();
            return;
        }
        if (CurrentQuestion == 1) {
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            ReactionRoleCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            ReactionRoleCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail(Questions.get(2)).build()).queue();
            return;
        }
        if (CurrentQuestion == 2) {
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            ReactionRoleCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            if (!Prettify.isInt(messageContent)) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Incorrect input. Please enter a number.").build()).queue();
                return;
            } else if (Integer.parseInt(messageContent) <= 0) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Incorrect input. Please enter a number greater than 0.").build()).queue();
                return;
            }
            for (int i = 0; i < Integer.parseInt(messageContent); i++) {
                Questions.add("Enter the **Emoji** for the `" + Prettify.formatNumber((i + 1)) + "` option in the reaction role panel.");
                Questions.add("Enter the **Role** for the `" + Prettify.formatNumber((i + 1)) + "` option in the reaction role panel.");
            }
            Questions.add("Type `\"submit\"` to submit your reaction role creator");
            ReactionRoleCache.get().getQuestionMap().put(e.getAuthor(), Questions);

            CurrentQuestion++;
            ReactionRoleCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail(Questions.get(3)).build()).queue();

            return;
        }
        if (CurrentQuestion > 2 && CurrentQuestion < Questions.size() - 1) {
            if (CurrentQuestion % 2 == 0) {
                Role role = FindFromString.get().getRole(messageContent, e.getMessage());
                if (role == null) {
                    e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Cannot find that role. Please try again").build()).queue();
                    return;
                }
                previousResponses.put(Questions.get(CurrentQuestion), role.getId());
                ReactionRoleCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
                CurrentQuestion++;
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail(Questions.get(CurrentQuestion)).build()).queue();
                ReactionRoleCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
                return;
            } else {
                previousResponses.put(Questions.get(CurrentQuestion), messageContent);
                ReactionRoleCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
                CurrentQuestion++;
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail(Questions.get(CurrentQuestion)).build()).queue();
                ReactionRoleCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
                return;
            }
        }
        if (CurrentQuestion == Questions.size() - 1) {
            if (!messageContent.equalsIgnoreCase("submit")) {
                e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbedNoThumbnail("Unknown message, to submit your reaction panel, type `submit`, to cancel type `" + conf.GENERAL_BOT_PREFIX + "cancel`").build()).queue();
                return;
            }
            ArrayList<String> responsesInOrder = new ArrayList<>();
            ((LinkedHashMap<String, String>) ReactionRoleCache.get().getResponseMap().get(e.getAuthor())).forEach((key, value) -> {
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


            ArrayList<JSONObject> toObject = new ArrayList<>();
            for (int i = 0; i < Emojis.size(); i++) {
                JSONObject json = new JSONObject();
                json.put("roleID", ContentList.get(i));
                json.put("Emoji", Emojis.get(i));
                toObject.add(json);
            }
            for (int i = 0; i < Emojis.size(); i++) {
                options.append(createdReactionRolesFormat
                        .replace("{role}", e.getJDA().getRoleById(ContentList.get(i)).getAsMention())
                        .replace("{emoji}", Emojis.get(i)));
            }

            Message FullChannel = channel.sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(createdReactionRolesTitle, e.getAuthor()), desc + "\n\n" + options.toString()).build()).complete();
            Message DmMessage = e.getChannel().sendMessage(EmbedFactory.get().createSimpleEmbed(Placeholders.convert(createdReactionRolesTitle, e.getAuthor()), desc + "\n\n" + options.toString()).build()).complete();

            ReactionRoles.get().createReactionRole(channel, FullChannel, toObject);

            for (String emoji : Emojis) {
                FullChannel.addReaction(emoji).queue();
                DmMessage.addReaction(emoji).queue();
            }
            ReactionRoleCache.get().removeUser(e.getAuthor());
        }
    }
}