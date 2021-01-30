package ru.glitchless.auth.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class GlitchlessGroupConfig {
    public static GlitchlessGroupConfig INSTANCE;
    public static ForgeConfigSpec INSTANCE_SPEC;
    public final ForgeConfigSpec.ConfigValue<List<String>> idToRank;
    public final ForgeConfigSpec.ConfigValue<String> serverToken;

    public GlitchlessGroupConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("general");
        idToRank = builder
                .comment("mapping id to rank name in format 1:admin")
                .define("id_to_rank", Arrays.asList("1:admin", "2:technopark", "3:zbt"));
        serverToken = builder
                .comment("Secret config for server")
                .define("server_token", "SECRET");
        builder.pop();
    }

    static {
        {
            final Pair<GlitchlessGroupConfig, ForgeConfigSpec> specPair
                    = new ForgeConfigSpec.Builder().configure(GlitchlessGroupConfig::new);
            INSTANCE = specPair.getLeft();
            INSTANCE_SPEC = specPair.getRight();
        }
    }
}
