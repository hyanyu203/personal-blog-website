package com.jiangou.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @RestController
    static class ServiceInfoController {
        @GetMapping("/api/v1/_service/info")
        Map<String, Object> info() {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("service", "notification-service");
            m.put("status", "scaffold");
            m.put("port", 8006);
            return m;
        }
    }
}
