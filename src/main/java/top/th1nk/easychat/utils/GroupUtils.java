package top.th1nk.easychat.utils;

public class GroupUtils {
    public static boolean isValidGroupName(String groupName) {
        return groupName != null && !groupName.trim().isEmpty() && groupName.length() <= 20;
    }

    public static boolean isValidDescription(String description) {
        return description != null && description.length() <= 150;
    }
}
