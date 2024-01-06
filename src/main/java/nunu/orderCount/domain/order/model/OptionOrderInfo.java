package nunu.orderCount.domain.order.model;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nunu.orderCount.domain.option.model.Option;

@Getter
@RequiredArgsConstructor
public class OptionOrderInfo {
    private final Option option;
    private final Integer count;
    private final List<OrderInfo> orderInfos;
}
