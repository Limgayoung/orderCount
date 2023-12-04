package nunu.orderCount.domain.option.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.option.model.Option;
import nunu.orderCount.domain.option.model.OptionDtoInfo;
import nunu.orderCount.domain.option.model.dto.request.RequestCreateOptionsDto;
import nunu.orderCount.domain.option.repository.OptionRepository;
import nunu.orderCount.domain.product.model.Product;
import nunu.orderCount.domain.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OptionService {
    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public void createOptions(RequestCreateOptionsDto dto) {
        List<Option> options = extractUnsavedOptions(dto.getOptionDtoInfos(), dto.getMemberInfo().getMember());
        optionRepository.saveAll(options);
    }

    private List<Option> extractUnsavedOptions(List<OptionDtoInfo> optionDtoInfos, Member member) {
        return optionDtoInfos.stream()
                .distinct()
                .filter(optionInfo -> {
                    Optional<Product> product = productRepository.findByZigzagProductIdAndMember(
                            optionInfo.getZigzagProductId(), member);
                    if (product.isPresent()) {
                        return !optionRepository.existsByProductAndName(product.get(), optionInfo.getOptionName());
                    } else {
                        return false;
                    }
                })
                .map(optionDtoInfo -> {
                    Product product = productRepository.findByZigzagProductIdAndMember(
                            optionDtoInfo.getZigzagProductId(), member).get();
                    return Option.builder()
                            .name(optionDtoInfo.getOptionName())
                            .product(product)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
