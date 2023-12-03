package nunu.orderCount.domain.product.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "주문 현황 업데이트 dto", description = "주문 현황 업데이트에 필요한 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestUpdateProductDto {
    @Schema(description = "회원 id", example = "1L")
    @NotNull
    private Long memberId;

    @Schema(description = "상품 정보 리스트")
    @NotNull
    private List<ProductDtoInfo> productInfos;
}