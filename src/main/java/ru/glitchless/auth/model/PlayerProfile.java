package ru.glitchless.auth.model;

import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;

public class PlayerProfile {
    @SerializedName("simulated_id")
    private String uuid;
    @SerializedName("nickname")
    private String nickname;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
