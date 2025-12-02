package top.lqsnow.astrel.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import top.lqsnow.astrel.auth.dto.LoginResponse;
import top.lqsnow.astrel.auth.jwt.JwtService;
import top.lqsnow.astrel.common.error.ErrorCode;
import top.lqsnow.astrel.common.exception.BizException;
import top.lqsnow.astrel.user.User;
import top.lqsnow.astrel.user.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final InviteCodeRepository inviteCodeRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public void register(String username, String rawPassword, String displayName, String inviteCodeStr) {
        // 1. 校验用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new BizException(ErrorCode.AUTH_USERNAME_EXISTS, "用户名已被使用");
        }

        // 2. 校验邀请码
        InviteCode inviteCode = inviteCodeRepository
                .findByCodeAndUsedFalse(inviteCodeStr)
                .orElseThrow(() -> new BizException(ErrorCode.AUTH_INVITE_CODE_INVALID, "邀请码无效或已被使用"));

        // 3. 密码加密
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 4. 创建用户
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .displayName(displayName)
                .enabled(true)
                .build();
        User saved = userRepository.save(user);

        // 5. 标记邀请码已使用
        inviteCode.setUsed(true);
        inviteCode.setUsedByUserId(saved.getId());
        inviteCodeRepository.save(inviteCode);
    }

    public LoginResponse login(String username, String rawPassword) {
        // 1. 查用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BizException(ErrorCode.AUTH_INVALID_CREDENTIALS, "用户名或密码错误"));

        // 2. 检查账号是否可用
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new BizException(ErrorCode.AUTH_ACCOUNT_LOCKED, "账号已被封禁");
        }

        // 3. 校验密码（使用 BCrypt）
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        if (!matches) {
            throw new BizException(ErrorCode.AUTH_INVALID_CREDENTIALS, "用户名或密码错误");
        }

        // 4. 生成 token
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}
