package com.jiangou.schedule.controller;

import com.jiangou.schedule.service.RssService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "RSS")
@RestController
@RequestMapping("/api/v1/rss")
public class RssController {

    private final RssService rssService;

    public RssController(RssService rssService) {
        this.rssService = rssService;
    }

    @GetMapping(value = "/feed.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String feed() {
        return rssService.buildFeed();
    }
}
