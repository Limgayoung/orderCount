package nunu.orderCount.domain.option.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        List<Option> options = extractUnsavedOptions(dto.getOptionDtoInfos());
        optionRepository.saveAll(options);
    }

    //todo: member로도 확인해줘야 하나? -> YES - product 관련 수정 필요, product.findBy~ 변경 필요
    //todo: productRepository에서 zigzagProductId와 member 로 find 해줄것
    private List<Option> extractUnsavedOptions(List<OptionDtoInfo> optionDtoInfos) {
        return optionDtoInfos.stream()
                .distinct()
                .filter(optionInfo -> {
                    Optional<Product> product = productRepository.findByZigzagProductId(
                            optionInfo.getZigzagProductId());
                    if (product.isPresent()) {
                        return !optionRepository.existsByProductAndName(product.get(), optionInfo.getOptionName());
                    } else {
                        return false;
                    }
                })
                .map(optionDtoInfo -> {
                    Product product = productRepository.findByZigzagProductId(
                            optionDtoInfo.getZigzagProductId()).get();
                    return Option.builder()
                            .name(optionDtoInfo.getOptionName())
                            .product(product)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
