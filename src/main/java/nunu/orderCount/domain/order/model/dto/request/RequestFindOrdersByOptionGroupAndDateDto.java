package nunu.orderCount.domain.order.model.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.Member;

@Schema(name = "특정 기간 배송준비중 주문 내역 조회", description = "주문 내역 조회에 필요한 정보")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestFindOrdersByOptionGroupAndDateDto {
    @Schema(description = "회원", example = "")
    @NotNull
    private Member member;

    @Schema(description = "시작일", example = "2023-12-19")
    @NotNull
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2023-12-21")
    @NotNull
    private LocalDate endDate;
}
