package nunu.orderCount.domain.member.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.global.common.BaseEntity;

import javax.persistence.*;

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
    private String storeName;
    private String role;
    //todo: refresh, zigzag token 은 redis 로 관리할 예정

    @Builder
    public Member(String email, String password, String storeName) {
        this.email = email;
        this.password = password;
        this.storeName = storeName;
    }
}
