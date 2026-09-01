package com.marcomoretta.dungeondesk.config;

import com.marcomoretta.dungeondesk.auth.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }


    /**
     * Add auth interceptors only on specified paths
     * In example, during login and registration phases, the requests do not have to be intercepted since the user is not logged in yet
     * @param registry A spring class instance providing methods to manage interceptors
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // Add all endpoints
                .addPathPatterns("/api/v1/**")
                // Exclude endpoints that do not need authentication
                .excludePathPatterns(
                        "/api/v1/auth/login",
                        "/api/v1/auth/register",
                        "/api/v1/auth/session/players",
                        "/api/v1/health");
    }
}
