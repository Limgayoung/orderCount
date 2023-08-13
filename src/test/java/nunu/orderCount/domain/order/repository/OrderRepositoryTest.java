package nunu.orderCount.domain.order.repository;

import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.order.model.Order;
import nunu.orderCount.domain.product.model.Product;
import nunu.orderCount.domain.product.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OptionRepository optionRepository;

    @DisplayName("가장 최근 주문 1건 조회")
    @Test
    void findTopByMemberOrderByDatePaidDesc() {
        //given
        Member testMember = createTestMember("email", "password", 1L);
        memberRepository.save(testMember);
        Product testProduct = createTestProduct("pro", "option", "imageurl", 1L, testMember);
        productRepository.save(testProduct);
        Option testOption = Option.builder()
                .product(testProduct)
                .name("option")
                .inventoryQuantity(2)
                .build();
        optionRepository.save(testOption);

        Order testOrder = Order.builder()
                .orderItemNumber("1")
                .datePaid(20230812L)
                .orderNumber("orderNumber")
                .member(testMember)
                .quantity(2L)
                .option(testOption)
                .build();
        Order testOrder2 = Order.builder()
                .orderItemNumber("2")
                .datePaid(20230813L)
                .orderNumber("orderNumber2")
                .member(testMember)
                .quantity(2L)
                .option(testOption)
                .build();
        orderRepository.save(testOrder);
        orderRepository.save(testOrder2);

        //when
        Optional<Order> latestOrder = orderRepository.findTopByMemberOrderByDatePaidDesc(testOrder.getMember());

        //then
        assertThat(latestOrder.get().getMember()).isEqualTo(testMember);
    }
    private Member createTestMember(String email, String password, Long memberId){
        Member testMember = Member.builder()
                .email(email)
                .password(password)
                .build();

        ReflectionTestUtils.setField(
                testMember,
                "memberId",
                memberId,
                Long.class
        );
        return testMember;
    }

    private Product createTestProduct(String name, String url, String zigzagProductId, Long productId, Member member){
        Product testProduct = Product.builder()
                .imageUrl(url)
                .name(name)
                .zigzagProductId(zigzagProductId)
                .member(member)
                .build();
        ReflectionTestUtils.setField(
                testProduct,
                "productId",
                productId,
                Long.class
        );
        return testProduct;
    }
}