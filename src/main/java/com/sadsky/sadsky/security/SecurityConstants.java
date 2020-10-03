package com.sadsky.sadsky.security;

class SecurityConstants {

    public final static String TOKEN_SECRET_PROPERTY_NAME = "token.secret";
    public final static long TOKEN_EXPIRATION_TIME_IN_MS = 864000000; // 10 days
    public final static String TOKEN_HEADER_NAME = "Authorization";
    public final static String TOKEN_HEADER_PREFIX = "Bearer";
    public final static String NAME = "SadSky";

    public final static String LOGIN_URI = "/users/login";
    public final static String REGISTER_URI = "/users";

}
