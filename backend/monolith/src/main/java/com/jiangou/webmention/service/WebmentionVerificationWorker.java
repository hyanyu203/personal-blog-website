package com.jiangou.webmention.service;

import com.jiangou.common.util.UrlSafetyUtils;
import com.jiangou.webmention.entity.WebmentionEntity;
import com.jiangou.webmention.mapper.WebmentionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class WebmentionVerificationWorker {

    private static final Logger log = LoggerFactory.getLogger(WebmentionVerificationWorker.class);
    private static final int CONNECT_TIMEOUT_MILLIS = 5000;
    private static final int READ_TIMEOUT_MILLIS = 10000;
    private static final int MAX_BODY_BYTES = 256 * 1024;

    private final WebmentionMapper webmentionMapper;

    public WebmentionVerificationWorker(WebmentionMapper webmentionMapper) {
        this.webmentionMapper = webmentionMapper;
    }

    @Async("webmentionExecutor")
    public void verify(Long id, String source, String target) {
        try {
            if (sourceContainsTarget(source, target)) {
                WebmentionEntity entity = webmentionMapper.selectById(id);
                if (entity != null && "pending".equals(entity.getStatus())) {
                    entity.setStatus("verified");
                    entity.setVerifiedAt(LocalDateTime.now());
                    entity.setUpdatedAt(LocalDateTime.now());
                    webmentionMapper.updateById(entity);
                }
            }
        } catch (Exception e) {
            log.warn("Webmention 验证失败 id={}: {}", id, e.getMessage());
        }
    }

    private boolean sourceContainsTarget(String sourceUrl, String targetUrl) throws Exception {
        HttpURLConnection conn = UrlSafetyUtils.openValidatedGetConnection(sourceUrl);
        try {
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
            conn.setReadTimeout(READ_TIMEOUT_MILLIS);
            // Follow HTTP 301/302 redirects so that a source URL that redirects
            // is not silently rejected. SSRF protection is already applied by
            // UrlSafetyUtils.openValidatedGetConnection before the connection is opened.
            conn.setInstanceFollowRedirects(true);
            if (conn.getResponseCode() >= 400) {
                return false;
            }
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            try (InputStream inputStream = conn.getInputStream()) {
                byte[] chunk = new byte[8192];
                int total = 0;
                int read;
                while ((read = inputStream.read(chunk)) != -1) {
                    int remaining = MAX_BODY_BYTES - total;
                    if (remaining <= 0) {
                        break;
                    }
                    int toWrite = Math.min(read, remaining);
                    body.write(chunk, 0, toWrite);
                    total += toWrite;
                    if (toWrite < read) {
                        break;
                    }
                }
            }
            return new String(body.toByteArray(), StandardCharsets.UTF_8).contains(targetUrl);
        } finally {
            conn.disconnect();
        }
    }
}
