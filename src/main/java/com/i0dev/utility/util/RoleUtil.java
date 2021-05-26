package com.i0dev.utility.util;

import com.i0dev.object.objects.Type;
import com.i0dev.object.objects.RoleQueueObject;
import com.i0dev.utility.GlobalConfig;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class RoleUtil {

    public static int getUsersWhoNeedRole(Role role) {
        int count = 0;
        for (Member member : GlobalConfig.GENERAL_MAIN_GUILD.getMembers()) {
            if (!member.getRoles().contains(role)) count++;
        }
        return count;
    }

    public static List<User> getUserListWhoNeedRole(Role role) {
        List<User> list = new ArrayList<>();
        for (Member member : GlobalConfig.GENERAL_MAIN_GUILD.getMembers()) {
            if (!member.getRoles().contains(role)) list.add(member.getUser());
        }
        return list;
    }

    public static void giveRolesLongs(List<Long> roleIDS, Member member) {
        for (long roleID : roleIDS) {
            Role role = member.getGuild().getRoleById(roleID);
            if (member.getRoles().contains(role)) continue;
            new RoleQueueObject(member.getIdLong(), roleID, Type.ADD_ROLE).add();

        }
    }

    public static void giveRolesMemberLongs(Role role, List<Member> members) {
        for (Member member : members) {
            if (member.getRoles().contains(role)) continue;
            new RoleQueueObject(member.getIdLong(), role.getIdLong(), Type.ADD_ROLE).add();
        }
    }

    public static void giveRolesUsersLongs(Role role, List<User> members) {

        for (User member : members) {
            if (GlobalConfig.GENERAL_MAIN_GUILD.getMember(member).getRoles().contains(role)) continue;
            new RoleQueueObject(member.getIdLong(), role.getIdLong(), Type.ADD_ROLE).add();

        }
    }

    public static void giveRolesStrings(List<String> roleIDS, Member member) {
        for (String roleID : roleIDS) {
            Role role = member.getGuild().getRoleById(roleID);
            if (role == null) continue;
            if (member.getRoles().contains(role)) continue;
            new RoleQueueObject(member.getIdLong(), role.getIdLong(), Type.ADD_ROLE).add();
        }
    }

    public static void giveRolesRoles(List<Role> roles, Member member) {
        for (Role role : roles) {
            if (role == null) continue;
            if (member.getRoles().contains(role)) continue;
            new RoleQueueObject(member.getIdLong(), role.getIdLong(), Type.ADD_ROLE).add();
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
