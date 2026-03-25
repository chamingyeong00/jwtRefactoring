package yuseteam.mealticketsystemwas.domain.oauthjwt.dto;

import lombok.Getter;
import lombok.Setter;
import yuseteam.mealticketsystemwas.domain.oauthjwt.RoleType;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private RoleType role;
    private String name;
    private String socialname;
}
