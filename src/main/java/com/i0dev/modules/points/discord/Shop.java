package com.i0dev.modules.points.discord;

import com.i0dev.modules.points.Option;
import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.object.objects.DiscordCommand;
import com.i0dev.utility.EmbedFactory;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.FormatUtil;
import com.i0dev.utility.util.MessageUtil;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.util.List;

public class Shop extends DiscordCommand {

    public static boolean PERMISSION_STRICT;
    public static boolean PERMISSION_LITE;
    public static boolean PERMISSION_ADMIN;
    public static boolean ENABLED;

    private static String DISCORD_TITLE_FORMAT;
    private static String DISCORD_DESC_FORMAT;
    private static List<JSONObject> OPTIONS;

    private static String MESSAGE_TITLE;
    private static String MESSAGE_DESC;
    private static boolean INLINE;
    private static long ITEMS_PER_PAGE;

    @Override
    public void init() {
        PERMISSION_STRICT = Configuration.getBoolean("modules.points.parts.shop.permission.strict");
        PERMISSION_LITE = Configuration.getBoolean("modules.points.parts.shop.permission.lite");
        PERMISSION_ADMIN = Configuration.getBoolean("modules.points.parts.shop.permission.admin");
        ENABLED = Configuration.getBoolean("modules.points.parts.shop.enabled");

        DISCORD_TITLE_FORMAT = Configuration.getString("pointShop.general.discordTitleFormat");
        DISCORD_DESC_FORMAT = Configuration.getString("pointShop.general.discordDescFormat");
        OPTIONS = Configuration.getObjectList("pointShop.options");

        MESSAGE_TITLE = Configuration.getString("modules.points.parts.shop.message.title");
        MESSAGE_DESC = Configuration.getString("modules.points.parts.shop.message.desc");
        INLINE = Configuration.getBoolean("pointShop.general.inline");
        ITEMS_PER_PAGE = Configuration.getLong("pointShop.general.itemsPerPage");

    }

    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Points Shop")) {
            return;
        }

        int page;
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            page = 1;
        } else {
            if (!FormatUtil.isInt(message[2])) {
                MessageUtil.sendMessage(e.getChannel().getIdLong(), "{authorTagBold}, " + message[2] + " is not a valid number. Try again", e.getAuthor());
                return;
            }
            page = Integer.parseInt(message[2]);
        }

        int size = (int) Math.floor(ITEMS_PER_PAGE);
        int maxPages = (int) (Math.ceil(OPTIONS.size()) / ((double) size)) + 1;
        int startingIndex = (page - 1) * size;
        int endingIndex = Math.min(page * size, OPTIONS.size());
        List<JSONObject> jsonObjects = OPTIONS.subList(startingIndex, endingIndex);
        MessageEmbed.Field[] fields = new MessageEmbed.Field[endingIndex - startingIndex];
        for (int i = 0; i < jsonObjects.size(); i++) {
            Option option = PointsManager.makeObject(jsonObjects.get(i), false);
            fields[i] = new MessageEmbed.Field(DISCORD_TITLE_FORMAT
                    .replace("{id}", startingIndex + i + 1 + "")
                    .replace("{displayName}", option.getDiscordDisplayName())
                    .replace("{price}", option.getPrice() + ""),
                    DISCORD_DESC_FORMAT
                            .replace("{id}", startingIndex + i + 1 + "")
                            .replace("{displayName}", option.getDiscordDisplayName())
                            .replace("{price}", option.getPrice() + "")
                            .replace("{description}", option.getDiscordDescription())
                    , INLINE);
        }
        MessageUtil.sendMessage(e.getChannel().getIdLong(), EmbedFactory.createEmbed(MESSAGE_TITLE.replace("{page}", page + "").replace("{max}", maxPages + ""), MESSAGE_DESC, "", "", "", fields).build());
    }

    public List<JSONObject> paginate(List<JSONObject> list, int itemsPerPage, int requestedPage) {
        int size = (int) Math.floor(itemsPerPage);
        int maxPages = (int) (Math.ceil(list.size()) / ((double) size)) + 1;
        int startingIndex = (requestedPage - 1) * size;
        int endingIndex = Math.min(requestedPage * size, OPTIONS.size());
        return list.subList(startingIndex, endingIndex);
    }
}
