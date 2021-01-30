package ru.glitchless.auth.config;

import com.feed_the_beast.mods.ftbranks.api.FTBRanksAPI;
import com.feed_the_beast.mods.ftbranks.api.Rank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class RankMap {
    private static Map<Integer, String> idToRank = new HashMap<>();
    private static int lastCacheInvalidateHash = 0;

    @Nullable
    public static synchronized Optional<Rank> getRankById(Integer id) {
        invalidateIfNeed();
        final String name = idToRank.get(id);

        return FTBRanksAPI.INSTANCE.getManager().getRank(name);
    }

    @Nonnull
    public static synchronized List<Rank> getRanksByIds(List<Integer> ids) {
        invalidateIfNeed();
        final List<Rank> toExit = new ArrayList<>();
        for (Integer id : ids) {
            final Optional<Rank> rank = FTBRanksAPI.INSTANCE.getManager().getRank(idToRank.get(id));
            rank.ifPresent(toExit::add);
        }
        return toExit;
    }

    private static void invalidateIfNeed() {
        int newHash = GlitchlessGroupConfig.id_to_rank.hashCode();
        if (lastCacheInvalidateHash == newHash) {
            return;
        }

        idToRank.clear();
        for (String element : GlitchlessGroupConfig.id_to_rank) {
            final String[] idRankSplit = element.split(":");
            idToRank.put(Integer.parseInt(idRankSplit[0]), idRankSplit[1]);
        }
        lastCacheInvalidateHash = newHash;
    }
}
