package yuseteam.mealticketsystemwas.domain.oauthjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import yuseteam.mealticketsystemwas.domain.oauthjwt.RoleType;

@Getter
@Setter
@AllArgsConstructor
public class SignUpReqDto {

    private String userId;
    private String userPW;
    private String name;
    private RoleType role;
    private String phone;

}
