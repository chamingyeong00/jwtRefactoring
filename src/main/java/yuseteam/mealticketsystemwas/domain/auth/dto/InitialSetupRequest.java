package yuseteam.mealticketsystemwas.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import yuseteam.mealticketsystemwas.domain.auth.entity.RoleType;

@Getter
@Setter
public class InitialSetupRequest {
    @NotNull
    private RoleType role;
    @NotBlank
    private String phone;
}
