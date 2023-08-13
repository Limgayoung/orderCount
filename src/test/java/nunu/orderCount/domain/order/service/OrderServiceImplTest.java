package nunu.orderCount.domain.order.service;

import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.order.exception.InvalidZigzagTokenException;
import nunu.orderCount.domain.order.exception.ZigzagRequestFailException;
import nunu.orderCount.domain.order.model.Order;
import nunu.orderCount.domain.order.model.dto.request.RequestOrderUpdateDto;
import nunu.orderCount.domain.order.model.dto.response.ResponseOrderUpdateDto;
import nunu.orderCount.domain.order.repository.OrderRepository;
import nunu.orderCount.domain.product.model.Product;
import nunu.orderCount.domain.product.repository.ProductRepository;
import nunu.orderCount.global.util.RedisUtil;
import nunu.orderCount.infra.zigzag.model.dto.response.ResponseZigzagOrderDto;
import nunu.orderCount.infra.zigzag.service.ZigzagOrderService;
import nunu.orderCount.infra.zigzag.service.ZigzagProductService;
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
    private RedisUtil redisUtil;
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
    @Mock
    private ZigzagProductService zigzagProductService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @DisplayName("주문 업데이트")
    @Nested
    class orderUpdate {
        @DisplayName("성공 - 모든 상품, 옵션 존재할 경우")
        @Test
        void orderUpdateTest(){
            //given
            RequestOrderUpdateDto dto = new RequestOrderUpdateDto(1L);
            Member testMember = createTestMember("email", "password", 1L);
            Product testProduct = createTestProduct("pro", "option", "imageurl",  1L, testMember);
            Option testOption = createTestOption("name", 2L, testProduct,1L);
            Order testOrder = createTestOrder("itemNumber", 20230811L, "orderNumber", testMember, 2L, testOption, 1L);

            doReturn(Optional.of(testMember)).when(memberRepository).findById(anyLong());
            doReturn("zigzagToken").when(redisUtil).getData(anyString());
            // orderRepository 에서 last order 찾기
            doReturn(Optional.of(testOrder)).when(orderRepository).findTopByMemberOrderByDatePaidDesc(any(Member.class));

            // zigzagOrderService 에서 주문 정보 받아오기
            doReturn(List.of(new ResponseZigzagOrderDto("","",2L,"","","",20230811L))).when(zigzagOrderService).zigzagOrderListRequester(anyString(), anyInt(), anyInt());
                //실패 시 예외 throw -> 프론트에서 비밀번호 입력해서 zigzag login 요청 후 재요청
            // list의 각 원소가 option에 이미 있는지 확인, 없으면 저장
            doReturn(Optional.of(testProduct)).when(productRepository).findByZigzagProductId(anyString());
            doReturn(Optional.of(testOption)).when(optionRepository).findByProductAndName(any(Product.class), anyString());

            // order 저장
            doReturn(List.of(testProduct)).when(productRepository).saveAll(anyList());
            doReturn(List.of(testOption)).when(optionRepository).saveAll(anyList());
            doReturn(List.of(testOrder)).when(orderRepository).saveAll(anyList());

            //when
            ResponseOrderUpdateDto response = orderService.orderUpdate(dto);

            //then
            assertThat(response.getUpdateOrderCount()).isEqualTo(1L);
        }

        @DisplayName("성공 - 상품 존재하지 않을 경우")
        @Test
        void orderUpdateNotExistProductTest(){
            //given
            RequestOrderUpdateDto dto = new RequestOrderUpdateDto(1L);
            Member testMember = createTestMember("email", "password", 1L);
            Product testProduct = createTestProduct("pro", "option", "imageurl",  1L, testMember);
            Option testOption = createTestOption("name", 2L, testProduct,1L);
            Order testOrder = createTestOrder("itemNumber", 20230811L, "orderNumber", testMember, 2L, testOption, 1L);

            doReturn(Optional.of(testMember)).when(memberRepository).findById(anyLong());
            doReturn("zigzagToken").when(redisUtil).getData(anyString());
            // orderRepository 에서 last order 찾기
            doReturn(Optional.of(testOrder)).when(orderRepository).findTopByMemberOrderByDatePaidDesc(any(Member.class));

            doReturn(List.of(new ResponseZigzagOrderDto("","",2L,"","","",20230811L))).when(zigzagOrderService).zigzagOrderListRequester(anyString(), anyInt(), anyInt());

            doReturn(Optional.empty()).when(productRepository).findByZigzagProductId(anyString());
            doReturn(Map.of("productId","imageurl")).when(zigzagProductService).ZigzagProductImagesUrlRequester(anyString(), anyList());

            // order 저장
            doReturn(List.of(testProduct)).when(productRepository).saveAll(anyList());
            doReturn(List.of(testOption)).when(optionRepository).saveAll(anyList());
            doReturn(List.of(testOrder)).when(orderRepository).saveAll(anyList());

            //when
            ResponseOrderUpdateDto response = orderService.orderUpdate(dto);

            //then
            assertThat(response.getUpdateOrderCount()).isEqualTo(1L);
        }

        @DisplayName("성공 - 옵션 존재하지 않을 경우")
        @Test
        void orderUpdateNotExistOptionTest(){
            //given
            RequestOrderUpdateDto dto = new RequestOrderUpdateDto(1L);
            Member testMember = createTestMember("email", "password", 1L);
            Product testProduct = createTestProduct("pro", "option", "imageurl",  1L, testMember);
            Option testOption = createTestOption("name", 2L, testProduct,1L);
            Order testOrder = createTestOrder("itemNumber", 20230811L, "orderNumber", testMember, 2L, testOption, 1L);

            doReturn(Optional.of(testMember)).when(memberRepository).findById(anyLong());
            doReturn("zigzagToken").when(redisUtil).getData(anyString());
            // orderRepository 에서 last order 찾기
            doReturn(Optional.of(testOrder)).when(orderRepository).findTopByMemberOrderByDatePaidDesc(any(Member.class));

            // zigzagOrderService 에서 주문 정보 받아오기
            doReturn(List.of(new ResponseZigzagOrderDto("","",2L,"","","",20230811L))).when(zigzagOrderService).zigzagOrderListRequester(anyString(), anyInt(), anyInt());
            doReturn(Optional.of(testProduct)).when(productRepository).findByZigzagProductId(anyString());
            doReturn(Optional.empty()).when(optionRepository).findByProductAndName(any(Product.class), anyString());

            // order 저장
            doReturn(List.of(testProduct)).when(productRepository).saveAll(anyList());
            doReturn(List.of(testOption)).when(optionRepository).saveAll(anyList());
            doReturn(List.of(testOrder)).when(orderRepository).saveAll(anyList());

            //when
            ResponseOrderUpdateDto response = orderService.orderUpdate(dto);

            //then
            assertThat(response.getUpdateOrderCount()).isEqualTo(1L);
        }

        @DisplayName("실패 - zigzag token 없음")
        @Test
        void orderUpdateFailNullOfZigzagTokenTest(){
            //given
            RequestOrderUpdateDto dto = new RequestOrderUpdateDto(1L);
            Member testMember = createTestMember("email", "password", 1L);
            Product testProduct = createTestProduct("pro", "option", "imageurl",  1L, testMember);
            Option testOption = createTestOption("name", 2L, testProduct,1L);
            Order testOrder = createTestOrder("itemNumber", 20230811L, "orderNumber", testMember, 2L, testOption, 1L);

            doReturn(Optional.of(testMember)).when(memberRepository).findById(anyLong());
            doReturn(null).when(redisUtil).getData(anyString());

            //when, then
            assertThatThrownBy(()->orderService.orderUpdate(dto))
                    .isInstanceOf(InvalidZigzagTokenException.class);;
        }

        @DisplayName("실패 - zigzag order request 실패")
        @Test
        void orderUpdateFailRequestZigzagOrderTest(){
            //given
            RequestOrderUpdateDto dto = new RequestOrderUpdateDto(1L);
            Member testMember = createTestMember("email", "password", 1L);
            Product testProduct = createTestProduct("pro", "option", "imageurl",  1L, testMember);
            Option testOption = createTestOption("name", 2L, testProduct,1L);
            Order testOrder = createTestOrder("itemNumber", 20230811L, "orderNumber", testMember, 2L, testOption, 1L);

            doReturn(Optional.of(testMember)).when(memberRepository).findById(anyLong());
            doReturn("zigzagToken").when(redisUtil).getData(anyString());
            // orderRepository 에서 last order 찾기
            doReturn(Optional.of(testOrder)).when(orderRepository).findTopByMemberOrderByDatePaidDesc(any(Member.class));
            doReturn(null).when(zigzagOrderService).zigzagOrderListRequester(anyString(), anyInt(), anyInt());
            
            // zigzagOrderService 에서 주문 정보 받아오기 실패
            //when, then
            assertThatThrownBy(()->orderService.orderUpdate(dto))
                    .isInstanceOf(ZigzagRequestFailException.class);;
        }
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