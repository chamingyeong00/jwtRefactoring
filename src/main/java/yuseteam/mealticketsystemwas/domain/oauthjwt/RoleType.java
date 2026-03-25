package yuseteam.mealticketsystemwas.domain.oauthjwt;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {
    STUDENT, ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
