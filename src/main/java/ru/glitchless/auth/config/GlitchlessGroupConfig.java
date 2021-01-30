package ru.glitchless.auth.config;

import ru.lionzxy.stopchangefuckingconfig.Comment;

import java.util.Arrays;
import java.util.List;

public class GlitchlessGroupConfig {
    @Comment("mapping id to rank name in format 1:admin")
    public static List<String> id_to_rank = Arrays.asList("1:admin", "2:technopark", "3:zbt");
    @Comment("Secret config for server")
    public static String server_token = "SECRET";
}
