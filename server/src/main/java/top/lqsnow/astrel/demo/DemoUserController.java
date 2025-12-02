package top.lqsnow.astrel.demo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;
import top.lqsnow.astrel.common.api.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/demo-users")
@RequiredArgsConstructor
public class DemoUserController {

    private final DemoUserRepository demoUserRepository;

    @PostMapping
    public ApiResponse<DemoUser> create(@RequestBody CreateDemoUserRequest request) {
        DemoUser user = DemoUser.builder()
                .username(request.getUsername())
                .displayName(request.getDisplayName())
                .build();
        DemoUser saved = demoUserRepository.save(user);
        return ApiResponse.success(saved);
    }

    @GetMapping
    public ApiResponse<List<DemoUser>> list() {
        List<DemoUser> all = demoUserRepository.findAll();
        return ApiResponse.success(all);
    }

    @Getter
    @Setter
    public static class CreateDemoUserRequest {
        private String username;
        private String displayName;
    }
}
