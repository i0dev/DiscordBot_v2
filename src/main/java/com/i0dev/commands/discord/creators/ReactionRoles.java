package com.i0dev.commands.discord.creators;

import com.i0dev.commands.DiscordCommandManager;
import com.i0dev.cache.ReactionRoleCache;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.*;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.PermissionUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ReactionRoles extends ListenerAdapter {

    public static final String Identifier = "Reaction Role Creator";
    private final List<String> COMMAND_ALIASES = Configuration.getStringList("commands.reactionRoles.aliases");
    public static final boolean REQUIRE_PERMISSIONS = Configuration.getBoolean("commands.reactionRoles.requirePermission");
    public static final boolean REQUIRE_LITE_PERMISSIONS = Configuration.getBoolean("commands.reactionRoles.permissionLiteMode");
    public static final String MESSAGE_CONTENT = Configuration.getString("commands.reactionRoles.messageContent");
    public static final String MESSAGE_FORMAT = Configuration.getString("commands.reactionRoles.format");
    public static final boolean COMMAND_ENABLED = Configuration.getBoolean("commands.reactionRoles.enabled");
    public static final String alreadyCreating = Configuration.getString("commands.reactionRoles.alreadyCreating");
    public static final String createdReactionRolesTitle = Configuration.getString("commands.reactionRoles.createdReactionRolesTitle");
    public static final String createdReactionRolesFormat = Configuration.getString("commands.reactionRoles.createdReactionRolesFormat");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, COMMAND_ENABLED, new PermissionHandler(REQUIRE_LITE_PERMISSIONS, REQUIRE_PERMISSIONS, false), "Reaction Role Creator")) {
            return;
        }
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length != 1) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_FORMAT.replace("{command}", GlobalConfig.GENERAL_BOT_PREFIX + DiscordCommandManager.REACTION_ROLE_CREATOR_ALIASES.get(0)), e.getAuthor())).build()).queue();
            return;
        }
        if (ReactionRoleCache.get().getMap().containsKey(e.getAuthor()) || ReactionRoleCache.get().getResponseMap().containsKey(e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(alreadyCreating, e.getAuthor())).build()).queue();
            return;
        }
        ReactionRoleCache.get().getMap().put(e.getAuthor(), 0);
        ReactionRoleCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + Configuration.getLong("general.creatorTimeouts"));
        e.getAuthor().openPrivateChannel().complete().sendMessage(EmbedFactory.createEmbed("Please enter the channel you would like the reaction role panel to be posted in.").build()).queue();
        e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(MESSAGE_CONTENT, e.getAuthor())).build()).queue();

    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if (!ReactionRoleCache.get().getMap().containsKey(e.getAuthor())) return;

        if (!COMMAND_ENABLED) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NOT_ENABLED.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }
        if (!PermissionUtil.get().hasPermission(REQUIRE_PERMISSIONS, REQUIRE_LITE_PERMISSIONS, GlobalConfig.GENERAL_MAIN_GUILD, e.getAuthor())) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(GlobalConfig.MESSAGE_COMMAND_NO_PERMISSION.replace("{command}", Identifier), e.getAuthor())).build()).queue();
            return;
        }

        if (e.getMessage().getContentRaw().equalsIgnoreCase(GlobalConfig.GENERAL_BOT_PREFIX + "cancel")) {
            e.getChannel().sendMessage(EmbedFactory.createEmbed("You have canceled your current reaction roles creator.").build()).queue();
            ReactionRoleCache.get().removeUser(e.getAuthor());

            return;
        }
        ReactionRoleCache.get().getTimeoutMap().put(e.getAuthor(), System.currentTimeMillis() + Configuration.getLong("general.creatorTimeouts"));

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
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Cannot find that channel. Please try again").build()).queue();
                return;
            }
            previousResponses.put(Questions.get(CurrentQuestion), channel.getId());
            ReactionRoleCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            ReactionRoleCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(1)).build()).queue();
            return;
        }
        if (CurrentQuestion == 1) {
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            ReactionRoleCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            CurrentQuestion++;
            ReactionRoleCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(2)).build()).queue();
            return;
        }
        if (CurrentQuestion == 2) {
            previousResponses.put(Questions.get(CurrentQuestion), messageContent);
            ReactionRoleCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
            if (!FormatUtil.isInt(messageContent)) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Incorrect input. Please enter a number.").build()).queue();
                return;
            } else if (Integer.parseInt(messageContent) <= 0) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Incorrect input. Please enter a number greater than 0.").build()).queue();
                return;
            }
            for (int i = 0; i < Integer.parseInt(messageContent); i++) {
                Questions.add("Enter the **Emoji** for the `" + FormatUtil.formatNumber((i + 1)) + "` option in the reaction role panel.");
                Questions.add("Enter the **Role** for the `" + FormatUtil.formatNumber((i + 1)) + "` option in the reaction role panel.");
            }
            Questions.add("Type `\"submit\"` to submit your reaction role creator");
            ReactionRoleCache.get().getQuestionMap().put(e.getAuthor(), Questions);

            CurrentQuestion++;
            ReactionRoleCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
            e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(3)).build()).queue();

            return;
        }
        if (CurrentQuestion > 2 && CurrentQuestion < Questions.size() - 1) {
            if (CurrentQuestion % 2 == 0) {
                Role role = FindFromString.get().getRole(messageContent, e.getMessage());
                if (role == null) {
                    e.getChannel().sendMessage(EmbedFactory.createEmbed("Cannot find that role. Please try again").build()).queue();
                    return;
                }
                previousResponses.put(Questions.get(CurrentQuestion), role.getId());
                ReactionRoleCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
                CurrentQuestion++;
                e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(CurrentQuestion)).build()).queue();
                ReactionRoleCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
                return;
            } else {
                previousResponses.put(Questions.get(CurrentQuestion), messageContent);
                ReactionRoleCache.get().getResponseMap().put(e.getAuthor(), previousResponses);
                CurrentQuestion++;
                e.getChannel().sendMessage(EmbedFactory.createEmbed(Questions.get(CurrentQuestion)).build()).queue();
                ReactionRoleCache.get().getMap().put(e.getAuthor(), CurrentQuestion);
                return;
            }

        }

        if (CurrentQuestion == Questions.size() - 1) {
            if (!messageContent.equalsIgnoreCase("submit")) {
                e.getChannel().sendMessage(EmbedFactory.createEmbed("Unknown message, to submit your reaction panel, type `submit`, to cancel type `" + GlobalConfig.GENERAL_BOT_PREFIX + "cancel`").build()).queue();
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
                if (Emojis.get(i).length() < 19) {
                    json.put("Emoji", String.format("%x", (int) Emojis.get(i).charAt(0)));

                } else {
                    json.put("Emoji", Emojis.get(i));
                }
                toObject.add(json);
            }
            for (int i = 0; i < Emojis.size(); i++) {
                String EmojiInit = Emojis.get(i);
                options.append(createdReactionRolesFormat
                        .replace("{role}", e.getJDA().getRoleById(ContentList.get(i)).getAsMention())
                        .replace("{emoji}", EmojiInit));
            }

            Message FullChannel = channel.sendMessage(EmbedFactory.createEmbed(Placeholders.convert(createdReactionRolesTitle, e.getAuthor()), desc + "\n\n" + options.toString()).build()).complete();
            Message DmMessage = e.getChannel().sendMessage(EmbedFactory.createEmbed(Placeholders.convert(createdReactionRolesTitle, e.getAuthor()), desc + "\n\n" + options.toString()).build()).complete();

            com.i0dev.object.objects.ReactionRoles.get().createReactionRole(channel, FullChannel, toObject);

            for (String Iemoji : Emojis) {
                String emoji = Iemoji;
                if (emoji.length() > 19) {
                    emoji = emoji.substring(0, emoji.length() - 1);
                }
                FullChannel.addReaction(emoji).queue();
                DmMessage.addReaction(emoji).queue();
            }
            ReactionRoleCache.get().removeUser(e.getAuthor());
        }
    }

}
