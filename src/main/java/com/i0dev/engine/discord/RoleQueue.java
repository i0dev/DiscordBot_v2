package com.i0dev.engine.discord;

import com.i0dev.utility.GlobalConfig;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.TimerTask;

public class RoleQueue {

    private static ArrayList<QueueObject> queue = new ArrayList<>();

    public static ArrayList<QueueObject> getQueue() {
        return queue;
    }

    public static void addToQueue(Long userID, Long roleID) {
        queue.add(new QueueObject(userID, roleID));

    }

    public static void addToQueue(User user, Role role) {
        addToQueue(user.getIdLong(), role.getIdLong());
    }

    public static void addToQueue(Member member, Role role) {
        addToQueue(member.getIdLong(), role.getIdLong());
    }

    public static void addToQueue(String userID, String roleID) {
        addToQueue(Long.valueOf(userID), Long.valueOf(roleID));
    }

    public static TimerTask applyRoles = new TimerTask() {
        public void run() {
            if (queue.isEmpty()) return;
            Guild guild = GlobalConfig.GENERAL_MAIN_GUILD;

            QueueObject queueObject = queue.get(0);

            User user = guild.getJDA().getUserById(queueObject.getUser());
            Role role = guild.getRoleById(queueObject.getRole());

            Member member = guild.getMemberById(user.getId());

            if (role != null && member != null) {
                guild.addRoleToMember(user.getId(), role).queue();
                System.out.println("[LOG] Applied the role {" + role.getName() + "} to the user: {" + member.getEffectiveName() + "}");
            }

            queue.remove(0);
        }
    };
}

class QueueObject {

    Long userID;
    Long roleID;

    QueueObject(Long userID, Long roleID) {
        this.userID = userID;
        this.roleID = roleID;
    }

    public Long getRole() {
        return roleID;
    }

    public Long getUser() {
        return userID;
    }
}