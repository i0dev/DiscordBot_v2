package main.java.com.i0dev.utility.util;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public class RoleUtil {


    public static void giveRolesLongs(List<Long> roleIDS, Member member) {
        for (long roleID : roleIDS) {
            Role role = member.getGuild().getRoleById(roleID);
            if (role == null) continue;
            if (member.getRoles().contains(role)) continue;
            member.getGuild().addRoleToMember(member, role).queue();
        }
    }

    public static void giveRolesStrings(List<String> roleIDS, Member member) {
        for (String roleID : roleIDS) {
            Role role = member.getGuild().getRoleById(roleID);
            if (role == null) continue;
            if (member.getRoles().contains(role)) continue;
            member.getGuild().addRoleToMember(member, role).queue();
        }
    }

    public static void giveRolesRoles(List<Role> roles, Member member) {
        for (Role role : roles) {
            if (role == null) continue;
            if (member.getRoles().contains(role)) continue;
            member.getGuild().addRoleToMember(member, role).queue();
        }
    }

    public static void removeRolesLongs(List<Long> roleIDS, Member member) {
        for (long roleID : roleIDS) {
            Role role = member.getGuild().getRoleById(roleID);
            if (role == null) continue;
            if (!member.getRoles().contains(role)) continue;
            member.getGuild().removeRoleFromMember(member, role).queue();
        }
    }

    public static void removeRolesStrings(List<String> roleIDS, Member member) {
        for (String roleID : roleIDS) {
            Role role = member.getGuild().getRoleById(roleID);
            if (role == null) continue;
            if (!member.getRoles().contains(role)) continue;
            member.getGuild().removeRoleFromMember(member, role).queue();
        }
    }

    public static void removeRolesRoles(List<Role> roles, Member member) {
        for (Role role : roles) {
            if (role == null) continue;
            if (!member.getRoles().contains(role)) continue;
            member.getGuild().removeRoleFromMember(member, role).queue();
        }
    }


}
