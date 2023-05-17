package com.example.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfiguration {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        //to deploy elsewhere uncomment the line below and comment the two lines below that
                        //.allowedOrigins("*")
                        .allowedOrigins("http://localhost:3000")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization")
                        .maxAge(3600);
            }
        };
    }
}
