package com.jiangou.upload.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangou.common.exception.NotFoundException;
import com.jiangou.common.exception.ValidationException;
import com.jiangou.common.result.PageResult;
import com.jiangou.config.MinioConfig.MinioProperties;
import com.jiangou.upload.entity.AttachmentEntity;
import com.jiangou.upload.mapper.AttachmentMapper;
import com.jiangou.upload.vo.AttachmentVO;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UploadService {

    private static final long MAX_UPLOAD_BYTES = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED = new HashSet<String>(Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "application/pdf"));

    private final AttachmentMapper attachmentMapper;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public UploadService(AttachmentMapper attachmentMapper, MinioClient minioClient,
                         MinioProperties minioProperties) {
        this.attachmentMapper = attachmentMapper;
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    public PageResult<AttachmentVO> listAdmin(long page, long pageSize) {
        Page<AttachmentEntity> result = attachmentMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<AttachmentEntity>()
                        .isNull(AttachmentEntity::getDeletedAt)
                        .orderByDesc(AttachmentEntity::getCreatedAt));
        return PageResult.of(result.getRecords().stream().map(this::toVo).collect(Collectors.toList()),
                result.getTotal(), page, pageSize);
    }

    @Transactional
    public AttachmentVO upload(MultipartFile file, Long uploaderId) {
        if (minioClient == null) {
            throw new ValidationException("MinIO is not available");
        }
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File must not be empty");
        }
        if (file.getSize() > MAX_UPLOAD_BYTES) {
            throw new ValidationException("File must not exceed 10MB");
        }

        byte[] bytes = readBytes(file);
        String mime = detectMime(bytes);
        if (!ALLOWED.contains(mime)) {
            throw new ValidationException("Unsupported file type");
        }
        ImageInfo imageInfo = validateImage(bytes, mime);

        String originalFilename = safeOriginalFilename(file.getOriginalFilename());
        String objectKey = buildObjectKey(mime);
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectKey)
                    .stream(in, bytes.length, -1)
                    .contentType(mime)
                    .build());
        } catch (Exception e) {
            throw new ValidationException("文件上传失败，请稍后重试");
        }

        AttachmentEntity entity = new AttachmentEntity();
        entity.setUploaderId(uploaderId);
        entity.setFilename(originalFilename);
        entity.setObjectKey(objectKey);
        entity.setUrl(buildPublicUrl(objectKey));
        entity.setMimeType(mime);
        entity.setSizeBytes((long) bytes.length);
        entity.setWidth(imageInfo.getWidth());
        entity.setHeight(imageInfo.getHeight());
        entity.setSha256(sha256(bytes));
        entity.setStatus("ready");
        entity.setMetadata("{}");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        attachmentMapper.insert(entity);
        return toVo(entity);
    }

    @Transactional
    public void delete(Long id) {
        AttachmentEntity entity = attachmentMapper.selectOne(new LambdaQueryWrapper<AttachmentEntity>()
                .eq(AttachmentEntity::getId, id).isNull(AttachmentEntity::getDeletedAt));
        if (entity == null) {
            throw new NotFoundException("Attachment does not exist");
        }
        if (minioClient != null) {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(entity.getObjectKey())
                        .build());
            } catch (Exception ignored) {
                // Object may already be gone.
            }
        }
        entity.setDeletedAt(LocalDateTime.now());
        attachmentMapper.updateById(entity);
    }

    private byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new ValidationException("Failed to read file");
        }
    }

    private String detectMime(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return "";
        }
        if (startsWith(bytes, new int[]{0xFF, 0xD8, 0xFF})) {
            return "image/jpeg";
        }
        if (startsWith(bytes, new int[]{0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A})) {
            return "image/png";
        }
        if (startsWithAscii(bytes, "GIF87a") || startsWithAscii(bytes, "GIF89a")) {
            return "image/gif";
        }
        if (bytes.length >= 12 && startsWithAscii(bytes, "RIFF")
                && bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return "image/webp";
        }
        if (startsWithAscii(bytes, "%PDF-")) {
            return "application/pdf";
        }
        return "";
    }

    private ImageInfo validateImage(byte[] bytes, String mime) {
        if ("application/pdf".equals(mime)) {
            return ImageInfo.empty();
        }
        if ("image/webp".equals(mime)) {
            validateWebp(bytes);
            return ImageInfo.empty();
        }
        if (!mime.startsWith("image/")) {
            return ImageInfo.empty();
        }
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                throw new ValidationException("Invalid image file");
            }
            return new ImageInfo(image.getWidth(), image.getHeight());
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Invalid image file");
        }
    }

    private void validateWebp(byte[] bytes) {
        if (bytes.length < 16 || !startsWithAscii(bytes, "RIFF")
                || bytes[8] != 'W' || bytes[9] != 'E' || bytes[10] != 'B' || bytes[11] != 'P') {
            throw new ValidationException("Invalid image file");
        }
        String chunk = new String(bytes, 12, 4, java.nio.charset.StandardCharsets.US_ASCII);
        if (!"VP8 ".equals(chunk) && !"VP8L".equals(chunk) && !"VP8X".equals(chunk)) {
            throw new ValidationException("Invalid image file");
        }
    }

    private boolean startsWith(byte[] bytes, int[] signature) {
        if (bytes.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if ((bytes[i] & 0xFF) != signature[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean startsWithAscii(byte[] bytes, String signature) {
        if (bytes.length < signature.length()) {
            return false;
        }
        for (int i = 0; i < signature.length(); i++) {
            if (bytes[i] != (byte) signature.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private String safeOriginalFilename(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "upload";
        }
        String normalized = filename.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        if (slash >= 0) {
            normalized = normalized.substring(slash + 1);
        }
        normalized = normalized.replaceAll("[\\r\\n\\t]", "_").trim();
        return normalized.isEmpty() ? "upload" : normalized;
    }

    private String buildObjectKey(String mime) {
        return "uploads/" + LocalDate.now() + "/" + UUID.randomUUID() + extensionFor(mime);
    }

    private String extensionFor(String mime) {
        if ("image/jpeg".equals(mime)) {
            return ".jpg";
        }
        if ("image/png".equals(mime)) {
            return ".png";
        }
        if ("image/gif".equals(mime)) {
            return ".gif";
        }
        if ("image/webp".equals(mime)) {
            return ".webp";
        }
        if ("application/pdf".equals(mime)) {
            return ".pdf";
        }
        return ".bin";
    }

    private String buildPublicUrl(String objectKey) {
        String endpoint = StringUtils.hasText(minioProperties.getPublicEndpoint())
                ? minioProperties.getPublicEndpoint()
                : minioProperties.getEndpoint();
        while (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint + "/" + minioProperties.getBucket() + "/" + objectKey;
    }

    private String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private AttachmentVO toVo(AttachmentEntity e) {
        return AttachmentVO.builder()
                .id(e.getId()).filename(e.getFilename()).url(e.getUrl())
                .mimeType(e.getMimeType()).sizeBytes(e.getSizeBytes())
                .width(e.getWidth()).height(e.getHeight()).createdAt(e.getCreatedAt()).build();
    }

    private static class ImageInfo {
        private final Integer width;
        private final Integer height;

        private ImageInfo(Integer width, Integer height) {
            this.width = width;
            this.height = height;
        }

        private static ImageInfo empty() {
            return new ImageInfo(null, null);
        }

        private Integer getWidth() {
            return width;
        }

        private Integer getHeight() {
            return height;
        }
    }
}
