package com.mecn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

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

    public static void main(String[] args) {
        SpringApplication.run(MECNApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  MECN - 高维宏观经济因果网络联动模型");
        System.out.println("  服务已启动：http://localhost:8080");
        System.out.println("  API 文档：http://localhost:8080/api/docs");
        System.out.println("=================================================");
    }
}
