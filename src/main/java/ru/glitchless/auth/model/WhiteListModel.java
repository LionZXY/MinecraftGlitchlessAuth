package ru.glitchless.auth.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WhiteListModel {
    @SerializedName("players")
    private List<PlayerProfile> players;

    public List<PlayerProfile> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerProfile> players) {
        this.players = players;
    }
}
