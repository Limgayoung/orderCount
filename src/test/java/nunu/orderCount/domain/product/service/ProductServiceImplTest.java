package nunu.orderCount.domain.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;
import nunu.orderCount.domain.product.model.dto.request.ProductDtoInfo;
import nunu.orderCount.domain.product.model.dto.request.RequestUpdateProductDto;
import nunu.orderCount.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    @Test
    @DisplayName("모든 데이터 새로 저장하는 경우")
    void updateProduct() {
        RequestUpdateProductDto requestUpdateProductDto = makeUpdateProductDto();
        doReturn(false).when(productRepository).existsByZigzagProductId(anyString());


        productService.updateProduct(requestUpdateProductDto);
    }

    private RequestUpdateProductDto makeUpdateProductDto(){
        return new RequestUpdateProductDto(1L, makeProductDtoInfos(0, 3));
    }

    private List<ProductDtoInfo> makeProductDtoInfos(int start, int n){
        new ProductDtoInfo("청바지", "11111");
        String name = "";
        String productId = "";
        List<ProductDtoInfo> productDtoInfos = new ArrayList<>();
        for(int i=start;i<start+n;i++){
            productId += i;
            productDtoInfos.add(new ProductDtoInfo(name + i, productId));
        }
        return productDtoInfos;
    }
}