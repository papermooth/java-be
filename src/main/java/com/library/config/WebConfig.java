package com.library.config;

import com.library.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册JWT拦截器，排除登录相关接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login", "/api/logout");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许跨域请求
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源访问配置
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // 文件上传目录访问配置
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + System.getProperty("user.dir") + "/uploads/");
    }
}