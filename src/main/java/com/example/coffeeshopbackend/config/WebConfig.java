package com.example.coffeeshopbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS",
                        "PATCH"
                )
                .allowedHeaders("*")
                .exposedHeaders(
                        "Authorization",
                        "Content-Type"
                )
                .allowCredentials(false)
                .maxAge(3600);
    }


    // =========================================================
    // SERVE AZURE UPLOADED IMAGES
    // =========================================================

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:./src/main/resources/static/uploads/"
                );
    }


    // =========================================================
    // CORS FILTER
    // =========================================================

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration config =
                new CorsConfiguration();

        config.addAllowedOrigin("*");

        config.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS",
                        "PATCH"
                )
        );

        config.addAllowedHeader("*");

        config.setExposedHeaders(
                Arrays.asList(
                        "Authorization",
                        "Content-Type",
                        "X-Requested-With",
                        "Accept",
                        "Origin",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers"
                )
        );

        config.setAllowCredentials(false);

        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/api/**",
                config
        );

        return new CorsFilter(source);
    }
}