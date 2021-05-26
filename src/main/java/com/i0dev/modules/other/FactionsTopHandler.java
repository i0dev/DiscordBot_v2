package com.i0dev.modules.other;

import com.i0dev.utility.Configuration;
import com.i0dev.utility.GlobalConfig;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionTopData;
import com.massivecraft.factions.entity.FactionValue;
import com.massivecraft.factions.task.TaskFactionTopCalculate;
import com.massivecraft.massivecore.collections.MassiveMapDef;
import com.massivecraft.massivecore.util.Txt;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class FactionsTopHandler {

    public static MassiveMapDef<String, FactionValue> getData() {
        MassiveMapDef<String, FactionValue> data;
        if (TaskFactionTopCalculate.get().isRunning()) {
            data = (new MassiveMapDef<>(FactionTopData.get().getBackupFactionValues()));
        } else {
            data = (new MassiveMapDef<>(FactionTopData.get().getFactionValues()));
        }
        return data;
    }

    public static MessageEmbed getFTOPEmbed() {
        MassiveMapDef<String, FactionValue> data = FactionsTopHandler.getData();
        StringBuilder totalList = new StringBuilder();
        int iterator = 1;
        for (String factionID : data.keySet()) {
            if (iterator >= 11) break;
            Faction faction = Faction.get(factionID);
            if (faction == null) continue;
            if (data.get(factionID).getTotalSpawnerValue() <= 0) continue;
            long value = data.get(factionID).getTotalSpawnerValue();
            String dailyPercentage = FactionsTopHandler.getDailyChange(faction);
            totalList.append(Configuration.getString("events.autoFtop.format")
                    .replace("{faction}", faction.getName())
                    .replace("{total}", NumberFormat.getIntegerInstance().format(value))
                    .replace("{change}", dailyPercentage)
                    .replace("{place}", iterator + "")
            );
            totalList.append("\n");
            iterator++;
        }
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(Configuration.getString("events.autoFtop.title"))
                .setDescription("No F-Top factions.")
                .setFooter(Configuration.getString("events.autoFtop.footer"))
                .setThumbnail(GlobalConfig.EMBED_THUMBNAIL)
                .setTimestamp(ZonedDateTime.now())
                .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE));
        if (iterator == 1) return embed.build();
        embed = new EmbedBuilder()
                .setTitle(Configuration.getString("events.autoFtop.title"))
                .setDescription(totalList.toString())
                .setFooter(Configuration.getString("events.autoFtop.footer"))
                .setThumbnail(GlobalConfig.EMBED_THUMBNAIL)
                .setTimestamp(ZonedDateTime.now())
                .setColor(Color.decode(GlobalConfig.EMBED_COLOR_HEX_CODE));
        return embed.build();
    }

    public static String getDailyChange(Faction faction) {

        Long oldTotal = FactionTopData.get().getNearestTotal(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24L), faction);
        FactionValue factionValue = getData().get(faction.getId());
        String dailyPercentageChange = "[0.0%]";
        double dailyValueChange;
        if (oldTotal != null) {
            dailyValueChange = factionValue.getTotalSpawnerValue() - oldTotal;
            double percent = Math.round(dailyValueChange / ((oldTotal == 0.0) ? 1.0 : oldTotal) * 100.0 * 10.0) / 10.0;
            double clampedPercent = Math.max(-9999.0, Math.min(percent, 9999.0));
            String StringclampedPercent = String.valueOf(clampedPercent).replace(".0", "");
            if (dailyValueChange > 0) {
                StringclampedPercent = "+" + StringclampedPercent;
            }
            dailyPercentageChange = Txt.parse("[" + ((clampedPercent < percent) ? ">" : ((clampedPercent < percent) ? "<" : "")) + StringclampedPercent + "%]");
        }
        return dailyPercentageChange;
    }
}
