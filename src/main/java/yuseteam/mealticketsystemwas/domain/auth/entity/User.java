package yuseteam.mealticketsystemwas.domain.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import yuseteam.mealticketsystemwas.domain.auth.entity.RoleType;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String userId;

    @NotBlank
    private String userPW;

    private String name;

    /** provider + providerId */
    private String socialname;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    @Builder.Default
    private Integer tokenVersion = 0;
}
