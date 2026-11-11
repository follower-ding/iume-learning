package learn.spring.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/** 纯 Spring MVC 配置，没有 Boot 自动装配。 */
@Configuration
@EnableWebMvc
public class WebConfig {
    @Bean GreetingController greetingController() { return new GreetingController(); }
    @Bean ApiExceptionHandler apiExceptionHandler() { return new ApiExceptionHandler(); }
}
