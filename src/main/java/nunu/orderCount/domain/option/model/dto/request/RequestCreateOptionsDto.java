package nunu.orderCount.domain.option.model.dto.request;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.option.model.OptionDtoInfo;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RequestCreateOptionsDto {
    private MemberInfo memberInfo;
    private List<OptionDtoInfo> optionDtoInfos;
}
