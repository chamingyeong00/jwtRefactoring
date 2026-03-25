package yuseteam.mealticketsystemwas.domain.auth.util;

public final class BearerTokenExtractor {

    private static final String PREFIX = "Bearer ";

    private BearerTokenExtractor() {
    }

    public static String extract(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) return null;
        if (authorizationHeader.startsWith(PREFIX)) return authorizationHeader.substring(PREFIX.length());
        return authorizationHeader;
    }
}
