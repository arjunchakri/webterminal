package com.sherlock.webterminal.spring.core;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.sherlock.webterminal.formulas.CustomFormulaExecutor;

@SpringBootApplication
//@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.sherlock.webterminal", "com.logviewer.springboot"})
public class AppWebTerminalStarter extends SpringBootServletInitializer {

//  @Bean
//  public ServletRegistrationBean servletRegistrationBean() {
//    ServletRegistrationBean bean = new ServletRegistrationBean(new GuacamoleTunnelServlet(), "/tunnel");
//    return bean;
//  }

  public static void main(String[] args) {

    SpringApplicationBuilder builder = new SpringApplicationBuilder(AppWebTerminalStarter.class);
    builder.headless(false);
    ConfigurableApplicationContext context = builder.run(args);

//    SpringApplication.run(AppWebTerminalStarter.class, args);

    initializeServices();

  }

  private static void initializeServices() {
    try {
      CustomFormulaExecutor.setupFormulaeCrons();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
