package com.champion.readingstory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = {SpringApplicationAdminJmxAutoConfiguration.class,
        JmxAutoConfiguration.class,
        GsonAutoConfiguration.class,
        XADataSourceAutoConfiguration.class,
        JndiDataSourceAutoConfiguration.class,
        JtaAutoConfiguration.class,
        WebSocketMessagingAutoConfiguration.class,
        FreeMarkerAutoConfiguration.class,
        MustacheAutoConfiguration.class})
@ServletComponentScan(basePackages = "com.iflytek.readingstory.filter")
@MapperScan(basePackages = {"com.iflytek.readingstory.dao.mapper"})
public class ReadingStoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadingStoryApplication.class, args);
    }

}
