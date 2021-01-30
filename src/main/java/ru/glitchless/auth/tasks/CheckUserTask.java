package ru.glitchless.auth.tasks;

import com.feed_the_beast.mods.ftbranks.api.FTBRanksAPI;
import com.feed_the_beast.mods.ftbranks.api.Rank;
import com.google.common.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import okhttp3.Request;
import okhttp3.Response;
import ru.glitchless.auth.GlitchlessAuth;
import ru.glitchless.auth.config.GlitchlessGroupConfig;
import ru.glitchless.auth.config.RankMap;
import ru.glitchless.auth.model.ApiResponse;
import ru.glitchless.auth.model.UserProfile;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CheckUserTask implements Runnable {
    @SuppressWarnings("UnstableApiUsage")
    private static final Type PROFILE_TYPE = new TypeToken<ApiResponse<UserProfile>>() {
    }.getType();
    private static final String BASE_URL = "https://games.glitchless.ru/api/minecraft/users/profile/?token=%s&nickname=%s";
    private final GameProfile profile;
    private final ServerPlayerEntity player;

    public CheckUserTask(ServerPlayerEntity player) {
        this.profile = player.getGameProfile();
        this.player = player;
    }

    @Override
    public void run() {
        final Request request = new Request.Builder()
                .url(String.format(
                        BASE_URL,
                        GlitchlessGroupConfig.INSTANCE.serverToken.get(),
                        profile.getName()
                )).build();
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

        // Sync rank
        final StringBuilder sb = new StringBuilder();
        List<Rank> ranks = FTBRanksAPI.INSTANCE.getManager().getRanks(player);
        for (Rank rank : ranks) {
            rank.remove(profile);
        }
        rankList.get(0).add(profile);
        sb.append(rankList.get(0));
        for (int i = 1; i < rankList.size(); i++) {
            sb.append(", ");
            final Rank plrRank = rankList.get(i);
            plrRank.add(profile);
            sb.append(plrRank.getId());
        }

        player.sendMessage(new StringTextComponent(String.format("Ваши текущие группы: %s", sb.toString())),
                player.getUniqueID());
        FTBRanksAPI.INSTANCE.getManager().saveRanks();
        FTBRanksAPI.INSTANCE.getManager().savePlayers();

        GlitchlessAuth.getLogger().info("Add to user " + profile.getName() + " " + sb);
    }

    private String makeRequest(Request request) throws IOException {
        try (Response response = GlitchlessAuth.getClient().newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
