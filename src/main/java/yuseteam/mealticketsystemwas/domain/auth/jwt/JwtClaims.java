package yuseteam.mealticketsystemwas.domain.auth.jwt;

public final class JwtClaims {
    private JwtClaims() {}

    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_TYPE = "type";
    public static final String CLAIM_TOKEN_VERSION = "tokenVersion";
}
