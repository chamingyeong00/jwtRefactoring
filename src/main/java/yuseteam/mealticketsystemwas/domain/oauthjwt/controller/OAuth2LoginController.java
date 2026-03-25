package yuseteam.mealticketsystemwas.domain.oauthjwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth2")
@Tag(name = "OAuth2", description = "소셜 인증/회원가입·로그인 API")
public class OAuth2LoginController {

    @Operation(
            summary = "Naver OAuth 로그인 및 회원가입",
            description = "이 경로를 호출하면 Spring Security가 Naver OAuth2 인증 페이지로 리디렉션합니다. 인증 성공 후, 사용자가 처음이면 회원가입을 처리하고, 기존 사용자라면 로그인 처리를 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "네이버 로그인 페이지로 리디렉션됨"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/authorization/naver")
    public String naverLogin() {
        return "이 경로는 네이버 OAuth2 로그인 및 회원가입을 처리하는 경로입니다. Swagger에서 테스트하지 않으며, 실제 로그인/회원가입 프로세스는 Spring Security가 처리합니다.";
    }

    @Operation(
            summary = "Google OAuth 로그인 및 회원가입",
            description = "이 경로를 호출하면 Spring Security가 Google OAuth2 인증 페이지로 리디렉션합니다. 인증 성공 후, 사용자가 처음이면 회원가입을 처리하고, 기존 사용자라면 로그인 처리를 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "구글 로그인 페이지로 리디렉션됨"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/authorization/google")
    public String googleLogin() {
        return "이 경로는 구글 OAuth2 로그인 및 회원가입을 처리하는 경로입니다. Swagger에서 테스트하지 않으며, 실제 로그인/회원가입 프로세스는 Spring Security가 처리합니다.";
    }

    @Operation(
            summary = "Kakao OAuth 로그인 및 회원가입",
            description = "이 경로를 호출하면 Spring Security가 Kakao OAuth2 인증 페이지로 리디렉션합니다. 인증 성공 후, 사용자가 처음이면 회원가입을 처리하고, 기존 사용자라면 로그인 처리를 합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "카카오 로그인 페이지로 리디렉션됨"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/authorization/kakao")
    public String kakaoLogin() {
        return "이 경로는 카카오 OAuth2 로그인 및 회원가입을 처리하는 경로입니다. Swagger에서 테스트하지 않으며, 실제 로그인/회원가입 프로세스는 Spring Security가 처리합니다.";
    }

    @Operation(summary = "로그인 성공 후 토큰 저장", description = "로그인 성공 후 쿠키에 JWT 토큰을 저장하고 응답으로 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(description = "JWT 토큰을 쿠키에 저장한 후 반환하는 응답 객체")),
                    headers = @Header(
                            name = "Set-Cookie",
                            description = "Authorization=<JWT 토큰> (payload에 id, role 포함)")
            )
    })
    @GetMapping("/success")
    public void oauthSuccess() {
    }

}