package nunu.orderCount.infra.zigzag.service;

import lombok.RequiredArgsConstructor;
import nunu.orderCount.infra.zigzag.exception.ZigzagRequestApiException;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ZigzagWebClientRequester {
    private final WebClient webClient;

    protected <T, V> T post(String url, String cookieString, V requestDto, Class<T> responseDtoClass) {
        return webClient.post()
                .uri(url)
                .header("Cookie", cookieString)
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
}
