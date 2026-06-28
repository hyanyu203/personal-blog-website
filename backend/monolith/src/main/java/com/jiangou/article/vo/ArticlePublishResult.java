package com.jiangou.article.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticlePublishResult {

    private final ArticleDetailVO article;
    private final boolean sendNewsletter;
}
