package nunu.orderCount.domain.product.repository;

import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.product.model.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("zigzag 상품 id로 상품 존재 확인")
    @Test
    void existsByZigzagProductId(){
        //given
        Member testMember = createTestMember("email", "password");
        memberRepository.save(testMember);
        Product testProduct = Product.builder()
                .imageUrl("url")
                .name("name")
                .zigzagProductId("zigzagProductId")
                .member(testMember)
                .build();
        productRepository.save(testProduct);

        //when
        Boolean isExists = productRepository.existsByZigzagProductId("zigzagProductId");
        Boolean isExists2 = productRepository.existsByZigzagProductId("zigzagProductIdd");

        //then
        assertThat(isExists).isTrue();
        assertThat(isExists2).isFalse();
    }

    @DisplayName("zigzag 상품 번호로 조회")
    @Test
    void findByZigzagProductId() {
        //given
        Member testMember = createTestMember("email", "password");
        memberRepository.save(testMember);
        Product testProduct = Product.builder()
                .imageUrl("url")
                .name("name")
                .zigzagProductId("zigzagProductId")
                .member(testMember)
                .build();
        productRepository.save(testProduct);

        //when
        Optional<Product> product = productRepository.findByZigzagProductId("zigzagProductId");
        //then
        assertThat(product.get().getZigzagProductId()).isEqualTo("zigzagProductId");

    }
    private Member createTestMember(String email, String password){
        Member testMember = Member.builder()
                .email(email)
                .password(password)
                .build();

        return testMember;
    }
}