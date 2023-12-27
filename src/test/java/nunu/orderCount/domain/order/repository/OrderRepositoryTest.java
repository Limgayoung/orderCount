package nunu.orderCount.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.order.model.Order;
import nunu.orderCount.domain.order.model.OrderCountByOption;
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

    @DisplayName("option 별 order quantity 집계")
    @Test
    void sumIsDoneFalseOrdersByOption(){
        Member testMember = createTestMember("email", "password");
        memberRepository.save(testMember);
        Product testProduct = createTestProduct("product", "url", "111", testMember);
        Option option1 = createTestOption(testProduct, "option1");
        Option option2 = createTestOption(testProduct, "option2");
        Order testOrder = createTestOrder(option1, testMember, "11", "11");
        orderRepository.save(testOrder);
        Order testOrder2 = createTestOrder(option2, testMember, "12", "12");
        orderRepository.save(testOrder2);
        List<OrderCountByOption> orderCountByOptions = orderRepository.sumIsDoneFalseOrdersByOption(testMember);

        assertThat(orderCountByOptions.size()).isEqualTo(2);
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

    private Option createTestOption(Product product, String name){
        return Option.builder()
                .product(product)
                .name(name)
                .build();
    }

    private Order createTestOrder(Option option, Member member, String orderNumber, String orderItemNumber){
        return Order.builder()
                .option(option)
                .orderDateTime(LocalDateTime.of(2023, 12, 12, 12, 12, 12))
                .orderNumber(orderNumber)
                .orderItemNumber(orderItemNumber)
                .quantity(2L)
                .member(member)
                .build();
    }
}