package yuseteam.mealticketsystemwas.domain.oauthjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yuseteam.mealticketsystemwas.domain.oauthjwt.RoleType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignInResDto {

    private String accessToken;
    private String name;
    private RoleType role;

}
