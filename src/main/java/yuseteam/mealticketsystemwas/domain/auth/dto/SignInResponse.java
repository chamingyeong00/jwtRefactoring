package yuseteam.mealticketsystemwas.domain.auth.dto;

import lombok.*;
import yuseteam.mealticketsystemwas.domain.auth.entity.RoleType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponse {
    private String accessToken;
    private String refreshToken;
    private String name;
    private RoleType role;
}
