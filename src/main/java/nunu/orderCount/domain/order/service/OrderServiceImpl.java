package nunu.orderCount.domain.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.member.repository.MemberRepository;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.order.exception.InvalidZigzagTokenException;
import nunu.orderCount.domain.order.exception.ZigzagRequestFailException;
import nunu.orderCount.domain.order.model.Order;
import nunu.orderCount.domain.order.model.OrderDtoInfo;
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
    private final ZigzagOrderService zigzagOrderService;

    /**
     * zigzag에서 새로운 order 정보를 받아옵니다.
     *
     * @return zigzag order 정보 list
     */
    public List<ResponseZigzagOrderDto> getOrdersFromZigzag(MemberInfo memberInfo) {
        //zigzagOrderService 에서 order 정보 받아오기
        Optional<Order> latestOrder = orderRepository.findTopByMemberOrderByDatePaidDesc(memberInfo.getMember());
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
//        //배송준비중 아닌(완료된) order의 isDone true로 변환
//        List<Order> doneOrderList = new ArrayList<>(orderRepository.findByMemberAndIsDoneFalse(member));
//        List<Order> orderList = saveOrderList.stream().distinct().collect(Collectors.toList());
//        doneOrderList.removeAll(orderList);
//        orderList.removeAll(doneOrderList);
//
//        doneOrderList.forEach(o -> o.setDone());
//
//        orderRepository.saveAll(orderList); //새로 추가
//        orderRepository.saveAll(doneOrderList); //완료 된 것 상태변화
//
//        return new ResponseOrderUpdateDto(saveOrderList.size(), doneOrderList.size());
        return null;
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
