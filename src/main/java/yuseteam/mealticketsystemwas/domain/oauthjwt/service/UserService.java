package yuseteam.mealticketsystemwas.domain.oauthjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.SignInReqDto;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.SignInResDto;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.SignUpReqDto;
import yuseteam.mealticketsystemwas.domain.oauthjwt.entity.User;
import yuseteam.mealticketsystemwas.domain.oauthjwt.jwt.JWTService;
import yuseteam.mealticketsystemwas.domain.oauthjwt.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Transactional
    public void signup(SignUpReqDto req) {

        if (!req.getPhone().matches("\\d{11}")) {
            throw new IllegalArgumentException("전화번호는 숫자만 입력 가능하며, 11자리여야 합니다.");
        }

        if (userRepository.existsByUserId(req.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (userRepository.existsByPhone(req.getPhone())) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }

        String encodedPw = passwordEncoder.encode(req.getUserPW());

        User user = User.builder()
                .userId(req.getUserId())
                .userPW(encodedPw)
                .name(req.getName())
                .role(req.getRole())
                .phone(req.getPhone())
                .build();

        userRepository.save(user);
    }

    @Transactional
    public SignInResDto login(SignInReqDto req) {
        User user = userRepository.findByUserId(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(req.getUserPW(), user.getUserPW())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String roleName = (user.getRole() == null) ? null : user.getRole().name();
        Integer tokenVersion = (user.getTokenVersion() == null) ? 0 : user.getTokenVersion();
        String token = jwtService.createToken(user.getId(), roleName, tokenVersion);

        return new SignInResDto(token, user.getName(), user.getRole());
    }
}