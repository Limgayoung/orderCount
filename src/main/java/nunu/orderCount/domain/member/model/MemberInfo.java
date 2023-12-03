package nunu.orderCount.domain.member.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberInfo {
    private final Long memberId;
    private final String zigzagToken;
}
