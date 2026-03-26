package com.api.gateway.filters;

import com.api.gateway.exceptions.ResponseCode;
import com.api.gateway.service.JwtService;
import com.api.gateway.utils.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private final RouteValidator routeValidator;
    private final JwtService jwtService;
    private final ResponseUtil responseUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if(!routeValidator.isSecured.test(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        // If token is not there, then throw exception
        if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new RuntimeException("Missing Authorization Header");
        }

        try {
            String authHeader = exchange.getRequest()
                    .getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                Map<String, String> claims = jwtService.extractClaims(token);

                // For testing purpose only
                if (exchange.getRequest().getURI().getPath().contains("test-auth")) {
                    return responseUtil.write(
                            exchange,
                            String.format("Hi, %s", claims.get("username")),
                            HttpStatus.OK,
                            ResponseCode.SUCCESS
                    );
                }

                // Mutate the request to downstream services with user data
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("X-User-Name", claims.get("username"))
                        .header("X-User-role", claims.get("role"))
                        .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }
        }
        catch (ExpiredJwtException ex) {
            return responseUtil.write(
                    exchange,
                    "Your token has been expired",
                    HttpStatus.UNAUTHORIZED,
                    ResponseCode.EXPIRED_TOKEN
            );
        }
        catch (MalformedJwtException ex) {
            log.warn("Malformed Exception: {} | Path: {}", ex.getMessage(), exchange.getRequest().getURI());
            return responseUtil.write(
                    exchange,
                    "Not a valid token",
                    HttpStatus.FORBIDDEN,
                    ResponseCode.INVALID_VALID
            );
        }
        catch (Exception ex) {
            log.warn("Unknown exception: {} | Path: {}", ex.getMessage(), exchange.getRequest().getURI());
            responseUtil.write(
                    exchange,
                    "Not a valid token",
                    HttpStatus.UNAUTHORIZED,
                    ResponseCode.INVALID_VALID
            );
        }
        return chain.filter(exchange);
    }
}
