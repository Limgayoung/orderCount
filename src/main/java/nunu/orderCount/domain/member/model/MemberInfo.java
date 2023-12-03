package nunu.orderCount.domain.member.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberInfo {
    private final Member member;
    private final String zigzagToken;
}
