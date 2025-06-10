package com.dqs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Arrays;

//@Configuration
public class WebCorsConfiguration {

//    @Value("${api.cors.origins}")
//    private String allowedOrigins;
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // Clean up origins by removing trailing slashes and splitting
//        String[] origins = allowedOrigins.split(",");
//        for (int i = 0; i < origins.length; i++) {
//            origins[i] = origins[i].trim().replaceAll("/$", "");
//        }
//
//        System.out.println("CORS - Allowed origins: " + Arrays.toString(origins));
//
//        configuration.setAllowedOrigins(Arrays.asList(origins));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        configuration.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
//
//    @Bean
//    public CorsFilter corsFilter() {
//        return new CorsFilter(corsConfigurationSource());
//    }
//
//    // Keep WebMvcConfigurer as backup
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                String[] origins = allowedOrigins.split(",");
//                for (int i = 0; i < origins.length; i++) {
//                    origins[i] = origins[i].trim().replaceAll("/$", "");
//                }
//
//                registry.addMapping("/**")
//                        .allowedOrigins(origins)
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
//                        .allowedHeaders("*")
//                        .allowCredentials(true)
//                        .maxAge(3600);
//            }
//        };
//    }
}