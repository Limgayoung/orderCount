package nunu.orderCount.domain.member.model;

import lombok.AccessLevel;
import lombok.Builder;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        if (!memberId.equals(member.memberId)) return false;
        return email.equals(member.email);
    }

    @Override
    public int hashCode() {
        int result = memberId.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}
