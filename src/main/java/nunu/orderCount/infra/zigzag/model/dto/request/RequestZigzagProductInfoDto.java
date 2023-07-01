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
    private final String display_status;
    private final String product_type;
    private final String entry_type;
    private final String suspend_status;
    private final String quality_status;
    private final Long skip_count;
    private final Long limit_count;
    private final String product_code_list;
    private final String name;
    private final List<String> id_list;
    private final String external_product_ids;
    private final String brand_id_list;
    private final String preorder;
    private final String trait_type_list;
    private final String penalty_status_list;
    private final List<String> sales_status_list;
    private final Long date_created_gte;
    private final Long date_created_lte;

    public ProductInput(List<String> productIdList) {
        this.display_status = null;
        this.product_type = null;
        this.entry_type = null;
        this.suspend_status = null;
        this.quality_status = null;
        this.skip_count = 0L;
        this.limit_count = 50L;
        this.product_code_list = null;
        this.name = null;
        this.id_list = productIdList;
        this.external_product_ids = null;
        this.brand_id_list = null;
        this.preorder = null;
        this.trait_type_list = null;
        this.penalty_status_list = null;
        this.sales_status_list = Arrays.asList("ON_SALE");
        this.date_created_gte = 946652400000L;
        this.date_created_lte = 1687618799999L;
    }
}