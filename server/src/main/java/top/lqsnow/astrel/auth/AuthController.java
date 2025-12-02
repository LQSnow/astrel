package top.lqsnow.astrel.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.lqsnow.astrel.auth.dto.LoginRequest;
import top.lqsnow.astrel.auth.dto.LoginResponse;
import top.lqsnow.astrel.common.api.ApiResponse;
import top.lqsnow.astrel.auth.dto.RegisterRequest;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(
                request.getUsername(),
                request.getPassword(),
                request.getDisplayName(),
                request.getInviteCode()
        );
        return ApiResponse.success(null, "注册成功");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.getUsername(), request.getPassword());
        return ApiResponse.success(response, "登录成功");
    }

}
