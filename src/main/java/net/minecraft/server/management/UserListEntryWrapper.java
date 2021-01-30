package net.minecraft.server.management;

import javax.annotation.Nullable;

public class UserListEntryWrapper<T> {
    private final UserListEntry<T> wrappedValue;

    public UserListEntryWrapper(UserListEntry<T> wrappedValue) {
        this.wrappedValue = wrappedValue;
    }

    @Nullable
    public T getValue() {
        return wrappedValue.getValue();
    }
}
