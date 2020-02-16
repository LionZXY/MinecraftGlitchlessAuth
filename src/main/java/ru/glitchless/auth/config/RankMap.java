package ru.glitchless.auth.config;

import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.feed_the_beast.ftbutilities.ranks.Ranks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class RankMap {
    private static Map<Integer, String> idToRank = new HashMap<>();
    private static int lastCacheInvalidateHash = 0;

    @Nullable
    public static synchronized Rank getRankById(Integer id) {
        invalidateIfNeed();
        final String name = idToRank.get(id);
        return Ranks.INSTANCE.getRank(name);
    }

    @Nonnull
    public static synchronized List<Rank> getRanksByIds(List<Integer> ids) {
        invalidateIfNeed();
        final List<Rank> toExit = new ArrayList<>();
        for (Integer id : ids) {
            final Rank rank = Ranks.INSTANCE.getRank(idToRank.get(id));
            if (rank != null) {
                toExit.add(rank);
            }
        }
        return toExit;
    }

    private static void invalidateIfNeed() {
        int newHash = Arrays.hashCode(GlitchlessGroupConfig.id_to_rank);
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
