package nunu.orderCount.domain.member.model.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nunu.orderCount.domain.member.model.Member;

@Getter
@RequiredArgsConstructor
public class ResponseJoinDto {
    private final Long memberId;
    private final String email;

    public static ResponseJoinDto of(Member member) {
        return new ResponseJoinDto(member.getMemberId(), member.getEmail());
    }
}
