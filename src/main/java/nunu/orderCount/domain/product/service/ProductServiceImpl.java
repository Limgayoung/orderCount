package nunu.orderCount.domain.product.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.domain.order.exception.InvalidZigzagTokenException;
import nunu.orderCount.domain.order.exception.NotExistMemberException;
import nunu.orderCount.domain.product.model.dto.request.ProductDtoInfo;
import nunu.orderCount.domain.product.model.dto.request.RequestUpdateProductDto;
import nunu.orderCount.domain.product.repository.ProductRepository;
import nunu.orderCount.global.util.RedisUtil;
import nunu.orderCount.infra.zigzag.service.ZigzagProductService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ZigzagProductService zigzagProductService;

    //상품 update
    public void updateProduct(RequestUpdateProductDto dto) {
        //1. 저장되지 않은 product 찾기
        List<ProductDtoInfo> productDtoInfos = dto.getProductInfos().stream()
                .filter(productInfo -> !productRepository.existsByZigzagProductId(productInfo.getZigzagProductId()))
                .collect(Collectors.toList());

        List<String> zigzagProductIds = productDtoInfos.stream().map(ProductDtoInfo::getZigzagProductId)
                .collect(Collectors.toList());

        //2. zigzag에서 image url 찾기
//        zigzagProductService.ZigzagProductImagesUrlRequester();


    }
}
