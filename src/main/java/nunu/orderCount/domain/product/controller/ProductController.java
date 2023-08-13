package nunu.orderCount.domain.product.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Product 관련 API")
@RequestMapping("/api/products")
@RestController
@RequiredArgsConstructor
public class ProductController {
}
