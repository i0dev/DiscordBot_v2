package com.i0dev.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.ZonedDateTime;

public class EmbedFactory {

    public static final String colorHex = Configuration.getString("messages.embeds.ColorHexCode");
    public static final String defaultThumnail = Configuration.getString("messages.embeds.Thumbnail");
    public static final String defaultTitle = Configuration.getString("messages.embeds.Title");
    public static final String defaultFooter = Configuration.getString("messages.embeds.Footer");

    public static EmbedBuilder createEmbed(String description) {
        return createEmbed(null, description, null, null);
    }

    public static EmbedBuilder createEmbed(String title, String description) {
        return createEmbed(title, description, null, null);
    }

    public static EmbedBuilder createEmbed(String title, String description, String footer) {
        return createEmbed(title, description, footer, null);
    }

    public static EmbedBuilder createEmbed(String title, String description, String footer, String thumbnail) {
        return createEmbed(title, description, footer, thumbnail, null);
    }


    public static EmbedBuilder createEmbed(String title, String description, String footer, String thumbnail, String image, MessageEmbed.Field... fields) {

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setTimestamp(ZonedDateTime.now());


        if (title != null && !title.equalsIgnoreCase("")) embedBuilder.setTitle(title);
        else {
            if (defaultTitle != null && !defaultTitle.equalsIgnoreCase("")) embedBuilder.setTitle(defaultTitle);

        }
        if (footer != null && !footer.equalsIgnoreCase("")) embedBuilder.setFooter(footer);
        else {
            if (defaultFooter != null && !defaultFooter.equalsIgnoreCase("")) embedBuilder.setFooter(defaultFooter);

        }
        if (thumbnail != null && !thumbnail.equalsIgnoreCase("")) embedBuilder.setThumbnail(thumbnail);
        else {
            if (defaultThumnail != null && !defaultThumnail.equalsIgnoreCase(""))
                embedBuilder.setThumbnail(defaultThumnail);
        }

        if (description != null && !description.equalsIgnoreCase("")) embedBuilder.setDescription(description);
        if (image != null && !image.equalsIgnoreCase("")) embedBuilder.setImage(image);
        if (colorHex != null && !colorHex.equalsIgnoreCase("")) embedBuilder.setColor(Color.decode(colorHex));

        for (MessageEmbed.Field field : fields) {
            embedBuilder.addField(field);
        }

        return embedBuilder;
    }

    public static EmbedBuilder createImageEmbed(String title, String image) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (colorHex != null && !colorHex.equalsIgnoreCase("")) embedBuilder.setColor(Color.decode(colorHex));

        embedBuilder.setTimestamp(ZonedDateTime.now());
        embedBuilder.setImage(image);
        embedBuilder.setTitle(title);

        return embedBuilder;
    }

}
