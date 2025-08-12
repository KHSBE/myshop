package org.example.myshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}")
    String uploadPath;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 파일 시스템 C:/Users/... 폴더를 /images/** URL로 매핑
        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                        // file:///C:/Users/.../ 형식으로
                        "file:///" + uploadPath.replace("\\", "/") + "/"
                );
    }
}
