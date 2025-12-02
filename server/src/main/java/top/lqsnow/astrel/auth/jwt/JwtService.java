package top.lqsnow.astrel.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    // 注意：真实项目中绝对不要把 secret 写死在代码里，要放到配置/环境变量。
    // 这里为了演示简单一点。
    private static final String SECRET = "this_is_a_demo_secret_key_for_astrel_please_change_me";

    // 访问 token 有效期：15 分钟
    private static final long ACCESS_TOKEN_EXPIRE_MS = 15 * 60 * 1000;

    // 刷新 token 有效期：7 天
    private static final long REFRESH_TOKEN_EXPIRE_MS = 7L * 24 * 60 * 60 * 1000;

    private final Key key;

    public JwtService() {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String username) {
        return generateToken(userId, username, ACCESS_TOKEN_EXPIRE_MS, "access");
    }

    public String generateRefreshToken(Long userId, String username) {
        return generateToken(userId, username, REFRESH_TOKEN_EXPIRE_MS, "refresh");
    }

    private String generateToken(Long userId, String username, long expireMillis, String tokenType) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + expireMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))             // 主体：用 userId
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .addClaims(Map.of(
                        "username", username,
                        "type", tokenType
                ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token).getBody();
        String sub = claims.getSubject();
        return Long.valueOf(sub);
    }

    public String getTokenType(String token) {
        Claims claims = parseToken(token).getBody();
        return claims.get("type", String.class);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token).getBody();
        return claims.get("username", String.class);
    }

}
