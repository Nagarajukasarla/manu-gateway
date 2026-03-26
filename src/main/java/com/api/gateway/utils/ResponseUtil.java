package com.api.gateway.utils;

import com.api.gateway.exceptions.ResponseCode;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public final class ResponseUtil {

    public Mono<Void> write(
            ServerWebExchange exchange,
            String message,
            HttpStatus status,
            ResponseCode responseCode
    ) {

        // Set response status and format
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");

        String body = String.format(
                "{\"message\": \"%s\", \"status\": %d, \"responseCode\": %s}",
                message, status.value(), responseCode.getMessage()
        );

        // Convert message into bytes
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        // Wrap converted bytes into Buffer
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
