package yuseteam.mealticketsystemwas.domain.oauthjwt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import yuseteam.mealticketsystemwas.domain.oauthjwt.RoleType;

@Getter
@Setter
public class InitialSetupReqDTO {
    @NotNull
    private RoleType role;
    @NotBlank
    private String phone;
}
