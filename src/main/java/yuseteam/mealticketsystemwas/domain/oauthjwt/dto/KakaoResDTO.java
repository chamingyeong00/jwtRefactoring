package yuseteam.mealticketsystemwas.domain.oauthjwt.dto;

import java.util.Map;

public class KakaoResDTO implements OAuth2ResDTO {

    private final Map<String, Object> attribute;

    public KakaoResDTO(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() { return attribute.get("name").toString(); }
}
