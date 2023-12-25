package nunu.orderCount.domain.order.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.order.model.Order;
import nunu.orderCount.domain.order.model.OrderDtoInfo;
import nunu.orderCount.domain.order.model.dto.request.RequestOrderUpdateDto;
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
            Option testOption = createTestOption(testProduct);
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
        Option testOption = createTestOption(testProduct);
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
    @DisplayName("기간별 상품 조회")
    void findByDate(){
        log.info("현재 시각: {}", System.currentTimeMillis());
        log.info("시간: {}",
                LocalDateTime.ofInstant(Instant.ofEpochMilli(1703155817585L), TimeZone.getDefault().toZoneId()));

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

    private Option createTestOption(Product product){
        return Option.builder()
                .product(product)
                .name("option")
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