package yuseteam.mealticketsystemwas.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yuseteam.mealticketsystemwas.domain.auth.entity.RoleType;
import yuseteam.mealticketsystemwas.domain.auth.entity.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    private String userId;
    private String userPW;
    private String name;
    private RoleType role;
    private String phone;

    public User toEntity(String encodedPw) {
        return User.builder()
                .userId(this.userId)
                .userPW(encodedPw)
                .name(this.name)
                .role(this.role)
                .phone(this.phone)
                .build();
    }
}
