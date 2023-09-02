package nunu.orderCount.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.order.exception.InvalidZigzagTokenException;
import nunu.orderCount.domain.order.exception.NotExistMemberException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final RedisUtil redisUtil;
    private final ZigzagOrderService zigzagOrderService;
    private final ZigzagProductService zigzagProductService;
    @Value("${spring.redis.key.zigzag-token}")
    private String REDIS_ZIGZAG_TOKEN;

    @Override
    public ResponseOrderUpdateDto orderUpdate(RequestOrderUpdateDto dto) {
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(() -> new NotExistMemberException("존재하지 않는 회원입니다."));
        String zigzagToken = redisUtil.getData(REDIS_ZIGZAG_TOKEN + member.getMemberId());
        if (zigzagToken == null) {
            throw new InvalidZigzagTokenException("zigzag token refresh가 필요합니다.");
        }

        Optional<Order> latestOrder = orderRepository.findTopByMemberOrderByDatePaidDesc(member);
        Integer startDate = calStartDate(latestOrder);
        Integer endDate = Integer.parseInt(String.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));

        List<ResponseZigzagOrderDto> zigzagOrderList = zigzagOrderService.zigzagOrderListRequester(zigzagToken, startDate, endDate);

        if (zigzagOrderList == null) {
            throw new ZigzagRequestFailException("zigzag 요청에 실패했습니다. zigzag token refresh가 필요합니다.");
        }

        List<String> imageRequestList = new ArrayList<>();
        List<Product> saveProductList = new ArrayList<>();
        List<Option> saveOptionList = new ArrayList<>();
        List<Order> saveOrderList = new ArrayList<>();
        for (ResponseZigzagOrderDto zigzagOrder : zigzagOrderList) {
            Optional<Product> orderProduct = productRepository.findByZigzagProductId(zigzagOrder.getProductId());
            Product product;
            Option option;
            if (orderProduct.isEmpty()) { //product(zigzag image 요청), option save
                product = Product.builder()
                        .member(member)
                        .zigzagProductId(zigzagOrder.getProductId())
                        .name(zigzagOrder.getProductName())
                        .build();
                option = Option.builder()
                        .name(zigzagOrder.getOption())
                        .product(product)
                        .inventoryQuantity(0)
                        .build();
                saveProductList.add(product);
                saveOptionList.add(option);
                imageRequestList.add(zigzagOrder.getProductId());
            }
            else{
                Optional<Option> orderOption = optionRepository.findByProductAndName(orderProduct.get(), zigzagOrder.getOption());
                if (orderOption.isEmpty()) { //option save
                    option = Option.builder()
                            .name(zigzagOrder.getOption())
                            .product(orderProduct.get())
                            .inventoryQuantity(0)
                            .build();
                    saveOptionList.add(option);
                }
                else{
                    option = orderOption.get();
                }
            }
            Order order = Order.of(zigzagOrder, member, option);
            saveOrderList.add(order);
        }
        if (!imageRequestList.isEmpty()) {
            Map<String, String> imageUrlMap = zigzagProductService.ZigzagProductImagesUrlRequester(zigzagToken, imageRequestList);
            saveProductList.forEach(p -> p.setImageUrl(imageUrlMap.get(p.getZigzagProductId())));
        }

        List<Product> productList = saveProductList.stream().distinct().collect(Collectors.toList());

        List<Option> optionList = saveOptionList.stream().distinct().collect(Collectors.toList());

        productRepository.saveAll(productList);

        //todo: optional.get() -> error throw 변경 필요
        optionList.forEach(option -> option.setProduct(productRepository.findByZigzagProductId(option.getProduct().getZigzagProductId()).get()));

        optionRepository.saveAll(optionList);
        
        //배송준비중 아닌(완료된) order의 isDone true로 변환
        List<Order> doneOrderList = new ArrayList<>(orderRepository.findByMemberAndIsDoneFalse(member));
        List<Order> orderList = saveOrderList.stream().distinct().collect(Collectors.toList());
        doneOrderList.removeAll(orderList);
        orderList.removeAll(doneOrderList);

        doneOrderList.forEach(o -> o.setDone());

        orderRepository.saveAll(orderList); //새로 추가
        orderRepository.saveAll(doneOrderList); //완료 된 것 상태변화

        return new ResponseOrderUpdateDto(saveOrderList.size(), doneOrderList.size());
    }

    private Integer calStartDate(Optional<Order> latestOrder) {
        if(latestOrder.isPresent()) {
            log.info("last order is present");
            return Integer.parseInt(String.valueOf(latestOrder.get().getDatePaid()));
        }
        else{
            log.info("last order is null");
            return Integer.parseInt(String.valueOf(LocalDate.now().minusMonths(1L).format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        }
    }
}
