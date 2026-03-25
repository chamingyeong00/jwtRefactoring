package yuseteam.mealticketsystemwas.domain.oauthjwt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import yuseteam.mealticketsystemwas.domain.oauthjwt.RoleType;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
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

    private String socialname;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private Integer tokenVersion = 0;

    @Builder
    public User(String userId, String userPW, String name, RoleType role, String phone) {
        this.userId = userId;
        this.userPW = userPW;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.tokenVersion = 0;
    }

}
