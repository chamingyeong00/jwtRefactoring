package yuseteam.mealticketsystemwas.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshAccessRequest {
    private String refreshToken;
}

