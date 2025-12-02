package top.lqsnow.astrel.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.lqsnow.astrel.common.api.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class MeController {

    @GetMapping
    public ApiResponse<Map<String, Object>> me(Authentication authentication) {
        // 在 JwtAuthenticationFilter 里，我们把 User 放到了 Authentication 的 principal 里
        User user = (User) authentication.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("displayName", user.getDisplayName());

        return ApiResponse.success(data);
    }
}
