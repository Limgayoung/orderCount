package nunu.orderCount.infra.zigzag.model.dto.request;

import lombok.Getter;
import java.util.Arrays;
import java.util.List;

@Getter
public class RequestZigzagOrderDto {
    private final String query;
    private final OrderVariables variables;

    public RequestZigzagOrderDto(String query, Integer gte, Integer lte) {
        this.query = query;
        this.variables = new OrderVariables(gte, lte);
    }
}

