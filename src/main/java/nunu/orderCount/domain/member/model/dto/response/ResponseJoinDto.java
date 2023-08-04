package nunu.orderCount.domain.member.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nunu.orderCount.domain.member.model.Member;

@Getter
@RequiredArgsConstructor
public class ResponseJoinDto {
    @Schema(description = "memberId",example = "1L")
    private final Long memberId;
    public static ResponseJoinDto of(Member member) {
        return new ResponseJoinDto(member.getMemberId());
    }
}
