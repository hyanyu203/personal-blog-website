package com.jiangou.auth.service;

import com.jiangou.auth.vo.CaptchaVO;
import com.jiangou.common.constant.ErrorCodes;
import com.jiangou.common.exception.BusinessException;
import com.jiangou.config.AuthProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {

    private static final char[] CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private static final String PREFIX = "captcha:";

    private final StringRedisTemplate redisTemplate;
    private final AuthProperties authProperties;
    private final SecureRandom random = new SecureRandom();

    public CaptchaService(StringRedisTemplate redisTemplate, AuthProperties authProperties) {
        this.redisTemplate = redisTemplate;
        this.authProperties = authProperties;
    }

    public CaptchaVO generate() {
        return generateWithCode(randomCode(4));
    }

    protected CaptchaVO generateWithCode(String code) {
        String captchaId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                PREFIX + captchaId,
                hash(code),
                authProperties.getCaptchaTtlSeconds(),
                TimeUnit.SECONDS);
        return CaptchaVO.builder()
                .captchaId(captchaId)
                .imageBase64(renderImage(code))
                .build();
    }

    public void verifyAndConsume(String captchaId, String captchaCode) {
        if (captchaId == null || captchaId.isEmpty() || captchaCode == null || captchaCode.isEmpty()) {
            throw new BusinessException(ErrorCodes.INVALID_CAPTCHA, "图形验证码错误");
        }
        String key = PREFIX + captchaId;
        String stored = redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);
        if (stored == null || !stored.equalsIgnoreCase(hash(captchaCode.trim()))) {
            throw new BusinessException(ErrorCodes.INVALID_CAPTCHA, "图形验证码错误或已过期");
        }
    }

    private String randomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS[random.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }

    private String hash(String code) {
        return DigestUtils.md5DigestAsHex(code.toLowerCase().getBytes(StandardCharsets.UTF_8));
    }

    protected String renderImage(String code) {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(245, 245, 245));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }
        g.setColor(new Color(30, 30, 30));
        g.drawString(code, 18, 28);
        g.dispose();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to render captcha", e);
        }
    }
}
