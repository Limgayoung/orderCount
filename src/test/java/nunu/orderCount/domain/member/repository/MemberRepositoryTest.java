package nunu.orderCount.domain.member.repository;

import nunu.orderCount.domain.member.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("email로 회원 찾기")
    @Test
    void findByEmail() {
        //given
        String email = "email@email.com";
        Member member = Member.builder()
                .email(email)
                .password("password")
                .build();
        //when
        memberRepository.save(member);
        //then
        assertThat(memberRepository.findByEmail(email).get().getEmail()).isEqualTo(email);
    }

    @DisplayName("email로 회원 존재 여부 조회")
    @Test
    void existsByEmail() {
        //given
        String email = "email@email.com";
        Member member = Member.builder()
                .email(email)
                .password("password")
                .build();
        //when
        memberRepository.save(member);
        //then
        assertThat(memberRepository.existsByEmail(email)).isEqualTo(true);
    }
}