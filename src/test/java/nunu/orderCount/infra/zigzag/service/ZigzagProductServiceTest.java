package nunu.orderCount.infra.zigzag.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class ZigzagProductServiceTest {

    @Autowired
    ZigzagProductService zigzagProductService;

    @Value("${webclient.zigzag.cookie}")
    String cookie;

    @Test
    void zigzagProductImagesUrlRequester() {
        Map<String, String> stringStringMap = zigzagProductService.ZigzagProductImagesUrlRequester(cookie,
                List.of("130385013"));
        assertThat(stringStringMap.size()).isEqualTo(1L);
    }
    
    //todo: 안되는 경우 test 필요
}