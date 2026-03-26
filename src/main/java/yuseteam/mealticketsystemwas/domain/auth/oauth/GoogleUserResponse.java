package yuseteam.mealticketsystemwas.domain.auth.oauth;

import java.util.Map;

public class GoogleUserResponse implements OAuth2UserResponse {

    private final Map<String, Object> attribute;

    public GoogleUserResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}

