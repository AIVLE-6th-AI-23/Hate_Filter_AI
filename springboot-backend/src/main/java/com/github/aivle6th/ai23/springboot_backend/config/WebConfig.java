// package com.github.aivle6th.ai23.springboot_backend.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class WebConfig implements WebMvcConfigurer {

//     @Override
//     public void addViewControllers(ViewControllerRegistry registry) {
//         registry.addViewController("/").setViewName("index");
//     }

//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/api/**")
//                 .allowedOrigins("http://localhost:8080")
//                 .allowedMethods("GET", "POST", "PUT", "DELETE")
//                 .allowedHeaders("*")
//                 .allowCredentials(true);
//     }
// }
