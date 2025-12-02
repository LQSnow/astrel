package top.lqsnow.astrel.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.lqsnow.astrel.user.User;
import top.lqsnow.astrel.user.UserRepository;

import io.jsonwebtoken.JwtException;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // 这些路径不做认证（auth & health）
        if (requestUri.startsWith("/api/v1/auth")
                || requestUri.startsWith("/api/v1/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 没有 token，直接放行到后面的 Security 规则，让它判是否需要登录
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 校验 token 并解析出用户信息
            String tokenType = jwtService.getTokenType(token);
            if (!"access".equals(tokenType)) {
                // 这里只接受 access token，refresh token 用于专门的刷新接口
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtService.getUserIdFromToken(token);
            String username = jwtService.getUsernameFromToken(token);

            // 简单从数据库查一下用户，确保用户存在且启用
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || Boolean.FALSE.equals(user.getEnabled())) {
                filterChain.doFilter(request, response);
                return;
            }

            // 构造一个 Authentication 放到上下文中
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,  // principal 直接放 User 实体
                            null,
                            Collections.emptyList()  // 暂时不做角色/权限
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException ex) {
            // token 非法或过期，这里先不抛异常，不设置认证信息，
            // 后面的 Security 会根据“没有认证”来决定返回 401。
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
