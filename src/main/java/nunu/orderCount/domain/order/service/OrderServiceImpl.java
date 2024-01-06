package nunu.orderCount.domain.order.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.order.exception.InvalidZigzagTokenException;
import nunu.orderCount.domain.order.model.Order;
import nunu.orderCount.domain.order.model.OptionOrderInfo;
import nunu.orderCount.domain.order.model.OrderDtoInfo;
import nunu.orderCount.domain.order.model.OrderInfo;
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
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final ZigzagOrderService zigzagOrderService;

    /**
     * zigzag에서 새로운 order 정보를 받아옵니다.
     *
     * @return zigzag order 정보 list
     */
    @Override
    public List<ResponseZigzagOrderDto> getOrdersFromZigzag(MemberInfo memberInfo) {
        //zigzagOrderService 에서 order 정보 받아오기
        Optional<Order> latestOrder = orderRepository.findTopByMemberOrderByOrderDateTimeDesc(memberInfo.getMember());
        Integer startDate = calStartDate(latestOrder);
        Integer endDate = Integer.parseInt(String.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        List<ResponseZigzagOrderDto> zigzagOrderList = zigzagOrderService.zigzagOrderListRequester(
                memberInfo.getZigzagToken(), startDate, endDate);

        if (zigzagOrderList == null) {
            throw new InvalidZigzagTokenException();
        }

        return zigzagOrderList;
    }

    @Override
    public ResponseOrderUpdateDto orderUpdate(RequestOrderUpdateDto dto) {
        //배송준비중인 order list 만들기
        List<Order> orders = makeOrderList(dto.getOrderDtoInfos(), dto.getMemberInfo());

        //새로 저장할 order list 뽑기 -> orderRepository에 저장되어 있지 않은 order list
        List<Order> newOrders = extractNewOrders(orders, dto.getMemberInfo().getMember());

        //완료된 order 상태 변환
        List<Order> doneOrders = new ArrayList<>(orderRepository.findByMemberAndIsDoneFalse(
                dto.getMemberInfo().getMember()));
        doneOrders.removeAll(orders);

        doneOrders.stream().forEach(order -> {
            order.setDone();
        });

        orderRepository.saveAll(newOrders);
        orderRepository.saveAll(doneOrders);
        return new ResponseOrderUpdateDto(newOrders.size(), doneOrders.size());
    }

    @Override
    public ResponseFindOrdersByOptionDto findOrdersByOptionGroup(RequestFindOrdersDto dto) {
        List<Order> orders = orderRepository.findByMemberAndIsDoneFalse(dto.getMember());

        Map<Option, List<Order>> ordersByOption = orders.stream()
                .collect(Collectors.groupingBy(Order::getOption));

        List<OptionOrderInfo> optionOrderInfos = createOptionOrderInfos(ordersByOption);

        return new ResponseFindOrdersByOptionDto(optionOrderInfos.size(), optionOrderInfos);
    }

    @Override
    public ResponseFindOrdersByOptionDto findOrdersByOptionGroupAndDate(RequestFindOrdersByOptionGroupAndDateDto dto) {
        return null;
    }

    private List<Order> makeOrderList(List<OrderDtoInfo> orderDtoInfos, MemberInfo memberInfo){
        return orderDtoInfos.stream()
                .distinct()
                .map(orderDtoInfo -> {
                    Optional<Product> product = productRepository.findByZigzagProductIdAndMember(
                            orderDtoInfo.getProductId(), memberInfo.getMember());
                    if (product.isEmpty()) return null;
                    Optional<Option> option = optionRepository.findByProductAndName(product.get(),
                            orderDtoInfo.getOptionName());
                    if (option.isEmpty()) return null;

                    return Order.builder()
                            .option(option.get())
                            .orderDateTime(orderDtoInfo.getOrderDateTime())
                            .member(memberInfo.getMember())
                            .orderNumber(orderDtoInfo.getOrderNumber())
                            .orderItemNumber(orderDtoInfo.getOrderItemNumber())
                            .quantity(orderDtoInfo.getQuantity())
                            .build();
                })
                .filter(o -> o != null)
                .collect(Collectors.toList());
    }

    private List<Order> extractNewOrders(List<Order> orders, Member member){
        return orders.stream()
                .filter(order -> !orderRepository.existsByMemberAndOrderItemNumber(member, order.getOrderItemNumber()))
                .collect(Collectors.toList());
    }

    private Integer calStartDate(Optional<Order> latestOrder) {
        if(latestOrder.isPresent()) {
            log.info("last order is present");
            return Integer.parseInt(latestOrder.get().getOrderDateTime().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        }
        else{
            log.info("last order is null");
            return Integer.parseInt(String.valueOf(LocalDate.now().minusMonths(1L).format(DateTimeFormatter.ofPattern("yyyyMMdd"))));
        }
    }

    private List<OptionOrderInfo> createOptionOrderInfos(Map<Option, List<Order>> ordersByOption){
        return ordersByOption.entrySet().stream()
                .map(optionListEntry -> {
                    List<OrderInfo> orderInfos = optionListEntry.getValue().stream()
                            .map(order -> {
                                return OrderInfo.builder()
                                        .orderQuantity(order.getOrderId())
                                        .optionName(order.getOption().getName())
                                        .productName(order.getOption().getProduct().getName())
                                        .productImageUrl(order.getOption().getProduct().getImageUrl())
                                        .inventoryQuantity(order.getOption().getInventoryQuantity())
                                        .orderDateTime(order.getOrderDateTime())
                                        .build();
                            })
                            .collect(Collectors.toList());

                    LocalDateTime latestOrderDateTime = findLatestOrderDateTime(orderInfos);
                    return new OptionOrderInfo(optionListEntry.getKey(), orderInfos.size(), latestOrderDateTime, orderInfos);
                })
                .collect(Collectors.toList());
    }

    LocalDateTime findLatestOrderDateTime(List<OrderInfo> orderInfos) {
        return orderInfos.stream().sorted(Comparator.comparing(OrderInfo::getOrderDateTime))
                .map(OrderInfo::getOrderDateTime)
                .findFirst().get();
    }
}
