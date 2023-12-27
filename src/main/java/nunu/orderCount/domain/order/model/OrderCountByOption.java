package nunu.orderCount.domain.order.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nunu.orderCount.domain.option.model.Option;

@Getter
@RequiredArgsConstructor
public class OrderCountByOption {
    private final Order order;
    private final Option option;
    private final Long count;
}
