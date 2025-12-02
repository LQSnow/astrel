package top.lqsnow.astrel.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 登录用用户名
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // 密码哈希（使用 BCrypt 之后的结果，一般长度在 60 左右）
    @Column(nullable = false, length = 100)
    private String password;

    // 昵称 / 显示名称
    @Column(nullable = false, length = 50)
    private String displayName;

    // 账号是否启用
    @Column(nullable = false)
    private Boolean enabled;
}
