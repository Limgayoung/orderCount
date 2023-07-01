package nunu.orderCount.infra.zigzag.model.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestZigzagLoginDto {
    private final String identifier;
    private final String password;
}
