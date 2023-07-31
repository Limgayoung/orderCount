package nunu.orderCount.global.util;

import nunu.orderCount.global.config.RedisTestContainers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(RedisTestContainers.class)
@SpringBootTest
public class RedisUtilTest {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    void getData() {
        //given
        String refreshToken = "asdf.asdf.asdf";
        String key = "refreshToken:1";
        //when
        redisUtil.setData(key,refreshToken, 100000);
        //then
        assertThat(redisUtil.getData(key)).isEqualTo(refreshToken);
    }

    @Test
    void existData() {
        //given
        String refreshToken = "asdf.asdf.asdf";
        String key = "refreshToken:1";
        //when
        redisUtil.setData(key,refreshToken, 100000);
        //then
        assertThat(redisUtil.existData(key)).isEqualTo(true);
    }

    @Test
    void setData() {
        //given
        String refreshToken = "asdf.asdf.asdf";
        String key = "refreshToken:1";
        //when
        redisUtil.setData(key,refreshToken, 100000);
        //then
        assertThat(redisUtil.getData(key)).isEqualTo(refreshToken);
    }

    @Test
    void deleteData() {
        //given
        String refreshToken = "asdf.asdf.asdf";
        String key = "refreshToken:1";
        redisUtil.setData(key,refreshToken, 100000);
        //when
        redisUtil.deleteData(key);
        //then
        assertThat(redisUtil.existData(key)).isEqualTo(false);
    }
}