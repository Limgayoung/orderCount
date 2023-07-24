package nunu.orderCount.domain.member.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.global.common.BaseEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String email; //id
    private String password;
    private Role role;
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
