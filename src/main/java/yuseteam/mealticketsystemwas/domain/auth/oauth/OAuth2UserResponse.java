package yuseteam.mealticketsystemwas.domain.auth.oauth;

public interface OAuth2UserResponse {
    String getProvider();
    String getProviderId();
    String getName();
}

