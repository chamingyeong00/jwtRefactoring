package yuseteam.mealticketsystemwas.domain.oauthjwt.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2UserDTO implements OAuth2User {

    private final UserDTO userDTO;

    public CustomOAuth2UserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        if (userDTO.getRole() != null) {
            collection.add(userDTO.getRole());
        }

        return collection;
    }

    @Override
    public String getName() {
        return userDTO.getName();
    }

    public Long getId() {
        return userDTO.getId();
    }
}