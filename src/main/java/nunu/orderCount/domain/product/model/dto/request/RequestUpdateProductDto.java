package nunu.orderCount.domain.product.model.dto.request;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.product.model.ProductDtoInfo;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestUpdateProductDto {
    @NotNull
    private MemberInfo memberInfo;

    @NotNull
    private List<ProductDtoInfo> productInfos;
}