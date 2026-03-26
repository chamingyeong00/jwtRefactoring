package yuseteam.mealticketsystemwas.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuseteam.mealticketsystemwas.domain.auth.service.OnboardingService;
import yuseteam.mealticketsystemwas.domain.auth.dto.InitialSetupRequest;
import yuseteam.mealticketsystemwas.domain.auth.util.ResponseMapSanitizer;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @Operation(summary = "초기 설정(역할+전화)", description = "signup 토큰으로 role/phone 설정 후 access 토큰으로 교환")
    @PostMapping("/initial-setup")
    public ResponseEntity<?> initialSetup(@RequestBody InitialSetupRequest body, HttpServletRequest request) {
        Map<String, Object> result = onboardingService.initialSetup(body, request);
        int status = (int) result.getOrDefault("status", 200);

        if (status == 200 && result.get("accessToken") instanceof String token) {
            return ResponseEntity.status(status)
                    .header("Authorization", "Bearer " + token)
                    .body(ResponseMapSanitizer.stripStatus(result));
        }

        return ResponseEntity.status(status).body(ResponseMapSanitizer.stripStatus(result));
    }
}
