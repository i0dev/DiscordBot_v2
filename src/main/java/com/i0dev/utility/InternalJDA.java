package com.i0dev.utility;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;

public class InternalJDA {

    @Getter
    @Setter
    private static JDA jda = null;

    public static void createJDA() {
        try {
            jda = JDABuilder.create(Configuration.getString("general.token"),
                    GatewayIntent.DIRECT_MESSAGES,
                    GatewayIntent.GUILD_BANS,
                    GatewayIntent.GUILD_EMOJIS,
                    GatewayIntent.GUILD_PRESENCES,
                    GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_INVITES,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS,
                    GatewayIntent.GUILD_MESSAGE_TYPING,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.DIRECT_MESSAGE_TYPING,
                    GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setContextEnabled(true)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(
                            CacheFlag.ACTIVITY,
                            CacheFlag.VOICE_STATE,
                            CacheFlag.MEMBER_OVERRIDES,
                            CacheFlag.EMOTE,
                            CacheFlag.CLIENT_STATUS)
                    .build()
                    .awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void registerListeners() {
        Reflections reflections = new Reflections("com.i0dev");
        int count = 0;
        for (Class<? extends EventListener> listener : reflections.getSubTypesOf(EventListener.class)) {
            try {
                if (listener.getName().equals("net.dv8tion.jda.api.hooks.ListenerAdapter")) continue;
                getJda().addEventListener(listener.newInstance());
                count++;
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Registered [" + count + "] total event listeners.");

    }
}
