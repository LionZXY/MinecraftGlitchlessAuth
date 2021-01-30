package ru.glitchless.auth.tasks;

import com.google.common.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.UserListEntryWrapper;
import net.minecraft.server.management.WhitelistEntry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import okhttp3.Request;
import okhttp3.Response;
import ru.glitchless.auth.GlitchlessAuth;
import ru.glitchless.auth.config.GlitchlessGroupConfig;
import ru.glitchless.auth.model.ApiResponse;
import ru.glitchless.auth.model.PlayerProfile;
import ru.glitchless.auth.model.WhiteListModel;
import ru.glitchless.auth.utils.UUIDUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class UpdateWhiteListTask implements Runnable {
    private static final String BASE_URL = "https://games.glitchless.ru/api/minecraft/servers/whitelist?token=%s";
    private static final Type WHITELIST_TYPE = new TypeToken<ApiResponse<WhiteListModel>>() {
    }.getType();

    @Override
    public void run() {
        GlitchlessAuth.getLogger().info("Run UpdateWhiteListTask");
        syncWhiteList();
    }

    private static synchronized void syncWhiteList() {
        final String url = String.format(BASE_URL, GlitchlessGroupConfig.server_token);
        final Request request = new Request.Builder().url(url).build();
        final ApiResponse<WhiteListModel> whiteListModel;
        try (Response response = GlitchlessAuth.getClient().newCall(request).execute()) {
            whiteListModel = GlitchlessAuth.getGson().fromJson(response.body().string(), WHITELIST_TYPE);
        } catch (IOException ex) {
            GlitchlessAuth.getLogger().error("Error getting " + request.url(), ex);
            return;
        }
        final Map<String, GameProfile> onlineWhiteList = new HashMap<>();
        for (PlayerProfile playerProfile : whiteListModel.getData().getPlayers()) {
            onlineWhiteList.put(playerProfile.getNickname(),
                    new GameProfile(UUIDUtils.formatFromInput(playerProfile.getUuid()), playerProfile.getNickname()));
        }


        final PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();

        final List<WhitelistEntry> currentWhiteList = new ArrayList<>(playerList.getWhitelistedPlayers().getEntries());
        final List<GameProfile> toRemove = new ArrayList<>();

        for (WhitelistEntry entry : currentWhiteList) {
            final GameProfile profile = new UserListEntryWrapper<>(entry).getValue();
            final GameProfile thisUserExistInNewWL = onlineWhiteList.remove(profile.getName());
            if (thisUserExistInNewWL == null) {
                toRemove.add(profile);
            }
        }

        if (onlineWhiteList.isEmpty() && toRemove.isEmpty()) {
            return;
        }

        GlitchlessAuth.getLogger().info(String.format("Find to remove %s users and %s to add (whitelist)",
                onlineWhiteList.size(), toRemove.size()));

        GlitchlessAuth.getInstance().getMainLooper().handle(() -> {
            GlitchlessAuth.getLogger().info(String.format("Remove %s users and %s add to whitelist",
                    onlineWhiteList.size(), toRemove.size()));
            for (GameProfile toRemoveProfile : toRemove) {
                playerList.getWhitelistedPlayers().removeEntry(toRemoveProfile);
                kickUser(toRemoveProfile.getId());
            }
            for (GameProfile toAddProfile : onlineWhiteList.values()) {
                playerList.getWhitelistedPlayers().addEntry(new WhitelistEntry(toAddProfile));
            }
        });
    }

    private static void kickUser(UUID uuid) {
        final PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        final ServerPlayerEntity playerMP = playerList.getPlayerByUUID(uuid);
        //noinspection ConstantConditions
        if (playerMP == null) {
            return;
        }
        final ServerPlayNetHandler connection = playerMP.connection;
        if (connection == null) {
            return;
        }
        connection.disconnect(new StringTextComponent("Вы были исключены из белого списка"));
    }
}
