package com.jiangou.subscription.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.subscription.dto.SubscribeDTO;
import com.jiangou.subscription.dto.SubscriptionTokenDTO;
import com.jiangou.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Subscriptions")
@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ApiResult<Map<String, String>> subscribe(@Valid @RequestBody SubscribeDTO dto) {
        subscriptionService.subscribe(dto);
        Map<String, String> result = new HashMap<String, String>();
        result.put("message", "确认邮件已发送，请查收邮箱");
        return ApiResult.ok(result);
    }

    @PostMapping("/confirm")
    public ApiResult<Map<String, String>> confirm(@Valid @RequestBody SubscriptionTokenDTO dto) {
        subscriptionService.confirm(dto.getToken());
        Map<String, String> result = new HashMap<String, String>();
        result.put("message", "订阅成功");
        return ApiResult.ok(result);
    }

    @PostMapping("/unsubscribe")
    public ApiResult<Map<String, String>> unsubscribe(@Valid @RequestBody SubscriptionTokenDTO dto) {
        subscriptionService.unsubscribe(dto.getToken());
        Map<String, String> result = new HashMap<String, String>();
        result.put("message", "已退订");
        return ApiResult.ok(result);
    }
}
