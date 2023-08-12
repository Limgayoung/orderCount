package nunu.orderCount.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.order.exception.InvalidZigzagTokenException;
import nunu.orderCount.domain.order.exception.NotExistMemberException;
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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private final String REDIS_ZIGZAG_TOKEN = "zigzag-token:";

    @Override
    public ResponseOrderUpdateDto orderUpdate(RequestOrderUpdateDto dto) {
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(() -> new NotExistMemberException("존재하지 않는 회원입니다."));
        Optional<Order> latestOrder = orderRepository.findTopByMemberOrderByDatePaidDesc(member);
        String zigzagToken = redisUtil.getData(REDIS_ZIGZAG_TOKEN + member.getMemberId());
        if (zigzagToken == null) {
            //zigzag login 필요 예외 메시지
            throw new InvalidZigzagTokenException("zigzag token refresh가 필요합니다.");
        }

        Integer startDate;
        Integer endDate = Integer.parseInt(String.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));

        if(latestOrder.isPresent()) {
            startDate = Integer.parseInt(String.valueOf(latestOrder.get().getDatePaid()));
        }
        else{
            startDate = Integer.parseInt(String.valueOf(LocalDate.now().minusMonths(2L)));
        }
        List<ResponseZigzagOrderDto> zigzagOrderList = zigzagOrderService.zigzagOrderListRequester(zigzagToken, startDate, endDate);
        if (zigzagOrderList == null) {
            //todo: 예외처리
        }

        List<String> imageRequestList = new ArrayList<>();
        List<Product> saveProductList = new ArrayList<>();
        List<Option> saveOptionList = new ArrayList<>();
        List<Order> saveOrderList = new ArrayList<>();
        for (ResponseZigzagOrderDto zigzagOrder : zigzagOrderList) {
            Optional<Product> orderProduct = productRepository.findByZigzagProductId(zigzagOrder.getProductId());
            Product product = Product.builder()
                    .member(member)
                    .zigzagProductId(zigzagOrder.getProductId())
                    .name(zigzagOrder.getProductName())
                    .build();
            Option option = Option.builder()
                    .name(zigzagOrder.getOption())
                    .product(product)
                    .build();
            Order order = Order.of(zigzagOrder, member, option);

            if (orderProduct.isEmpty()) { //product(zigzag image 요청), option save
                saveProductList.add(product);
                saveOptionList.add(option);
                imageRequestList.add(zigzagOrder.getProductId());
            }
            else{
                Optional<Option> orderOption = optionRepository.findByProductAndName(orderProduct.get(), zigzagOrder.getOption());
                if (orderOption.isEmpty()) { //option save
                    saveOptionList.add(option);
                }
            }
            saveOrderList.add(order);
        }
        if (!imageRequestList.isEmpty()) {
            Map<String, String> imageUrlMap = zigzagProductService.ZigzagProductImagesUrlRequester(zigzagToken, imageRequestList);
            saveProductList.forEach(p -> p.setImageUrl(imageUrlMap.get(p.getZigzagProductId())));
        }
        productRepository.saveAll(saveProductList);
        optionRepository.saveAll(saveOptionList);
        orderRepository.saveAll(saveOrderList);

        return new ResponseOrderUpdateDto(saveOrderList.stream().count());
    }
}
