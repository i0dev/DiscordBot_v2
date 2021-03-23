package main.java.com.i0dev.util;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.ZonedDateTime;

public class EmbedFactory {

    private static final EmbedFactory embedFactory = new EmbedFactory();

    public static EmbedFactory get() {
        return embedFactory;
    }

    private final String colorHex;
    private final String Thumbnail;
    private final String Title;
    private final String Footer;

    public EmbedFactory() {
        colorHex = getConfig.get().getString("messages.embeds.ColorHexCode");
        Thumbnail = getConfig.get().getString("messages.embeds.Thumbnail");
        Title = getConfig.get().getString("messages.embeds.Title");
        Footer = getConfig.get().getString("messages.embeds.Footer");
    }

    public EmbedBuilder createSimpleEmbedNoThumbnail(String description) {

        return new EmbedBuilder()
                .setTimestamp(ZonedDateTime.now())
                .setColor(Color.decode(colorHex))
                .setDescription(description)
                .setTitle(Title)
                .setFooter(Footer);
    }

    public EmbedBuilder createSimpleEmbed(String description) {

        return new EmbedBuilder()
                .setTimestamp(ZonedDateTime.now())
                .setColor(Color.decode(colorHex))
                .setThumbnail(Thumbnail)
                .setDescription(description)
                .setTitle(Title)
                .setFooter(Footer);
    }

    public EmbedBuilder createSimpleEmbed(String Title, String description, String AvatarURL) {

        if (Title == null && AvatarURL != null) {
            return new EmbedBuilder()
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(Color.decode(colorHex))
                    .setThumbnail(AvatarURL)
                    .setDescription(description)
                    .setTitle(this.Title)
                    .setFooter(Footer);
        }
        if (AvatarURL == null && Title != null) {
            return new EmbedBuilder()
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(Color.decode(colorHex))
                    .setDescription(description)
                    .setTitle(Title)
                    .setFooter(Footer);
        }
        if (AvatarURL == null) {
            return new EmbedBuilder()
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(Color.decode(colorHex))
                    .setDescription(description)
                    .setTitle(this.Title)
                    .setFooter(Footer);
        }
        return new EmbedBuilder()
                .setTimestamp(ZonedDateTime.now())
                .setColor(Color.decode(colorHex))
                .setThumbnail(AvatarURL)
                .setDescription(description)
                .setTitle(Title)
                .setFooter(Footer);
    }



    public EmbedBuilder createSimpleEmbed(String title, String description) {
        if (title == null) {
            return new EmbedBuilder()
                    .setTimestamp(ZonedDateTime.now())
                    .setColor(Color.decode(colorHex))
                    .setThumbnail(Thumbnail)
                    .setDescription(description)
                    .setTitle(this.Title)
                    .setFooter(Footer);
        }
        return new EmbedBuilder()
                .setTimestamp(ZonedDateTime.now())
                .setColor(Color.decode(colorHex))
                .setThumbnail(Thumbnail)
                .setDescription(description)
                .setTitle(title)
                .setFooter(Footer);
    }

    public EmbedBuilder imageEmbed(String title, String imageURL) {

        return new EmbedBuilder()
                .setColor(Color.decode(colorHex))
                .setTitle(title)
                .setImage(imageURL);
    }
}
