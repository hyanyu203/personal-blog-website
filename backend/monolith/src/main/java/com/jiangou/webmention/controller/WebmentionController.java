package com.jiangou.webmention.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.webmention.service.WebmentionService;
import com.jiangou.webmention.vo.WebmentionVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Webmention")
@RestController
@RequestMapping("/api/v1/webmention")
public class WebmentionController {

    private final WebmentionService webmentionService;

    public WebmentionController(WebmentionService webmentionService) {
        this.webmentionService = webmentionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ApiResult<WebmentionVO>> receive(
            @RequestParam("source") String source,
            @RequestParam("target") String target) {
        WebmentionVO result = webmentionService.receive(source, target);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.ok(result));
    }
}
