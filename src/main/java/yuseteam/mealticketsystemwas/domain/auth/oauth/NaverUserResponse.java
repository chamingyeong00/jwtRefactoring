package yuseteam.mealticketsystemwas.domain.auth.oauth;

import java.util.Map;

public class NaverUserResponse implements OAuth2UserResponse {

    private final Map<String, Object> attribute;

    public NaverUserResponse(Map<String, Object> attribute) { this.attribute = attribute; }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}

