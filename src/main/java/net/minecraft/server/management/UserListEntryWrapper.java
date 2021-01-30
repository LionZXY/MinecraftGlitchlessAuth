package net.minecraft.server.management;

public class UserListEntryWrapper<T> {
    private final UserListEntry<T> wrappedValue;

    public UserListEntryWrapper(UserListEntry<T> wrappedValue) {
        this.wrappedValue = wrappedValue;
    }

    public T getValue() {
        return getValue();
    }
}
