package ru.glitchless.auth.tasks;

import com.google.common.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;
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

        final PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();

        final String[] currentWhiteList = playerList.getWhitelistedPlayers().getKeys();
        final List<GameProfile> toRemove = new ArrayList<>();

        for (String name : currentWhiteList) {
            final GameProfile profile = onlineWhiteList.remove(name);
            if (profile == null) {
                toRemove.add(playerList.getWhitelistedPlayers().getByName(name));
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
                playerList.removePlayerFromWhitelist(toRemoveProfile);
                kickUser(toRemoveProfile.getId());
            }
            for (GameProfile toAddProfile : onlineWhiteList.values()) {
                playerList.addWhitelistedPlayer(toAddProfile);
            }
        });
    }

    private static void kickUser(UUID uuid) {
        final PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        final EntityPlayerMP playerMP = playerList.getPlayerByUUID(uuid);
        //noinspection ConstantConditions
        if (playerMP == null) {
            return;
        }
        final NetHandlerPlayServer connection = playerMP.connection;
        if (connection == null) {
            return;
        }
        connection.disconnect(new TextComponentString("Вы были исключены из белого списка"));
    }
}
