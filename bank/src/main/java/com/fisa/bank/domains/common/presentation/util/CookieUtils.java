package com.fisa.bank.domains.common.presentation.util;

import static com.fisa.bank.domains.common.config.security.jwt.JwtConst.COOKIE_ACCESS_TOKEN;
import static com.fisa.bank.domains.common.config.security.jwt.JwtConst.COOKIE_REFRESH_TOKEN;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public final class CookieUtils {

  private CookieUtils() {}

  public static void addTokenCookies(
      HttpServletResponse response, String accessToken, String refreshToken) {
    addCookie(response, COOKIE_ACCESS_TOKEN, accessToken, true);
    addCookie(response, COOKIE_REFRESH_TOKEN, refreshToken, true);
  }

  public static void clearTokenCookies(HttpServletResponse response) {
    addCookie(response, COOKIE_ACCESS_TOKEN, "", true, 0);
    addCookie(response, COOKIE_REFRESH_TOKEN, "", true, 0);
  }

  private static void addCookie(
      HttpServletResponse response, String name, String value, boolean httpOnly) {
    addCookie(response, name, value, httpOnly, -1);
  }

  private static void addCookie(
      HttpServletResponse response, String name, String value, boolean httpOnly, int maxAge) {
    ResponseCookie cookie =
        ResponseCookie.from(name, value)
            .httpOnly(httpOnly)
            .secure(true)
            .path("/")
            .sameSite("Strict")
            .maxAge(maxAge)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
