package yuseteam.mealticketsystemwas.domain.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseteam.mealticketsystemwas.domain.auth.jwt.AuthTokenType;
import yuseteam.mealticketsystemwas.domain.auth.dto.SignInRequest;
import yuseteam.mealticketsystemwas.domain.auth.dto.SignInResponse;
import yuseteam.mealticketsystemwas.domain.auth.dto.SignUpRequest;
import yuseteam.mealticketsystemwas.domain.auth.jwt.JwtTokenService;
import yuseteam.mealticketsystemwas.domain.auth.entity.User;
import yuseteam.mealticketsystemwas.domain.auth.repository.UserRepository;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public void signup(SignUpRequest req) {
        if (req.getPhone() == null || !req.getPhone().matches("\\d{11}")) {
            throw new IllegalArgumentException("전화번호는 숫자만 입력 가능하며, 11자리여야 합니다.");
        }

        if (userRepository.existsByUserId(req.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (userRepository.existsByPhone(req.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        String encodedPw = passwordEncoder.encode(req.getUserPW());
        User user = req.toEntity(encodedPw);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest req) {
        User user = userRepository.findByUserId(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.getUserPW(), user.getUserPW())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return issueTokensFor(user);
    }

    @Transactional
    public SignInResponse refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        if (jwtTokenService.isExpired(refreshToken)) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        String type = jwtTokenService.parseType(refreshToken);
        if (!AuthTokenType.REFRESH.name().toLowerCase().equals(type)) {
            throw new IllegalArgumentException("refresh 토큰이 아닙니다.");
        }

        Long userId = jwtTokenService.parseUserId(refreshToken);
        if (userId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        Integer tokenVerFromToken = jwtTokenService.parseTokenVersion(refreshToken);
        if (tokenVerFromToken == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        int currentTokenVer = (user.getTokenVersion() == null) ? 0 : user.getTokenVersion();
        if (tokenVerFromToken != currentTokenVer) {
            throw new IllegalArgumentException("토큰이 무효화되었습니다.");
        }

        String roleName = (user.getRole() == null) ? null : user.getRole().name();
        String newAccess = jwtTokenService.issueAccessToken(user.getId(), roleName, currentTokenVer);

        return new SignInResponse(newAccess, null, user.getName(), user.getRole());
    }

    private SignInResponse issueTokensFor(User user) {
        String roleName = user.getRole() == null ? null : user.getRole().name();
        int tokenVersion = user.getTokenVersion() == null ? 0 : user.getTokenVersion();

        String accessToken = jwtTokenService.issueAccessToken(user.getId(), roleName, tokenVersion);
        String refreshToken = jwtTokenService.issueRefreshToken(user.getId(), roleName, tokenVersion);

        return new SignInResponse(accessToken, refreshToken, user.getName(), user.getRole());
    }
}
