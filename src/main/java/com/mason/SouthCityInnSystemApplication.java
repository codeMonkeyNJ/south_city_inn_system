package com.mason;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableFeignClients
@EnableScheduling//开启定时
@EnableWebSocket // 开启Spring WebSocket
@SpringBootApplication
public class SouthCityInnSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SouthCityInnSystemApplication.class, args);
    }

}
