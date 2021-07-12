package ru.glitchless.auth.utils;

import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReplaceServerHelper {
    private static final String BASE_URL = "https://games.glitchless.ru/api/minecraft/users/";

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    public void replaceAll() throws Exception {
        replaceSession();
        replaceAuthServer();
    }

    private void replaceSession() throws Exception {
        Field baseUrl = YggdrasilMinecraftSessionService.class.getDeclaredField("BASE_URL");
        Field joinUrl = YggdrasilMinecraftSessionService.class.getDeclaredField("JOIN_URL");
        Field checkUrl = YggdrasilMinecraftSessionService.class.getDeclaredField("CHECK_URL");
        baseUrl.setAccessible(true);
        joinUrl.setAccessible(true);
        checkUrl.setAccessible(true);
        setFinalStatic(baseUrl, BASE_URL);
        setFinalStatic(joinUrl, HttpAuthenticationService.constantURL(BASE_URL + "join"));
        setFinalStatic(checkUrl, HttpAuthenticationService.constantURL(BASE_URL + "has_joined"));
    }

    private void replaceAuthServer() throws Exception {
        Field baseUrl = YggdrasilUserAuthentication.class.getDeclaredField("BASE_URL");
        Field routeAuthenticate = YggdrasilUserAuthentication.class.getDeclaredField("ROUTE_AUTHENTICATE");
        Field routeRefresh = YggdrasilUserAuthentication.class.getDeclaredField("ROUTE_REFRESH");
        Field routeValidate = YggdrasilUserAuthentication.class.getDeclaredField("ROUTE_VALIDATE");
        Field routeInvalidate = YggdrasilUserAuthentication.class.getDeclaredField("ROUTE_INVALIDATE");
        Field routeSignout = YggdrasilUserAuthentication.class.getDeclaredField("ROUTE_SIGNOUT");
        baseUrl.setAccessible(true);
        routeAuthenticate.setAccessible(true);
        routeRefresh.setAccessible(true);
        routeValidate.setAccessible(true);
        routeInvalidate.setAccessible(true);
        routeSignout.setAccessible(true);

        setFinalStatic(baseUrl, BASE_URL);
        setFinalStatic(routeAuthenticate, HttpAuthenticationService.constantURL(BASE_URL + "authenticate"));
        setFinalStatic(routeRefresh, HttpAuthenticationService.constantURL(BASE_URL + "refresh"));
        setFinalStatic(routeValidate, HttpAuthenticationService.constantURL(BASE_URL + "validate"));
        setFinalStatic(routeInvalidate, HttpAuthenticationService.constantURL(BASE_URL + "invalidate"));
        setFinalStatic(routeSignout, HttpAuthenticationService.constantURL(BASE_URL + "signout"));
    }
}
