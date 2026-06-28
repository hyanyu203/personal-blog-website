package com.jiangou.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }

    @RestController
    static class ServiceInfoController {
        @GetMapping("/api/v1/_service/info")
        Map<String, Object> info() {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("service", "search-service");
            m.put("status", "scaffold");
            m.put("port", 8005);
            return m;
        }
    }
}
