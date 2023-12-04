package nunu.orderCount.domain.option.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.member.model.MemberInfo;
import nunu.orderCount.domain.option.model.OptionDtoInfo;
import nunu.orderCount.domain.option.model.dto.request.RequestCreateOptionsDto;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.product.model.Product;
import nunu.orderCount.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @Mock
    OptionRepository optionRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    OptionService optionService;

    @Test
    @DisplayName("create options")
    void createOptionsSuccess(){
        //1. dto 받기 - MemberInfo, OptionDtoInfos
        MemberInfo memberInfo = createMemberInfo();
        List<OptionDtoInfo> optionDtoInfos = createOptionDtoInfos(1, 5);
        RequestCreateOptionsDto requestCreateOptionsDto = new RequestCreateOptionsDto(createMemberInfo(),
                optionDtoInfos);
        Product product = createProduct("1", memberInfo.getMember(), "product");

        //2. 이미 저장되어 있는 option 제거하기
        doReturn(Optional.of(product)).when(productRepository).findByZigzagProductId(anyString());
        doReturn(true).when(optionRepository).existsByProductAndName(any(Product.class), anyString());
        doReturn(List.of("1")).when(optionRepository).saveAll(anyList());

        optionService.createOptions(requestCreateOptionsDto);
    }

    private MemberInfo createMemberInfo(){
        return new MemberInfo(new Member("email", "password"), "token");
    }

    private List<OptionDtoInfo> createOptionDtoInfos(int start, int n){
        new OptionDtoInfo("option1", "1");

        List<OptionDtoInfo> optionDtoInfos = new ArrayList<>();
        String num = "";
        for(int i=start;i<start+n;i++){
            num+=i;
            optionDtoInfos.add(new OptionDtoInfo("option" + i, num));
        }

        return optionDtoInfos;
    }

    private Product createProduct(String zigzagProductId, Member member, String productName) {
        return Product.builder()
                .name(productName)
                .imageUrl("")
                .zigzagProductId(zigzagProductId)
                .member(member)
                .build();
    }

}