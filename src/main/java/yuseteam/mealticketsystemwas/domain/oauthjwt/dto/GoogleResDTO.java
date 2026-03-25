package yuseteam.mealticketsystemwas.domain.oauthjwt.dto;

import java.util.Map;

public class GoogleResDTO implements OAuth2ResDTO {

    private final Map<String, Object> attribute;

    public GoogleResDTO(Map<String, Object> attribute) {
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
