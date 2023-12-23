package nunu.orderCount.infra.zigzag.model.dto.request;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class RequestZigzagProductInfoDto {
    private final String query;
    private final ProductVariables variables;

    public RequestZigzagProductInfoDto(String query, List<String> productIdList) {
        this.query = query;
        this.variables = new ProductVariables(productIdList);
    }
}

@Getter
class ProductVariables {
    private final ProductInput input;

    public ProductVariables(List<String> productIdList) {
        this.input = new ProductInput(productIdList);
    }
}

@Getter
class ProductInput{

    private final List<String> id_list;

    public ProductInput(List<String> productIdList) {
        this.id_list = productIdList;
    }
}