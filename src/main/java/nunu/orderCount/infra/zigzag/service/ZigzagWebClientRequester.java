package nunu.orderCount.infra.zigzag.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nunu.orderCount.infra.zigzag.exception.ZigzagRequestApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class ZigzagWebClientRequester {
    private final WebClient webClient;

    @Value("${webclient.zigzag.origin}")
    private String ORIGIN_URI;

    protected <T, V> T post(String url, String cookieString, V requestDto, Class<T> responseDtoClass) {
        return webClient.post()
                .uri(url)
                .header("Cookie", cookieString)
                .header("Origin", ORIGIN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                        clientResponse -> Mono.error(new ZigzagRequestApiException("webclient 요청을 실패했습니다.")))
                .onStatus(httpStatus -> httpStatus.is5xxServerError(),
                        clientResponse -> Mono.error(new ZigzagRequestApiException("zigzag 오류로 응답값을 받아올 수 없습니다.")))
                .bodyToMono(responseDtoClass)
                .block();
    }

    protected <T> ClientResponse.Headers postGetHeader(String url, T requestDto) {
        ClientResponse clientResponse = webClient.post()
                .uri(url)
                .header("Origin", ORIGIN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchangeToMono(response -> {
                    if (response.statusCode().is4xxClientError()) {
                        log.info("webclient 요청을 실패했습니다.");
                        return Mono.error(new ZigzagRequestApiException("webclient 요청을 실패했습니다."));
                    } else if (response.statusCode().is5xxServerError()) {
                        log.info("zigzag 오류로 응답값을 받아올 수 없습니다.");
                        return Mono.error(new ZigzagRequestApiException("zigzag 오류로 응답값을 받아올 수 없습니다."));
                    }
                    return Mono.just(response);
                })
                .block();

        return clientResponse.headers();
    }
}
