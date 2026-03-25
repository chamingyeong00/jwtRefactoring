package yuseteam.mealticketsystemwas.domain.oauthjwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.SignInReqDto;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.SignInResDto;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.SignUpReqDto;
import yuseteam.mealticketsystemwas.domain.oauthjwt.service.UserService;
import yuseteam.mealticketsystemwas.domain.oauthjwt.jwt.JWTService;
import yuseteam.mealticketsystemwas.domain.oauthjwt.service.LogoutService;

@Tag(name = "Auth", description = "인증/회원가입·로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JWTService jwtService;
    private final LogoutService logoutService;

    @Operation(
            summary = "회원가입",
            description = "사용자 정보를 받아 회원가입을 처리합니다. 이미 존재하는 아이디일 경우 오류 메시지를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "\"회원가입 성공\""))
            ),
            @ApiResponse(responseCode = "409", description = "아이디 또는 전화번호 중복 또는 전화번호 형식 오류",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "아이디 중복", value = "\"이미 존재하는 아이디입니다.\""),
                                    @ExampleObject(name = "전화번호 중복", value = "\"이미 등록된 전화번호입니다.\""),
                                    @ExampleObject(name = "전화번호 형식 오류", value = "\"전화번호는 숫자만 입력 가능하며, 11자리여야 합니다.\"")
                            },
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpReqDto signUpReqDto) {
        try {
            userService.signup(signUpReqDto);
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(
            summary = "로그인",
            description = "사용자 아이디와 비밀번호로 로그인하여 JWT 토큰을 반환합니다. 로그인 후, Authorization 헤더에 JWT 토큰을 포함하여 응답을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SignInResDto.class),
                            examples = @ExampleObject(value = """
                                        {
                                          "accessToken": "<jwt-token>",
                                          "name": "홍길동",
                                          "role": "STUDENT"
                                        }
                                        """)
                    ),
                    headers = @Header(name = "Authorization", description = "JWT 토큰. " +
                            "payload에는 { \"id\": <Long>, \"role\": <String> } 가 포함됩니다.")
            ),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 잘못된 경우",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "accessToken": null,
                                          "name": "아이디 또는 비밀번호가 올바르지 않습니다.",
                                          "role": null
                                        }
                                        """))
            )
    })
    @PostMapping("/signin")
    public ResponseEntity<SignInResDto> login(@RequestBody SignInReqDto signInReqDto, HttpServletResponse response) {
        try {
            SignInResDto signInResDto = userService.login(signInReqDto);
            response.setHeader("Authorization", signInResDto.getAccessToken());
            Cookie cookie = new Cookie("Authorization", signInResDto.getAccessToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);
            response.addCookie(cookie);
            return ResponseEntity.ok(signInResDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new SignInResDto(null, e.getMessage(), null));
        }
    }

    @Operation(
        summary = "로그아웃",
        description = "Authorization 헤더의 JWT를 무효화(버전 증가)하여 만료 전까지 무효화합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "\"로그아웃 성공\""))
        ),
        @ApiResponse(responseCode = "401", description = "이미 파괴된 토큰",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "\"Token has been revoked.\""))
        ),
        @ApiResponse(responseCode = "400", description = "토큰 없음 또는 잘못된 토큰",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "\"Invalid token.\""))
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            HttpServletRequest request,
            HttpServletResponse response) {
        String token = null;
        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            token = authorizationHeader.replace("Bearer ", "");
        } else {
            // 헤더가 없으면 쿠키에서 찾음
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("Authorization".equals(c.getName())) {
                        token = c.getValue();
                        break;
                    }
                }
            }
        }

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }

        try {
            if (jwtService.isExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 만료되었습니다.");
            }
            Long userId = jwtService.parseUserId(token);
            logoutService.logoutByUserId(userId);
            // Authorization 쿠키 삭제
            Cookie cookie = new Cookie("Authorization", "");
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return ResponseEntity.ok("로그아웃 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }
    }
}