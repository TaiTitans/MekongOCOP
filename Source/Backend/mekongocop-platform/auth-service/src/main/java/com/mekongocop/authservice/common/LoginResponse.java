package com.mekongocop.authservice.common;
import jakarta.servlet.http.Cookie;

public class LoginResponse {
    private Cookie accessTokenCookie;
    private Cookie refreshTokenCookie;
    private Cookie usernameCookie;

    public LoginResponse(String accessToken, String refreshToken, String username) {
        this.accessTokenCookie = createCookie("access_token", accessToken, 3600);
        this.refreshTokenCookie = createCookie("refresh_token", refreshToken, 259200);
        this.usernameCookie = createCookie("username", username, 259200);
    }

    public LoginResponse(Cookie accessTokenCookie, Cookie refreshTokenCookie, Cookie usernameCookie) {
        this.accessTokenCookie = accessTokenCookie;
        this.refreshTokenCookie = refreshTokenCookie;
        this.usernameCookie = usernameCookie;
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    public Cookie getAccessTokenCookie() {
        return accessTokenCookie;
    }

    public Cookie getRefreshTokenCookie() {
        return refreshTokenCookie;
    }

    public Cookie getUsernameCookie() {
        return usernameCookie;
    }
}
