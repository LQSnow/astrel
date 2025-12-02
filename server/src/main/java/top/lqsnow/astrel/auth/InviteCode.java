package top.lqsnow.astrel.auth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invite_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 邀请码字符串，比如 "ABC123"
    @Column(nullable = false, unique = true, length = 32)
    private String code;

    // 是否已经被使用
    @Column(nullable = false)
    private Boolean used;

    // 谁用了这枚邀请码（简单起见，直接存用户 ID）
    @Column
    private Long usedByUserId;
}
