package com.sherlock.webterminal.spring.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticConfig  implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/static/**").addResourceLocations("file:staticassets/");
  
  }
  
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    WebMvcConfigurer.super.addInterceptors(registry);
    
    registry.addInterceptor(new HandlerInterceptor() {
      
      @Override
      public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
          throws Exception {
        
        String contextPath = request.getContextPath();
        System.out.println("IM preHandle for " + contextPath);
        
        return HandlerInterceptor.super.preHandle(request, response, handler);
      }
      
    }).addPathPatterns("/static/**");
  }
  
}
