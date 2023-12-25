package nunu.orderCount.domain.option.repository;

import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.product.model.Product;
import nunu.orderCount.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OptionRepositoryTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OptionRepository optionRepository;

    @Test
    void findByProductAndName() {

    }

    @Test
    @DisplayName("product, name 가진 option 존재하는지 찾기")
    void existsByProductAndName() {
        Member testMember = createTestMember("email", "password");
        memberRepository.save(testMember);
        Product p1 = productRepository.save(createTestProduct("p1", "1", testMember));
        Product p2 = productRepository.save(createTestProduct("p2", "2", testMember));

        optionRepository.save(createTestOption(p1, "option1"));
        optionRepository.save(createTestOption(p2, "option2"));

        assertThat(optionRepository.existsByProductAndName(p1, "option1")).isTrue();
        assertThat(optionRepository.existsByProductAndName(p2, "option2")).isTrue();
        assertThat(optionRepository.existsByProductAndName(p1, "option2")).isFalse();
        assertThat(optionRepository.existsByProductAndName(p1, "option3")).isFalse();

    }

    private Option createTestOption(Product product, String name) {
        return Option.builder()
                .product(product)
                .name(name)
                .build();
    }

    private Product createTestProduct(String name, String zigzagProductId, Member member){
        return Product.builder()
                .name(name)
                .zigzagProductId(zigzagProductId)
                .imageUrl("url")
                .member(member)
                .build();
    }

    private Member createTestMember(String email, String password){
        Member testMember = Member.builder()
                .email(email)
                .password(password)
                .build();

        return testMember;
    }
}