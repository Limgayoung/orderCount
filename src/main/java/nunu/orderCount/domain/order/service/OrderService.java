package nunu.orderCount.domain.order.service;

import java.util.List;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.order.model.dto.request.RequestFindOrdersByDateDto;
import nunu.orderCount.domain.order.model.dto.request.RequestFindOrdersDto;
import nunu.orderCount.domain.order.model.dto.request.RequestOrderUpdateDto;
import nunu.orderCount.domain.order.model.dto.response.ResponseOrderUpdateDto;
import nunu.orderCount.domain.order.model.dto.response.ResponseFindOrdersByOptionDto;
import nunu.orderCount.infra.zigzag.model.dto.response.ResponseZigzagOrderDto;

public interface OrderService {
    //zigzag에서 상품 받아오기
    public List<ResponseZigzagOrderDto> getOrdersFromZigzag(MemberInfo memberInfo);

    // 주문 update (상품의 last order ~ now 인 주문 update)
    public ResponseOrderUpdateDto orderUpdate(RequestOrderUpdateDto dto);

    // 주문 개수 조회 - 모든 배송준비중인 주문 조회
    public ResponseFindOrdersByOptionDto findOrdersByOptionGroup(RequestFindOrdersDto dto);
    //주문 개수 조회 - 특정 기간 배송준비중인 주문 조회
    public ResponseFindOrdersByOptionDto findOrdersByDate(RequestFindOrdersByDateDto dto);

    // 주문 개수 조회 - 기간, 상품 (정렬 조건 (오래된 순, 상품 이름순, 등등))

    // 주문 list 조회 - 상품
    // 주문 조회
}
