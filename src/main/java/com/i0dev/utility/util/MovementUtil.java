package com.i0dev.utility.util;

import com.i0dev.object.objects.RoleQueueObject;
import com.i0dev.object.objects.Type;
import com.i0dev.utility.Configuration;
import com.i0dev.utility.InternalJDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovementUtil {

    private static final List<JSONObject> Tracks = Configuration.getObjectList("movementTracks");

    public static Role getParentStaff(Member member) {
        for (JSONObject object : Tracks) {
            long mainRoleID = (long) object.get("mainRole");

            Role mainRole = InternalJDA.getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;
            if (member.getRoles().contains(mainRole)) {
                return mainRole;
            }
        }
        return null;
    }

    public static void giveNewRoles(Member member, long mainRoleID) {
        for (JSONObject object : Tracks) {
            if (!object.get("mainRole").equals(mainRoleID)) continue;
            List<Long> RoleIDS = (ArrayList<Long>) object.get("extraRoles");
            RoleIDS.add(mainRoleID);
            for (long roleToGiveID : RoleIDS) {
                Role roleToGive = InternalJDA.getJda().getRoleById(roleToGiveID);
                if (roleToGive == null) continue;
                new RoleQueueObject(member.getIdLong(), roleToGive.getIdLong(), Type.ADD_ROLE).add();
            }
        }
    }

    public static List<JSONObject> getTracks() {
        return Tracks;

    }

    public static void removeOldRoles(Member member, long oldMainRoleID) {
        for (JSONObject object : Tracks) {
            if (!object.get("mainRole").equals(oldMainRoleID)) continue;
            List<Long> RoleIDS = (ArrayList<Long>) object.get("extraRoles");
            RoleIDS.add(oldMainRoleID);
            for (long roleToGiveID : RoleIDS) {
                Role roleToGive = InternalJDA.getJda().getRoleById(roleToGiveID);
                if (roleToGive == null) continue;
                new RoleQueueObject(member.getIdLong(), roleToGive.getIdLong(), Type.REMOVE_ROLE).add();
            }
        }
    }

    public static JSONObject getObject(Role role) {
        for (JSONObject object : Tracks) {
            if (((long) object.get("mainRole")) == role.getIdLong()) {
                return object;
            }
        }
        return null;
    }

    public static boolean isAlreadyStaff(Member member) {
        for (JSONObject object : Tracks) {
            long mainRoleID = (long) object.get("mainRole");
            Role mainRole = InternalJDA.getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;
            if (member.getRoles().contains(mainRole)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHighestStaff(Member member) {
        long topRoleID = (long) Tracks.get(Tracks.size() - 1).get("mainRole");
        Role topRole = InternalJDA.getJda().getRoleById(topRoleID);
        if (topRole == null) return false;
        if (member.getRoles().contains(topRole)) {
            return true;
        }
        return false;
    }

    public static boolean isLowestStaff(Member member) {
        long lowestRoleID = (long) Tracks.get(0).get("mainRole");
        Role lowestRole = InternalJDA.getJda().getRoleById(lowestRoleID);
        if (lowestRole == null) return false;
        if (member.getRoles().contains(lowestRole)) {
            return true;
        }
        return false;
    }


    public static Role getNextRole(Role role) {

        for (int i = 0; i < Tracks.size(); i++) {
            long mainRoleID = (long) Tracks.get(i).get("mainRole");
            Role mainRole = InternalJDA.getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;

            if (mainRole == role) {

                try {
                    Tracks.get(i + 1).get("mainRole");
                } catch (Exception ignored) {
                    return null;
                }

                long nextTrackID = (long) Tracks.get(i + 1).get("mainRole");

                Role nextRole = InternalJDA.getJda().getRoleById(nextTrackID);
                if (nextRole == null) continue;

                return nextRole;
            }
        }
        return null;
    }

    public static Role getPreviousRole(Role role) {

        for (int i = 0; i < Tracks.size(); i++) {
            long mainRoleID = (long) Tracks.get(i).get("mainRole");
            Role mainRole = InternalJDA.getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;

            if (mainRole == role) {

                try {
                    Tracks.get(i - 1).get("mainRole");
                } catch (Exception ignored) {
                    return null;
                }

                long nextTrackID = (long) Tracks.get(i - 1).get("mainRole");

                Role nextRole = InternalJDA.getJda().getRoleById(nextTrackID);
                if (nextRole == null) continue;

                return nextRole;
            }
        }
        return null;
    }

    public static JSONObject getNextRoleObject(Role role) {
        for (int i = 0; i < Tracks.size(); i++) {
            long mainRoleID = (long) Tracks.get(i).get("mainRole");
            Role mainRole = InternalJDA.getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;
            if (mainRole == role) {
                try {
                    Tracks.get(i + 1).get("mainRole");
                } catch (Exception ignored) {
                    return null;
                }
                long nextTrackID = (long) Tracks.get(i + 1).get("mainRole");

                Role nextRole = InternalJDA.getJda().getRoleById(nextTrackID);
                if (nextRole == null) continue;
                return getObject(nextRole);
            }
        }
        return null;
    }

    public static JSONObject getPreviousRoleObject(Role role) {
        for (int i = 0; i < Tracks.size(); i++) {
            long mainRoleID = (long) Tracks.get(i).get("mainRole");
            Role mainRole = InternalJDA.getJda().getRoleById(mainRoleID);
            if (mainRole == null) continue;
            if (mainRole == role) {
                try {
                    Tracks.get(i - 1).get("mainRole");
                } catch (Exception ignored) {
                    return null;
                }
                long nextTrackID = (long) Tracks.get(i - 1).get("mainRole");

                Role nextRole = InternalJDA.getJda().getRoleById(nextTrackID);
                if (nextRole == null) continue;
                return getObject(nextRole);
            }
        }
        return null;
    }
}
