package nunu.orderCount.domain.order.service;

import java.util.ArrayList;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
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
            doReturn(Optional.empty()).when(orderRepository).findTopByMemberOrderByDatePaidDesc(any(Member.class));
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
            Order testOrder = createTestOrder(20231205L, testOption);
            doReturn(Optional.of(testOrder)).when(orderRepository)
                    .findTopByMemberOrderByDatePaidDesc(any(Member.class));
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

        doReturn(Optional.of(testProduct)).when(productRepository).findByZigzagProductIdAndMember(anyString(), any(Member.class));
        doReturn(false).when(orderRepository).existsByMemberAndOrderItemNumber(any(Member.class), anyString());
        doReturn(true).when(optionRepository).existsByProductAndName(any(Product.class), anyString());
        doReturn(Optional.of(testOption)).when(optionRepository).findByProductAndName(any(Product.class), anyString());

        List<OrderDtoInfo> dto = createOrderDtoInfo(5, 20231205L);
        ResponseOrderUpdateDto response = orderService.orderUpdate(
                new RequestOrderUpdateDto(memberInfo, dto));
        
        assertThat(response.getNewOrderCount()).isEqualTo(5);
    }

    private MemberInfo createMemberInfo(){
        return new MemberInfo(new Member("email", "password"), "token");
    }

    private List<ResponseZigzagOrderDto> createResponseZigzagOrderDto(int size){
        List<ResponseZigzagOrderDto> responseZigzagOrders = new ArrayList<>();
        for(int i=1;i<=size;i++){
            responseZigzagOrders.add(
                    new ResponseZigzagOrderDto("product" + i, "option" + i, Long.valueOf(i), "itemNum" + i,
                            "orderNum" + i, "" + i, Long.valueOf(i)));
        }
        return responseZigzagOrders;
    }

    private List<OrderDtoInfo> createOrderDtoInfo(int size, Long date){
        List<OrderDtoInfo> orderDtoInfos = new ArrayList<>();
        String num ="";
        for(int i=1;i<=size;i++){
            num+=i;
            orderDtoInfos.add(new OrderDtoInfo(Long.valueOf(i), num, num, date, num, "option" + i));
        }
        return orderDtoInfos;
    }

    private Order createTestOrder(long datePaid, Option option){
        return Order.builder()
                .datePaid(datePaid)
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
                .inventoryQuantity(1)
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

    private Order createTestOrder(String itemNumber, Long datePaid, String orderNumber, Member member, Long quantity, Option option, Long orderId){
        Order testOrder = Order.builder()
                .orderItemNumber(itemNumber)
                .datePaid(datePaid)
                .orderNumber(orderNumber)
                .member(member)
                .option(option)
                .quantity(quantity)
                .build();
        ReflectionTestUtils.setField(
                testOrder,
                "orderId",
                orderId,
                Long.class
        );
        return testOrder;
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

    private Option createTestOption(String name, Long quantity, Product product, Long optionId){
        Option testOption = Option.builder()
                .product(product)
                .name("option")
                .inventoryQuantity(2)
                .build();

        ReflectionTestUtils.setField(
                testOption,
                "optionId",
                optionId,
                Long.class
        );
        return testOption;
    }
}