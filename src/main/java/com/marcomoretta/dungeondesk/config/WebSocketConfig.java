package com.marcomoretta.dungeondesk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * Web Socket Configuration
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler gameWebSocketHandler;
    private final HandshakeInterceptor webSocketInterceptor;

    public WebSocketConfig(WebSocketHandler gameWebSocketHandler, HandshakeInterceptor webSocketInterceptor) {
        this.gameWebSocketHandler = gameWebSocketHandler;
        this.webSocketInterceptor = webSocketInterceptor;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameWebSocketHandler, "/ws")
                .addInterceptors(webSocketInterceptor);
    }
}
