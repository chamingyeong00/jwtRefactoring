package yuseteam.mealticketsystemwas.domain.auth.oauth;

import java.util.Map;

public class KakaoUserResponse implements OAuth2UserResponse {

    private final Map<String, Object> attribute;

    public KakaoUserResponse(Map<String, Object> attribute) { this.attribute = attribute; }

    @Override
    public String getProvider() { return "kakao"; }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}

