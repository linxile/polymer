package com.polymer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

/**
 * EnableCaching 启用缓存支持
 * exclude = { DataSourceAutoConfiguration.class }: 禁用Spring Boot数据源的自动装配
 */
@EnableCaching
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PolymerApplication {
    public static void main(String[] args) {
        // 生产环境强制禁用 DevTools 热重启，防止类加载器泄漏
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(PolymerApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  polymer启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}