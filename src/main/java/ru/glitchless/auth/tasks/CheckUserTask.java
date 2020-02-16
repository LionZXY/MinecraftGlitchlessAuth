package ru.glitchless.auth.tasks;

import com.feed_the_beast.ftbutilities.ranks.PlayerRank;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.command.TextComponentHelper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.glitchless.auth.GlitchlessAuth;
import ru.glitchless.auth.config.GlitchlessGroupConfig;
import ru.glitchless.auth.config.RankMap;
import ru.glitchless.auth.model.ApiResponse;
import ru.glitchless.auth.model.UserProfile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class CheckUserTask implements Runnable {
    @SuppressWarnings("UnstableApiUsage")
    private static final Type PROFILE_TYPE = new TypeToken<ApiResponse<UserProfile>>() {
    }.getType();
    private static final String BASE_URL = "https://games.glitchless.ru/api/minecraft/users/profile/?token=%s&nickname=%s";
    private final GameProfile profile;
    private final EntityPlayer player;

    public CheckUserTask(EntityPlayer player) {
        this.profile = player.getGameProfile();
        this.player = player;
    }

    @Override
    public void run() {
        final Request request = new Request.Builder()
                .url(String.format(BASE_URL, GlitchlessGroupConfig.server_token, profile.getName()))
                .build();
        ApiResponse<UserProfile> result;
        try {
            result = GlitchlessAuth.getGson().fromJson(makeRequest(request), PROFILE_TYPE);
        } catch (Exception ex) {
            GlitchlessAuth.getLogger().error("Error getting " + request.url(), ex);
            return;
        }
        final List<Integer> groups = result.getData().getGroups();
        if (groups == null || groups.isEmpty()) {
            return;
        }
        final List<Rank> rankList = RankMap.getRanksByIds(groups);
        if (rankList.isEmpty()) {
            GlitchlessAuth.getLogger().error("Not found rank for " + Arrays.toString(groups.toArray()));
            return;
        }
        final PlayerRank playerRank = Ranks.INSTANCE.getPlayerRank(profile);

        final StringBuilder sb = new StringBuilder();
        playerRank.clearParents();

        playerRank.addParent(rankList.get(0));
        sb.append(rankList.get(0));
        for (int i = 1; i < rankList.size(); i++) {
            sb.append(", ");
            final Rank plrRank = rankList.get(i);
            playerRank.addParent(plrRank);
            sb.append(plrRank.getId());
        }

        player.sendMessage(new TextComponentString(String.format("Ваши текущие группы: %s", sb.toString())));
        Ranks.INSTANCE.save();

        GlitchlessAuth.getLogger().info("Add to user " + profile.getName() + " " + sb);
    }

    private String makeRequest(Request request) throws IOException {
        try (Response response = GlitchlessAuth.getClient().newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
