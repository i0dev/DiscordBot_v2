package com.i0dev.commands.discord.points;

import com.i0dev.object.engines.PermissionHandler;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.EmbedFactory;
import com.i0dev.utility.GlobalCheck;
import com.i0dev.utility.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Shop {

    private static final boolean PERMISSION_STRICT = Configuration.getBoolean("modules.points.parts.shop.permission.strict");
    private static final boolean PERMISSION_LITE = Configuration.getBoolean("modules.points.parts.shop.permission.lite");
    private static final boolean PERMISSION_ADMIN = Configuration.getBoolean("modules.points.parts.shop.permission.admin");
    private static final boolean ENABLED = Configuration.getBoolean("modules.points.parts.shop.enabled");

    private static final String DISCORD_TITLE_FORMAT = Configuration.getString("pointShop.general.discordTitleFormat");
    private static final String DISCORD_DESC_FORMAT = Configuration.getString("pointShop.general.discordDescFormat");
    private static final List<JSONObject> OPTIONS = Configuration.getObjectList("pointShop.options");


    public static void run(GuildMessageReceivedEvent e) {
        if (!GlobalCheck.checkBasic(e, ENABLED, new PermissionHandler(PERMISSION_LITE, PERMISSION_STRICT, PERMISSION_ADMIN), "Points Shop")) {
            return;
        }

        int page;
        String[] message = e.getMessage().getContentRaw().split(" ");
        if (message.length == 2) {
            page = 1;
        } else {
            page = Integer.parseInt(message[2]);
        }

        //Hi

        int size = 5;
        int maxPages = (int) Math.ceil(OPTIONS.size() / size);
        int startingIndex = (page - 1) * size;
        int endingIndex = Math.min(page * size, OPTIONS.size());
        List<JSONObject> jsonObjects = OPTIONS.subList(startingIndex, endingIndex);
        MessageEmbed.Field[] fields = new MessageEmbed.Field[endingIndex - startingIndex];
        for (int i = 0; i < jsonObjects.size(); i++) {
            Option option = makeObject(jsonObjects.get(i));
            fields[i] = new MessageEmbed.Field(DISCORD_TITLE_FORMAT
                    .replace("{id}", startingIndex + i + "")
                    .replace("{displayName}", option.getDiscordDisplayName())
                    .replace("{price}", option.getPrice() + ""),
                    DISCORD_DESC_FORMAT
                            .replace("{id}", startingIndex + i + "")
                            .replace("{description}", option.getDiscordDescription())
                    , false);
        }
        MessageUtil.sendMessage(e.getChannel().getIdLong(), EmbedFactory.createEmbed("Discord Points Shop Page [" + page + "/" + maxPages + "]", "", "", "", "", fields).build());
    }

    public static Option makeObject(JSONObject object) {
        Option option = new Option();
        option.setPrice(((long) object.get("price")));
        option.setDiscordDisplayName(object.get("discordDisplayName").toString());
        option.setIngameDisplayName(object.get("ingameDisplayName").toString());
        option.setCommandsToRun(((ArrayList<String>) object.get("commandsToRun")));
        option.setDiscordDescription(object.get("discordDescription").toString());
        option.setIngameDescription(((ArrayList<String>) object.get("ingameDescription")));
        option.setItemMaterial(object.get("itemMaterial").toString());
        option.setItemAmount(((long) object.get("itemAmount")));
        option.setItemData(((long) object.get("itemData")));
        return option;
    }

}

@Setter
@Getter
class Option {
    private long price = 0;
    private String discordDisplayName = "";
    private String ingameDisplayName = "";
    private ArrayList<String> commandsToRun;
    private String discordDescription = "";
    private ArrayList<String> ingameDescription;
    private String itemMaterial;
    private long itemAmount = 0;
    private long itemData = 0;

    public Option() {
        this.commandsToRun = new ArrayList<>();
        this.ingameDescription = new ArrayList<>();
    }
}
