package nunu.orderCount.domain.order.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseOrderUpdateDto {
    private final Integer newOrderCount;
    private final Integer changeStatusOrderCount;
}
