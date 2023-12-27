package nunu.orderCount.domain.order.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.model.MemberInfo;


@Schema(name = "전체 배송준비중 주문 내역 조회", description = "주문 내역 조회에 필요한 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestFindOrdersDto {
    @Schema(description = "회원", example = "")
    @NotNull
    private Member member;
}
