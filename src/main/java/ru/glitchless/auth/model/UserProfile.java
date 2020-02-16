package ru.glitchless.auth.model;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;
import java.util.List;

public class UserProfile {
    @SerializedName("groups")
    private List<Integer> groups;

    public List<Integer> getGroups() {
        return groups;
    }

    public void setGroups(@Nullable List<Integer> groups) {
        this.groups = groups;
    }
}
