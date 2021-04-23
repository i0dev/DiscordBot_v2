package com.i0dev.engine.discord;

import com.i0dev.utility.GlobalConfig;
import lombok.Getter;
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

    public static void addToQueue(Long userID, Long roleID, Type type) {
        queue.add(new QueueObject(userID, roleID, type));

    }

    public static void addToQueue(User user, Role role, Type type) {
        try {
            addToQueue(user.getIdLong(), role.getIdLong(), type);
        } catch (Exception ignored) {
        }
    }

    public static void addToQueue(Member member, Role role, Type type) {
        addToQueue(member.getIdLong(), role.getIdLong(), type);
    }

    public static TimerTask applyRoles = new TimerTask() {
        public void run() {
            if (queue.isEmpty()) return;
            Guild guild = GlobalConfig.GENERAL_MAIN_GUILD;

            QueueObject queueObject = queue.get(0);

            User user = guild.getJDA().getUserById(queueObject.getUserID());
            Role role = guild.getRoleById(queueObject.getRoleID());

            if (user == null || role == null) {
                queue.remove(0);
            }

            Member member = guild.getMemberById(user.getId());

            if (queueObject.getType().equals(Type.ADD_ROLE)) {
                if (role != null && member != null) {
                    guild.addRoleToMember(user.getId(), role).queue();
                    System.out.println("[LOG] Applied the role {" + role.getName() + "} to the user: {" + member.getEffectiveName() + "}");
                }
            } else if (queueObject.getType().equals(Type.REMOVE_ROLE)) {
                guild.removeRoleFromMember(user.getId(), role).queue();
                System.out.println("[LOG] Removed the role {" + role.getName() + "} to the user: {" + member.getEffectiveName() + "}");
            }

            queue.remove(0);
        }
    };
}

@Getter
class QueueObject {

    Long userID;
    Long roleID;
    Type type;

    QueueObject(Long userID, Long roleID, Type type) {
        this.userID = userID;
        this.roleID = roleID;
        this.type = type;
    }

}