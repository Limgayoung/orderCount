package nunu.orderCount.domain.order.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.order.model.Order;
import nunu.orderCount.domain.order.model.OptionOrderInfo;
import nunu.orderCount.domain.product.model.Product;
import nunu.orderCount.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

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

    @DisplayName("가장 오래된 주문 찾기")
    @Test
    void findTopByMemberAndOptionAndIsDoneIsFalseOrderByOrderDateTimeDesc(){
        Member testMember = createTestMember("email", "password");
        memberRepository.save(testMember);
        Product testProduct = createTestProduct("product", "url", "111", testMember);
        Option option1 = createTestOption(testProduct, "option1");
        Option option2 = createTestOption(testProduct, "option2");
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 12, 12, 12, 12);
        LocalDateTime dateTime2 = LocalDateTime.of(2023, 12, 13, 12, 12, 12);
        Order testOrder = createTestOrder(option1, testMember, "11", "11",dateTime);
        orderRepository.save(testOrder);
        Order testOrder2 = createTestOrder(option2, testMember, "12", "12",dateTime);
        orderRepository.save(testOrder2);
        Order testOrder3 = createTestOrder(option2, testMember, "123", "123",dateTime2);
        orderRepository.save(testOrder3);


        Order result = orderRepository.findTopByMemberAndOptionAndIsDoneIsFalseOrderByOrderDateTime(
                testMember, option2);
        assertThat(result.getOrderDateTime()).isEqualTo(dateTime);
    }

    @DisplayName("특정 기간 배송준비중 주문 조회")
    @Test
    void findByMemberAndIsDoneFalseAndOrderDateTimeBetween(){
        Member testMember = createTestMember("email", "password");
        memberRepository.save(testMember);
        Product testProduct = createTestProduct("product", "url", "111", testMember);
        Option option1 = createTestOption(testProduct, "option1");
        Option option2 = createTestOption(testProduct, "option2");
        Order testOrder = createTestOrder(option1, testMember, "11", "11",LocalDate.of(2023,12,12));
        orderRepository.save(testOrder);
        Order testOrder2 = createTestOrder(option2, testMember, "12", "12",LocalDate.of(2023,12,13));
        orderRepository.save(testOrder2);
        Order testOrder3 = createTestOrder(option1, testMember, "12", "15",LocalDate.of(2023,12,14));
        orderRepository.save(testOrder3);
        Order testOrder4 = createTestOrder(option1, testMember, "12", "16",LocalDate.of(2023,12,15));
        orderRepository.save(testOrder4);

        List<Order> optionOrderInfos = orderRepository.findByMemberAndIsDoneFalseAndOrderDateTimeBetween(
                testMember, LocalDateTime.of(2023, 12, 11,0,0,0),
                LocalDateTime.of(2023, 12, 14,0,0,0));
        assertThat(optionOrderInfos.size()).isEqualTo(3);

        optionOrderInfos = orderRepository.findByMemberAndIsDoneFalseAndOrderDateTimeBetween(
                testMember, LocalDateTime.of(2023, 12, 12,0,0,0),
                LocalDateTime.of(2023, 12, 13,0,0,0));
        assertThat(optionOrderInfos.size()).isEqualTo(2);

        optionOrderInfos = orderRepository.findByMemberAndIsDoneFalseAndOrderDateTimeBetween(
                testMember, LocalDateTime.of(2023, 12, 11,0,0,0),
                LocalDateTime.of(2023, 12, 15,0,0,0));
        assertThat(optionOrderInfos.size()).isEqualTo(4);
        optionOrderInfos = orderRepository.findByMemberAndIsDoneFalseAndOrderDateTimeBetween(
                testMember, LocalDateTime.of(2023, 12, 11,0,0,0),
                LocalDateTime.of(2023, 12, 14,23,59,59));
        assertThat(optionOrderInfos.size()).isEqualTo(3);
        optionOrderInfos = orderRepository.findByMemberAndIsDoneFalseAndOrderDateTimeBetween(
                testMember, LocalDateTime.of(2023, 12, 11,0,0,0),
                LocalDateTime.of(2023, 12, 14,23,59,59,500));
        assertThat(optionOrderInfos.size()).isEqualTo(3);
        optionOrderInfos = orderRepository.findByMemberAndIsDoneFalseAndOrderDateTimeBetween(
                testMember, LocalDateTime.of(2023, 12, 11,0,0,0),
                LocalDateTime.of(2023, 12, 15,0,0,0));
        assertThat(optionOrderInfos.size()).isEqualTo(4);
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
    private Order createTestOrder(Option option, Member member, String orderNumber, String orderItemNumber, LocalDateTime dateTime){
        return Order.builder()
                .option(option)
                .orderDateTime(dateTime)
                .orderNumber(orderNumber)
                .orderItemNumber(orderItemNumber)
                .quantity(2L)
                .member(member)
                .build();
    }
    private Order createTestOrder(Option option, Member member, String orderNumber, String orderItemNumber, LocalDate date){
        return Order.builder()
                .option(option)
                .orderDateTime(LocalDateTime.of(date, LocalTime.of(0,0,0)))
                .orderNumber(orderNumber)
                .orderItemNumber(orderItemNumber)
                .quantity(2L)
                .member(member)
                .build();
    }
}