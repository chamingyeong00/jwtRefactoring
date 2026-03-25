package yuseteam.mealticketsystemwas.domain.auth.util;

import java.util.Map;

public final class ResponseMapSanitizer {

    private ResponseMapSanitizer() {
    }

    public static Map<String, Object> stripStatus(Map<String, Object> result) {
        if (result == null) return null;
        result.remove("status");
        return result;
    }
}

