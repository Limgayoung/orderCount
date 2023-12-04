package nunu.orderCount.domain.option.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OptionDtoInfo {
    private String optionName;
    private String zigzagProductId;
}
