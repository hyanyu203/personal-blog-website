package com.jiangou.common.util;

import com.jiangou.common.exception.ValidationException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;

public final class UrlSafetyUtils {

    private UrlSafetyUtils() {
    }

    public static String normalizeOptionalHttpUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        return parseHttpUrl(url.trim(), false);
    }

    public static void validateExternalHttpUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new ValidationException("URL 不能为空");
        }
        resolveValidatedHost(url.trim());
    }

    public static void validateSameSiteTarget(String target, String siteUrl) {
        if (target == null || target.trim().isEmpty() || siteUrl == null || siteUrl.trim().isEmpty()) {
            throw new ValidationException("target 必须为本站 URL");
        }
        URI targetUri = parseHttpUri(target.trim());
        URI siteUri = parseHttpUri(siteUrl.trim());
        if (!schemeEquals(targetUri, siteUri) || !hostEquals(targetUri, siteUri) || !portEquals(targetUri, siteUri)) {
            throw new ValidationException("target 必须为本站 URL");
        }
        String sitePath = normalizePath(siteUri.getPath());
        String targetPath = normalizePath(targetUri.getPath());
        if (!"/".equals(sitePath)
                && !targetPath.equals(sitePath)
                && !targetPath.startsWith(sitePath.endsWith("/") ? sitePath : sitePath + "/")) {
            throw new ValidationException("target 必须为本站 URL");
        }
    }

    public static HttpURLConnection openValidatedGetConnection(String urlString) throws IOException {
        URI uri = URI.create(urlString.trim());
        String host = resolveValidatedHost(urlString.trim());
        InetAddress[] addresses = InetAddress.getAllByName(host);
        for (InetAddress address : addresses) {
            if (isBlockedAddress(address)) {
                throw new ValidationException("不允许访问内网或保留地址");
            }
        }
        int defaultPort = "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
        int port = uri.getPort() > 0 ? uri.getPort() : defaultPort;
        String path = uri.getRawPath();
        if (path == null || path.isEmpty()) {
            path = "/";
        }
        if (uri.getRawQuery() != null) {
            path = path + "?" + uri.getRawQuery();
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(
                uri.getScheme(), addresses[0].getHostAddress(), port, path).openConnection();
        conn.setRequestProperty("Host", host);
        conn.setInstanceFollowRedirects(false);
        return conn;
    }

    private static String resolveValidatedHost(String trimmed) {
        String host = parseHttpUrl(trimmed, true);
        if ("localhost".equalsIgnoreCase(host) || host.endsWith(".local")) {
            throw new ValidationException("不允许访问本地地址");
        }
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress address : addresses) {
                if (isBlockedAddress(address)) {
                    throw new ValidationException("不允许访问内网或保留地址");
                }
            }
        } catch (UnknownHostException e) {
            throw new ValidationException("无法解析 URL 主机名");
        }
        return host;
    }

    private static String parseHttpUrl(String trimmed, boolean returnHost) {
        URI uri = parseHttpUri(trimmed);
        return returnHost ? uri.getHost() : trimmed;
    }

    private static URI parseHttpUri(String trimmed) {
        URI uri;
        try {
            uri = URI.create(trimmed);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("URL 格式无效");
        }
        String scheme = uri.getScheme();
        if (scheme == null || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
            throw new ValidationException("仅允许 http/https URL");
        }
        String host = uri.getHost();
        if (host == null || host.isEmpty()) {
            throw new ValidationException("URL 缺少主机名");
        }
        return uri;
    }

    private static boolean schemeEquals(URI left, URI right) {
        return left.getScheme().equalsIgnoreCase(right.getScheme());
    }

    private static boolean hostEquals(URI left, URI right) {
        return left.getHost().equalsIgnoreCase(right.getHost());
    }

    private static boolean portEquals(URI left, URI right) {
        return effectivePort(left) == effectivePort(right);
    }

    private static int effectivePort(URI uri) {
        if (uri.getPort() > 0) {
            return uri.getPort();
        }
        return "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
    }

    private static String normalizePath(String path) {
        if (path == null || path.isEmpty() || "/".equals(path)) {
            return "/";
        }
        return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    private static boolean isBlockedAddress(InetAddress address) {
        if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()) {
            return true;
        }
        if (address.isSiteLocalAddress()) {
            return true;
        }
        byte[] octets = address.getAddress();
        if (octets.length == 4) {
            int b0 = octets[0] & 0xFF;
            int b1 = octets[1] & 0xFF;
            if (b0 == 0) {
                return true;
            }
            if (b0 == 10) {
                return true;
            }
            if (b0 == 172 && b1 >= 16 && b1 <= 31) {
                return true;
            }
            if (b0 == 192 && b1 == 168) {
                return true;
            }
            if (b0 == 169 && b1 == 254) {
                return true;
            }
        }
        return false;
    }
}
