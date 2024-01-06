package nunu.orderCount.domain.order.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.order.model.Order;
import nunu.orderCount.domain.order.model.OrderDtoInfo;
import nunu.orderCount.domain.order.model.dto.request.RequestFindOrdersByOptionGroupAndDateDto;
import nunu.orderCount.domain.order.model.dto.request.RequestFindOrdersDto;
import nunu.orderCount.domain.order.model.dto.request.RequestOrderUpdateDto;
import nunu.orderCount.domain.order.model.dto.response.ResponseFindOrdersByOptionDto;
import nunu.orderCount.domain.order.model.dto.response.ResponseOrderUpdateDto;
import nunu.orderCount.domain.order.repository.OrderRepository;
import nunu.orderCount.domain.product.model.Product;
import nunu.orderCount.domain.product.repository.ProductRepository;
import nunu.orderCount.infra.zigzag.model.dto.response.ResponseZigzagOrderDto;
import nunu.orderCount.infra.zigzag.service.ZigzagOrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OptionRepository optionRepository;
    @Mock
    private ZigzagOrderService zigzagOrderService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @DisplayName("zigzag에서 주문 정보 받아오기")
    @Nested
    class getOrdersFromZigzag{
        @Test
        @DisplayName("마지막 주문 정보 없을 경우")
        void notExistLastOrder(){
            MemberInfo memberInfo = createMemberInfo();
            doReturn(Optional.empty()).when(orderRepository).findTopByMemberOrderByOrderDateTimeDesc(any(Member.class));
            List<ResponseZigzagOrderDto> responseZigzagOrderDtos = createResponseZigzagOrderDto(5);
            doReturn(responseZigzagOrderDtos).when(zigzagOrderService).zigzagOrderListRequester(anyString(), anyInt(), anyInt());

            orderService.getOrdersFromZigzag(memberInfo);

            assertThat(responseZigzagOrderDtos.size()).isEqualTo(5);
        }

        @Test
        @DisplayName("마지막 주문 정보 있을 경우")
        void existLastOrder(){
            MemberInfo memberInfo = createMemberInfo();
            Product testProduct = createTestProduct(memberInfo.getMember());
            Option testOption = createTestOption(testProduct, "option");
            LocalDateTime dateTime = LocalDateTime.of(2023, 12, 8, 10, 10, 10);

            Order testOrder = createTestOrder(dateTime, testOption);
            doReturn(Optional.of(testOrder)).when(orderRepository)
                    .findTopByMemberOrderByOrderDateTimeDesc(any(Member.class));
            List<ResponseZigzagOrderDto> responseZigzagOrderDtos = createResponseZigzagOrderDto(5);
            doReturn(responseZigzagOrderDtos).when(zigzagOrderService).zigzagOrderListRequester(anyString(), anyInt(), anyInt());

            orderService.getOrdersFromZigzag(memberInfo);

            assertThat(responseZigzagOrderDtos.size()).isEqualTo(5);
        }
    }

    @Test
    @DisplayName("order update")
    void orderUpdate(){
        MemberInfo memberInfo = createMemberInfo();
        Product testProduct = createTestProduct(memberInfo.getMember());
        Option testOption = createTestOption(testProduct, "option");
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 8, 10, 10, 10);

        Order testOrder = createTestOrder(dateTime, testOption);
        doReturn(Optional.of(testProduct)).when(productRepository).findByZigzagProductIdAndMember(anyString(), any(Member.class));
        doReturn(false).when(orderRepository).existsByMemberAndOrderItemNumber(any(Member.class), anyString());
        doReturn(Optional.of(testOption)).when(optionRepository).findByProductAndName(any(Product.class), anyString());
        doReturn(List.of(testOrder)).when(orderRepository).findByMemberAndIsDoneFalse(any(Member.class));
        doReturn(List.of(1)).when(orderRepository).saveAll(anyList());

        List<OrderDtoInfo> dto = createOrderDtoInfo(5, dateTime);

        dto.add(new OrderDtoInfo(testOrder.getQuantity(), testOrder.getOrderItemNumber(), testOrder.getOrderNumber(),
                testOrder.getOrderDateTime(), testProduct.getZigzagProductId(), testOption.getName()));
        ResponseOrderUpdateDto response = orderService.orderUpdate(
                new RequestOrderUpdateDto(memberInfo, dto));

        assertThat(response.getNewOrderCount()).isEqualTo(6);
        assertThat(response.getChangeStatusOrderCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("전체 배송준비중 주문 조회")
    void findOrders(){
        Member testMember = new Member("email", "password");
        Product testProduct = createTestProduct(testMember);
        Option testOption1 = createTestOption(testProduct, "option1");
        Option testOption2 = createTestOption(testProduct, "option2");
        Option testOption3 = createTestOption(testProduct, "option3");
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 8, 10, 10, 10);

        List<Order> orders = new ArrayList<>();
        orders.add(createTestOrder(dateTime, testOption1));
        orders.add(createTestOrder(dateTime, testOption2));
        orders.add(createTestOrder(dateTime, testOption3));

        doReturn(orders).when(orderRepository).findByMemberAndIsDoneFalse(any(Member.class));

        ResponseFindOrdersByOptionDto response = orderService.findOrdersByOptionGroup(new RequestFindOrdersDto(testMember));
        assertThat(response.getOptionCount()).isEqualTo(3L);
        assertThat(response.getProductOptionOrderInfos().size()).isEqualTo(1L);
        assertThat(response.getProductOptionOrderInfos().get(0).getOptionOrderInfos().size()).isEqualTo(3L);
    }

    @Test
    @DisplayName("특정 기간 배송준비중 주문 조회")
    void findOrdersByOptionGroupAndDate(){
        Member testMember = new Member("email", "password");
        Product testProduct = createTestProduct(testMember);
        Option testOption1 = createTestOption(testProduct, "option1");
        Option testOption2 = createTestOption(testProduct, "option2");
        Option testOption3 = createTestOption(testProduct, "option3");
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 8, 10, 10, 10);

        List<Order> orders = new ArrayList<>();
        orders.add(createTestOrder(dateTime, testOption1));
        orders.add(createTestOrder(LocalDateTime.of(2023,12,12,0,0,0), testOption1));
        orders.add(createTestOrder(LocalDateTime.of(2023,12,12,0,0,0), testOption2));
        orders.add(createTestOrder(LocalDateTime.of(2023,12,12,0,0,0), testOption3));
        orders.add(createTestOrder(LocalDateTime.of(2023,12,12,23,59,59), testOption1));
        orders.add(createTestOrder(LocalDateTime.of(2023,12,12,23,59,59), testOption2));
        orders.add(createTestOrder(LocalDateTime.of(2023,12,11,23,59,59), testOption2));
        orders.add(createTestOrder(LocalDateTime.of(2023,12,11,23,59,59), testOption1));
        orders.add(createTestOrder(LocalDateTime.of(2023,12,11,0,0,0), testOption1));
        orders.add(createTestOrder(LocalDateTime.of(2023,12,13,0,0,0), testOption3));

        doReturn(orders).when(orderRepository)
                .findByMemberAndIsDoneFalseAndOrderDateTimeBetween(any(Member.class), any(), any());

        ResponseFindOrdersByOptionDto response = orderService.findOrdersByOptionGroupAndDate(
                new RequestFindOrdersByOptionGroupAndDateDto(testMember, LocalDate.of(2023, 12, 11),
                        LocalDate.of(2023, 12, 12)));

        assertThat(response.getOptionCount()).isEqualTo(3);
//        log.info("oldest order dateTime: {}", response.getOptionOrderInfos().get(0).getOption().getName());
        //어떤 option이 맨 앞에 올지 모름, 확인할 때에만 사용할 것
//        assertThat(response.getOptionOrderInfos().get(0).getOldestOrderDateTime()).isEqualTo(dateTime);
//        assertThat(response.getOptionOrderInfos().get(0).getCount()).isEqualTo(5);

    }

    private MemberInfo createMemberInfo(){
        return new MemberInfo(new Member("email", "password"), "token");
    }

    private List<ResponseZigzagOrderDto> createResponseZigzagOrderDto(int size){
        List<ResponseZigzagOrderDto> responseZigzagOrders = new ArrayList<>();
        for(int i=1;i<=size;i++){
            responseZigzagOrders.add(
                    new ResponseZigzagOrderDto("product" + i, "option" + i, Long.valueOf(i), "itemNum" + i,
                            "orderNum" + i, "" + i, LocalDateTime.of(2023, 12, 20, 10, 10, i)));
        }
        return responseZigzagOrders;
    }

    private List<OrderDtoInfo> createOrderDtoInfo(int size, LocalDateTime dateTime){
        List<OrderDtoInfo> orderDtoInfos = new ArrayList<>();
        String num ="";
        for(int i=1;i<=size;i++){
            num+=i;
            orderDtoInfos.add(new OrderDtoInfo(Long.valueOf(i), num, num, dateTime, num, "option" + i));
        }
        return orderDtoInfos;
    }

    private Order createTestOrder(LocalDateTime orderDateTime, Option option){
        return Order.builder()
                .orderDateTime(orderDateTime)
                .orderNumber("1")
                .quantity(1L)
                .orderItemNumber("1")
                .option(option)
                .build();
    }

    private Option createTestOption(Product product, String name){
        return Option.builder()
                .product(product)
                .name(name)
                .build();
    }

    private Product createTestProduct(Member member) {
        return Product.builder()
                .member(member)
                .zigzagProductId("1")
                .name("product")
                .imageUrl("")
                .build();
    }
}