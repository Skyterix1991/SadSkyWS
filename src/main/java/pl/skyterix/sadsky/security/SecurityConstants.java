package pl.skyterix.sadsky.security;

class SecurityConstants {

    /**
     * Token property name in application.properties.
     */
    public final static String TOKEN_SECRET_PROPERTY_NAME = "token.secret";
    /**
     * Token expire time in milliseconds.
     * Default 10 days.
     */
    public final static long TOKEN_EXPIRATION_TIME_IN_MS = 864000000;
    /**
     * Header name for token authorization in request.
     */
    public final static String TOKEN_HEADER_NAME = "Authorization";
    /**
     * Token in header prefix.
     */
    public final static String TOKEN_HEADER_PREFIX = "Bearer";
    /**
     * Application name
     */
    public final static String NAME = "SadSky";
    /**
     * Login endpoint URI.
     */
    public final static String LOGIN_URI = "/users/login";
    /**
     * Register endpoint URI.
     */
    public final static String REGISTER_URI = "/users";
}
