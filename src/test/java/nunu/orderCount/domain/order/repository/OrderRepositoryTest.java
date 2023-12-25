package nunu.orderCount.domain.order.repository;

import java.time.LocalDateTime;
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
        Member testMember = createTestMember("email", "password");
        memberRepository.save(testMember);
        Product testProduct = createTestProduct("pro", "option", "imageurl", testMember);
        productRepository.save(testProduct);
        Option testOption = Option.builder()
                .product(testProduct)
                .name("option")
                .build();
        optionRepository.save(testOption);

        Order testOrder = Order.builder()
                .orderItemNumber("1")
                .orderDateTime(LocalDateTime.of(2023,12,8,10,10,10))
                .orderNumber("orderNumber")
                .member(testMember)
                .quantity(2L)
                .option(testOption)
                .build();
        Order testOrder2 = Order.builder()
                .orderItemNumber("2")
                .orderDateTime(LocalDateTime.of(2023,12,8,10,10,10))
                .orderNumber("orderNumber2")
                .member(testMember)
                .quantity(2L)
                .option(testOption)
                .build();
        orderRepository.save(testOrder);
        orderRepository.save(testOrder2);

        //when
        Optional<Order> latestOrder = orderRepository.findTopByMemberOrderByOrderDateTimeDesc(testOrder.getMember());

        //then
        assertThat(latestOrder.get().getMember()).isEqualTo(testMember);
    }

    @DisplayName("주문 없을 때 최근 주문 조회")
    @Test
    void findTopByMemberOrderByDatePaidDescWhenNotOrderTest(){
        //given
        Member testMember = createTestMember("email", "password");
        memberRepository.save(testMember);
        //when
        Optional<Order> latestOrder = orderRepository.findTopByMemberOrderByOrderDateTimeDesc(testMember);

        //then
        assertThat(latestOrder).isEqualTo(Optional.empty());
    }

    private Member createTestMember(String email, String password){
        Member testMember = Member.builder()
                .email(email)
                .password(password)
                .build();
        return testMember;
    }

    private Product createTestProduct(String name, String url, String zigzagProductId, Member member){
        Product testProduct = Product.builder()
                .imageUrl(url)
                .name(name)
                .zigzagProductId(zigzagProductId)
                .member(member)
                .build();
        return testProduct;
    }
}