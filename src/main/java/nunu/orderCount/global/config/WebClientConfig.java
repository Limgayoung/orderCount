package nunu.orderCount.global.config;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(){
        HttpClient httpClient = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 100)
                .responseTimeout(Duration.ofMillis(60 * 1000L));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1*1024*1024)) //1M 설정, 기본 256KB
                .defaultHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
//                .filter(
//                        ExchangeFilterFunction.ofRequestProcessor(
//                                clientRequest -> {
//                                    log.info("------REQUEST--------");
//                                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
//                                    clientRequest.headers().forEach(
//                                            (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
//                                    );
//                                    return Mono.just(clientRequest);
//                                }
//                        )
//                )
                .build();
    }
}
