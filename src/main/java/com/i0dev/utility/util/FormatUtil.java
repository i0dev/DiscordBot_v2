package com.i0dev.utility.util;

import com.i0dev.utility.Configuration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang.RandomStringUtils;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class FormatUtil {

    public static String capitalizeFirst(String a) {
        return a.substring(0, 1).toUpperCase() + a.substring(1).toLowerCase();

    }

    public static String formatNumber(int num) {
        String Formatted = "Extra, Beyond set formatting";

        switch (num) {
            case 1:
                Formatted = "First";
                break;
            case 2:
                Formatted = "Second";
                break;
            case 3:
                Formatted = "Third";
                break;
            case 4:
                Formatted = "Fourth";
                break;
            case 5:
                Formatted = "Fifth";
                break;
            case 6:
                Formatted = "Sixth";
                break;
            case 7:
                Formatted = "Seventh";
                break;
            case 8:
                Formatted = "Eighth";
                break;
            case 9:
                Formatted = "Ninth";
                break;
            case 10:
                Formatted = "Tenth";
                break;
            case 11:
                Formatted = "Eleventh";
                break;
            case 12:
                Formatted = "Twelfth";
                break;
            case 13:
                Formatted = "Thirteenth";
                break;
            case 14:
                Formatted = "Fourteenth";
                break;
            case 15:
                Formatted = "Fifteenth";
                break;
        }
        return Formatted;
    }

    public static String FormatList(List<Role> list) {

        StringBuilder sb = new StringBuilder();

        ArrayList<String> Stripped = new ArrayList<>();
        for (Role s : list) {
            Stripped.add(capitalizeFirst(s.getAsMention()));
        }
        for (int i = 0; i < Stripped.size(); i++) {
            sb.append(Stripped.get(i));
            if (Stripped.size() - 1 > i) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String FormatListUser(List<User> list) {

        StringBuilder sb = new StringBuilder();

        ArrayList<String> Stripped = new ArrayList<>();
        for (User s : list) {
            Stripped.add(capitalizeFirst(s.getAsMention()));
        }
        for (int i = 0; i < Stripped.size(); i++) {
            sb.append(Stripped.get(i));
            if (Stripped.size() - 1 > i) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String FormatDoubleListUser(List<User> list) {

        StringBuilder sb = new StringBuilder();

        ArrayList<String> Stripped = new ArrayList<>();
        for (User s : list) {
            Stripped.add(capitalizeFirst(s.getAsMention()) + "`(" + s.getAsTag() + ")`");
        }
        for (int i = 0; i < Stripped.size(); i++) {
            sb.append(Stripped.get(i));
            if (Stripped.size() - 1 > i) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String FormatListString(List<String> list) {

        StringBuilder sb = new StringBuilder();

        ArrayList<String> Stripped = new ArrayList<>(list);
        for (int i = 0; i < Stripped.size(); i++) {
            sb.append(Stripped.get(i));
            if (Stripped.size() - 1 > i) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static String FormatList(EnumSet<Permission> list) {

        StringBuilder sb = new StringBuilder();

        ArrayList<String> Stripped = new ArrayList<>();
        for (Permission s : list) {
            Stripped.add(capitalizeFirst(s.getName()));
        }
        for (int i = 0; i < Stripped.size(); i++) {
            sb.append(Stripped.get(i));
            if (Stripped.size() - 1 > i) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String remainingArgFormatter(String[] message, int startPos) {
        StringBuilder sb = new StringBuilder();
        if (message == null) {
            sb.append("Nothing Provided");
            return sb.toString();
        }
        for (int i = startPos; i < message.length; i++) {
            sb.append(message[i]);
            if (message.length - 1 > i) {
                sb.append(" ");
            }
        }
        if (message.length == startPos) {
            sb.append("Nothing Provided");
        }
        return sb.toString();
    }

    public static ZonedDateTime getZonedDateTimeFromString(String s) {
        return ZonedDateTime.parse(s);
    }


    public static String ticketRemainingArgFormatter(String[] message, int startPos) {
        StringBuilder sb = new StringBuilder();
        if (message == null) {
            sb.append(Configuration.getString("commands.ticketClose.defaultCloseReason"));
            return sb.toString();
        }
        for (int i = startPos; i < message.length; i++) {
            sb.append(message[i]);
            if (message.length - 1 > i) {
                sb.append(" ");
            }
        }
        if (message.length == startPos) {
            sb.append(Configuration.getString("commands.ticketClose.defaultCloseReason"));
        }
        return sb.toString();
    }

    public static int getTimeMilis(String input) {
        input = input.toLowerCase();
        if (input.isEmpty()) return -1;
        int time = 0;
        StringBuilder number = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (isInt(String.valueOf(c))) {
                number.append(c);
                continue;
            }
            if (number.toString().isEmpty()) return -1;
            int add = Integer.parseInt(number.toString());
            switch (c) {
                case 'w':
                    add *= 7;
                case 'd':
                    add *= 24;
                case 'h':
                    add *= 60;
                case 'm':
                    add *= 60;
                case 's':
                    time += add;
                    number.setLength(0);
                    break;
                default:
                    return -1;
            }
        }
        return time * 1000;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String formatDate(ZonedDateTime time) {
        String Month = time.getMonth().getValue() + "";
        String Day = time.getDayOfMonth() + "";
        String Year = time.getYear() + "";
        String Hour = time.getHour() + "";
        String Minute = time.getMinute() + "";
        String Second = time.getSecond() + "";

        return "[" + Month + "/" + Day + "/" + Year + " " + Hour + ":" + Minute + ":" + Second + "]";
    }

    public static String formatDate(Long instant) {
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(instant), ZoneId.of("America/New_York"));
        String Month = time.getMonth().getValue() + "";
        String Day = time.getDayOfMonth() + "";
        String Year = time.getYear() + "";
        String Hour = time.getHour() + "";
        String Minute = time.getMinute() + "";
        String Second = time.getSecond() + "";

        return "[" + Month + "/" + Day + "/" + Year + " " + Hour + ":" + Minute + ":" + Second + "]";
    }

    public static String GenerateRandomString() {
        return RandomStringUtils.random(10, true, true);
    }

    public static String GenerateRandomString(int length) {
        return RandomStringUtils.random(length, true, true);
    }

    public static String c(String s) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String formatValueSuffix(long number) {
        String[] valueFormatSuffix = new String[]{"", "K", "M", "B", "T"};
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", valueFormatSuffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        int MAX_LENGTH = 6;
        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }
        return r;
    }

    public static String getFactionName(DPlayer dPlayer) {
        String uid = dPlayer.getLinkInfo().getMinecraftUUID();
        UUID uuid = uid.equals("") ? null : UUID.fromString(uid);
        if (uuid == null) return "";
        org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(uuid);
        com.massivecraft.factions.entity.Faction f = com.massivecraft.factions.entity.MPlayer.get(p).getFaction();
        return f.isNone() ? "Wilderness" : f.getName();
    }

    public static String getPrefix(DPlayer dPlayer) {
        UUID uuid = dPlayer.getLinkInfo().getMinecraftUUID().equals("") ? null : UUID.fromString(dPlayer.getLinkInfo().getMinecraftUUID());
        if (uuid == null) return "";
        org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(uuid);
        com.massivecraft.factions.entity.Faction f = com.massivecraft.factions.entity.MPlayer.get(p).getFaction();
        return f.isNone() ? "" : com.massivecraft.factions.entity.MPlayer.get(p).getRole().getPrefix();
    }

    public static MessageEmbed getEmbedFromEncode(String string) {
        EmbedBuilder eb = new EmbedBuilder();
        if (!string.startsWith("_embed")) return null;
        string = string.replace("#channel ", "");
        string = string.replace("_embed ", "");
        String[] partsArray = string.split("\\|");
        String[] news = new String[partsArray.length];
        for (int i = 0; i < news.length; i++) {
            news[i] = partsArray[i].trim();
        }
        partsArray = news;
        List<String> parts = Arrays.stream(partsArray).collect(Collectors.toList());
        if (partContain("description=", parts)) {
            eb.setDescription(getRemain("description=", parts));
        }
        if (partContain("title=", parts)) {
            eb.setTitle(getRemain("title=", parts));
        }
        if (partContain("author=", parts)) {
            String part = getPart("author=", parts);
            int indexAuthor = part.indexOf("name=");
            int indexIcon = part.indexOf("icon=");
            int indexUrl = part.indexOf("url=");
            String name = !part.contains("name=") ? null : part.substring(indexAuthor, part.contains("icon=") ? indexIcon : (part.contains("url=") ? indexUrl : part.length()));
            String icon = !part.contains("icon=") ? null : part.substring(indexIcon, indexUrl != -1 ? indexUrl : part.length() - 1);
            String url = !part.contains("url=") ? null : part.substring(indexUrl);
            eb.setAuthor(name != null ? name.replaceFirst("name=", "") : null, url != null ? url.replaceAll("url=", "") : null, icon != null ? icon.replaceFirst("icon=", "") : null);
        }
        if (partContain("thumbnail=", parts)) {
            eb.setThumbnail(getRemain("thumbnail=", parts));
        }
        if (partContain("image=", parts)) {
            eb.setImage(getRemain("image=", parts));
        }
        if (partContain("footer=", parts)) {
            String part = getPart("footer=", parts);
            if (part.contains("icon=")) {
                int indexAuthor = part.indexOf("name=");
                int indexIcon = part.indexOf("icon=");
                String name = !part.contains("name=") ? null : part.substring(indexAuthor, part.contains("icon=") ? indexIcon : part.length());
                String icon = !part.contains("icon=") ? null : part.substring(indexIcon);
                eb.setFooter(name != null ? name.replaceFirst("name=", "") : null, icon != null ? icon.replaceFirst("icon=", "") : null);
            } else {
                eb.setFooter(getRemain("footer=", parts));

            }
        }
        if (partContain("color=", parts)) {
            eb.setColor(Color.decode(getRemain("color=", parts)));
        }

        for (String part : getParts("field=", parts)) {
            int indexAuthor = part.indexOf("name=");
            int indexIcon = part.indexOf("value=");
            int indexUrl = part.indexOf("inline=");
            String name = !part.contains("name=") ? null : part.substring(indexAuthor, part.contains("value=") ? indexIcon : (part.contains("inline=") ? indexUrl : part.length()));
            String icon = !part.contains("value=") ? null : part.substring(indexIcon, indexUrl != -1 ? indexUrl : part.length() - 1).equals("") ? "" : part.substring(indexIcon, indexUrl != -1 ? indexUrl : part.length() - 1);
            boolean inline = !part.contains("inline=");
            eb.addField(name != null ? name.replaceFirst("name=", "") : "", icon != null ? icon.replaceAll("value=", "") : "", inline);
        }
        return eb.build();
    }

    public static boolean partContain(String s, List<String> ls) {
        for (String l : ls) {
            if (l.startsWith(s)) return true;
        }
        return false;
    }

    public static String getPart(String s, List<String> ls) {
        for (String l : ls) {
            if (l.startsWith(s)) return l;
        }
        return "";
    }

    public static List<String> getParts(String s, List<String> ls) {
        List<String> ret = new ArrayList<>();
        for (String l : ls) {
            if (l.startsWith(s)) {
                ret.add(l);
            }
        }
        return ret;
    }

    public static String getRemain(String s, List<String> ls) {
        for (String l : ls) {
            if (l.startsWith(s)) {
                return l.substring(s.length());
            }
        }
        return "";
    }


}
