package yuseteam.mealticketsystemwas.domain.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    private String userId;
    private String userPW;
}
