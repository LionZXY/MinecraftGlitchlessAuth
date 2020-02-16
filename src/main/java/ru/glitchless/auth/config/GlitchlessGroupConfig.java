package ru.glitchless.auth.config;

import net.minecraftforge.common.config.Config;
import ru.glitchless.auth.GlitchlessAuth;

@Config(modid = GlitchlessAuth.MODID)
public class GlitchlessGroupConfig {
    @Config.Comment("mapping id to rank name in format 1:admin")
    public static String[] id_to_rank = new String[]{"1:admin", "2:technopark", "3:zbt"};
    @Config.Comment("Secret config for server")
    public static String server_token = "SECRET";
}
