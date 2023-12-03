package nunu.orderCount.domain.product.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.MemberInfo;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestUpdateProductDto {
    @NotNull
    private MemberInfo memberInfo;

    @NotNull
    private List<ProductDtoInfo> productInfos;
}