package nunu.orderCount.domain.member.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.global.common.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Table(name = "member")
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @NotNull
    private String email; //id
    @NotNull
    private String password;
    @NotNull
    private Role role;
    @NotNull
    private boolean isLocked;
    //todo: refresh, zigzag token 은 redis 로 관리할 예정
    //todo: platform 추가

    @Builder
    public Member(String email, String password) {
        this.email = email;
        this.password = password;
        this.isLocked = false;
        this.role = Role.OWNER;
    }
    //todo: role 관련해서 수정할 것.
}
