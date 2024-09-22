package com.adam_stegienko.campaign_controller_api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                // Check if the 'dev' profile is active
                if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
                    registry.addMapping("/v1/api/**")
                            .allowedOrigins("http://localhost:3000", "https://campaign-controller.stegienko.com:8443")
                            .allowedMethods("*")
                            .allowedHeaders("*")
                            .allowCredentials(true);
                } else {
                    registry.addMapping("/v1/api/**")
                            .allowedOrigins("http://localhost:3000", "https://campaign-controller.stegienko.com:8443")
                            .allowedMethods("*")
                            .allowedHeaders("*")
                            .allowCredentials(true);
                }
            }
        };
    }
}