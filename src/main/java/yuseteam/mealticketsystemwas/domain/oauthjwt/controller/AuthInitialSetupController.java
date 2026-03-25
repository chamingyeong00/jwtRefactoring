package yuseteam.mealticketsystemwas.domain.oauthjwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuseteam.mealticketsystemwas.domain.oauthjwt.dto.InitialSetupReqDTO;
import yuseteam.mealticketsystemwas.domain.oauthjwt.service.AuthInitialSetupService;

import java.util.Map;

@Tag(name = "Auth", description = "초기 설정(역할+전화) 통합 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthInitialSetupController {

    private final AuthInitialSetupService authInitialSetupService;

    @Operation(
            summary = "초기 설정: 역할 선택 + 전화번호 등록(한 번에 처리)",
            description = """
        소셜 로그인 직후 한 번의 호출로 역할(STUDENT|ADMIN)과 전화번호(숫자 11자리)를 동시에 설정합니다.
        성공 시 역할 클레임이 포함된 새 JWT를 Set-Cookie로 재발급합니다.
        """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설정 성공",
                    headers = @Header(name = "Set-Cookie", description = "Authorization=<새JWT>"),
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                            {
                              "message": "initial setup complete",
                              "id": 7,
                              "role": "STUDENT",
                              "phone": "01012345678",
                              "token": "<new-jwt-token>"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "유효성 오류",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {"error":"전화번호는 숫자만 입력 가능하며, 11자리여야 합니다.", "status":400}
                """))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {"error":"Authorization 쿠키가 없습니다.", "status":401}
                """))),
            @ApiResponse(responseCode = "409", description = "전화번호 중복",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                {"error":"이미 등록된 전화번호입니다.", "status":409}
                """)))
    })
    @PostMapping("/initial-setup")
    public ResponseEntity<?> initialSetup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "역할/전화번호",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InitialSetupReqDTO.class),
                            examples = {
                                    @ExampleObject(name = "STUDENT", value = "{ \"role\":\"STUDENT\", \"phone\":\"01012345678\" }"),
                                    @ExampleObject(name = "ADMIN", value = "{ \"role\":\"ADMIN\", \"phone\":\"01099998888\" }")
                            })
            )
            @RequestBody InitialSetupReqDTO body,
            HttpServletRequest request,
            HttpServletResponse response) {

        Map<String, Object> result = authInitialSetupService.initialSetup(body, request, response);
        int status = (int) result.getOrDefault("status", 200);
        result.remove("status");
        return ResponseEntity.status(status).body(result);
    }
}