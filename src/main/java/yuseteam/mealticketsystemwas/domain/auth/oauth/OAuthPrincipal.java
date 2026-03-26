package yuseteam.mealticketsystemwas.domain.auth.oauth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * OAuth2 로그인 후 success handler가 userId를 안정적으로 꺼낼 수 있게 하는 최소 Principal.
 */
public class OAuthPrincipal implements OAuth2User {

    private final Long userId;

    public OAuthPrincipal(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}

