package nunu.orderCount.infra.zigzag.model.dto.request;

import lombok.Getter;
import java.util.Arrays;
import java.util.List;

@Getter
public class RequestZigzagOrderDto {
    private final String query;
    private final Variables variables;

    public RequestZigzagOrderDto(String query, Integer gte, Integer lte) {
        this.query = query;
        this.variables = new Variables(gte, lte);
    }
}

@Getter
class Variables {
    private final List<String> status_list;
    private final boolean only_late_shipment;
    private final boolean only_withdrawn_cancel_request;
    private final Integer page;
    private final Integer items_per_page;
    private final String order_by;
    private final Integer date_marked_awaiting_shipment_ymd_gte;
    private final Integer date_marked_awaiting_shipment_ymd_lte;

    public Variables(Integer date_marked_awaiting_shipment_ymd_gte, Integer date_marked_awaiting_shipment_ymd_lte) {
        this.status_list = Arrays.asList("AWAITING_SHIPMENT");
        this.only_late_shipment = false;
        this.only_withdrawn_cancel_request = false;
        this.page = 1;
        this.items_per_page = 100;
        this.order_by = "DATE_PAID_DESC";
        this.date_marked_awaiting_shipment_ymd_gte = date_marked_awaiting_shipment_ymd_gte;
        this.date_marked_awaiting_shipment_ymd_lte = date_marked_awaiting_shipment_ymd_lte;
    }
}
