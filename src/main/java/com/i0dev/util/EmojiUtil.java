package main.java.com.i0dev.util;

import net.dv8tion.jda.api.entities.MessageReaction;

public class EmojiUtil {

    public static String getSimpleEmoji(String Emoji) {
        if (Emoji.length() < 20) {
            return MessageReaction.ReactionEmote.fromUnicode(Emoji, initJDA.get().getJda()).getEmoji();
        } else {
            return Emoji.substring(2, Emoji.length() - 20);
        }
    }

    public static String getUnicodeFromCodepoints(String s) {
        return "U" + s.split("U")[1];
    }

    public static String getEmojiWithoutArrow(String Emoji) {
        if (Emoji.length() < 20) {
            return MessageReaction.ReactionEmote.fromUnicode(Emoji, initJDA.get().getJda()).getEmoji();
        } else {
            return Emoji.substring(0, Emoji.length() - 1);
        }
    }

}
