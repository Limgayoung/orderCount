package nunu.orderCount.domain.order.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.order.model.OrderDtoInfo;


@Schema(name = "주문 현황 업데이트 dto", description = "주문 현황 업데이트에 필요한 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestOrderUpdateDto {
    @Schema(description = "회원 정보", example = "")
    @NotNull
    private MemberInfo memberInfo;

    @Schema(description = "주문 정보", example = "")
    @NotNull
    private List<OrderDtoInfo> orderDtoInfos;
}
