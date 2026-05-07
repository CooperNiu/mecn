package com.mecn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ComponentScan;
import com.mecn.i18n.ConsoleMessage;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * MECN Spring Boot 应用启动类
 * 
 * Macro Economic Causal Network - 高维宏观经济因果网络联动模型
 * 
 * @author MECN Team
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.mecn")
public class MECNApplication {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("i18n/messages");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        return source;
    }

    public static void main(String[] args) {
        SpringApplication.run(MECNApplication.class, args);
        ConsoleMessage msg = new ConsoleMessage();
        System.out.println("=================================================");
        System.out.println("  " + msg.get("app.name"));
        System.out.println("  " + msg.fmt("app.started", "http://localhost:8080"));
        System.out.println("  " + msg.fmt("app.docs", "http://localhost:8080/api/docs"));
        System.out.println("=================================================");
    }
}
