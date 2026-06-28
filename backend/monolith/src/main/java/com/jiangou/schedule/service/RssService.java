package com.jiangou.schedule.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiangou.article.entity.ArticleEntity;
import com.jiangou.article.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RssService {

    private static final DateTimeFormatter RFC822 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z");

    private final ArticleMapper articleMapper;

    @Value("${jiangou.site-url:http://localhost:3000}")
    private String siteUrl;

    @Value("${jiangou.site-title:渐构}")
    private String siteTitle;

    public RssService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    public String buildFeed() {
        List<ArticleEntity> articles = articleMapper.selectList(new LambdaQueryWrapper<ArticleEntity>()
                .eq(ArticleEntity::getStatus, "published")
                .eq(ArticleEntity::getVisibility, "public")
                .isNull(ArticleEntity::getDeletedAt)
                .orderByDesc(ArticleEntity::getPublishedAt)
                .last("LIMIT 50"));

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<rss version=\"2.0\">\n<channel>\n");
        xml.append("<title>").append(escape(siteTitle)).append("</title>\n");
        xml.append("<link>").append(escape(siteUrl)).append("</link>\n");
        xml.append("<description>渐次构建，理解计算机世界</description>\n");

        for (ArticleEntity a : articles) {
            xml.append("<item>\n");
            xml.append("<title>").append(escape(a.getTitle())).append("</title>\n");
            xml.append("<link>").append(escape(siteUrl)).append("/posts/").append(escape(a.getSlug())).append("</link>\n");
            xml.append("<guid>").append(escape(siteUrl)).append("/posts/").append(escape(a.getSlug())).append("</guid>\n");
            if (a.getSummary() != null) {
                xml.append("<description>").append(escape(a.getSummary())).append("</description>\n");
            }
            if (a.getPublishedAt() != null) {
                xml.append("<pubDate>").append(a.getPublishedAt().format(RFC822)).append("</pubDate>\n");
            }
            xml.append("</item>\n");
        }

        xml.append("</channel>\n</rss>");
        return xml.toString();
    }

    private String escape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
