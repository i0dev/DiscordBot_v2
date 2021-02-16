package com.i0dev.util;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Prettify {

    public static String capitalizeFirst(String a) {
        return a.substring(0, 1).toUpperCase() + a.substring(1).toLowerCase();

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

    public static String FormatListString(List<String> list) {

        StringBuilder sb = new StringBuilder();

        ArrayList<String> Stripped = new ArrayList<>();
        for (String s : list) {
            Stripped.add(capitalizeFirst(s));
        }
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

    public static String ticketRemainingArgFormatter(String[] message, int startPos) {
        StringBuilder sb = new StringBuilder();
        if (message == null) {
            sb.append("Have a nice day.");
            return sb.toString();
        }
        for (int i = startPos; i < message.length; i++) {
            sb.append(message[i]);
            if (message.length - 1 > i) {
                sb.append(" ");
            }
        }
        if (message.length == startPos) {
            sb.append("Have a nice day.");
        }
        return sb.toString();
    }
}
