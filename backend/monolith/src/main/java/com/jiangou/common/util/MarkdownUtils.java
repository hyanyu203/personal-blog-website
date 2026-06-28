package com.jiangou.common.util;

import com.jiangou.article.vo.TocItemVO;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarkdownUtils {

    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,6})\\s+(.+)$");

    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

    private MarkdownUtils() {
    }

    public static String toHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        return toHtmlWithAnchors(markdown);
    }

    public static String toHtmlWithAnchors(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        List<TocItemVO> toc = extractToc(markdown);
        Node document = PARSER.parse(markdown);
        String html = RENDERER.render(document);
        if (toc.isEmpty()) {
            return HtmlSanitizer.sanitize(html);
        }
        // Use DOTALL so the content group matches inline HTML (e.g. <code>, <strong>).
        // group(2) is re-used verbatim as the heading's inner HTML; only the opening tag gains an id.
        Pattern pattern = Pattern.compile("<h([1-6])>(.*?)</h\\1>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        int index = 0;
        while (matcher.find() && index < toc.size()) {
            String replacement = "<h" + matcher.group(1) + " id=\"" + toc.get(index).getId()
                    + "\">" + matcher.group(2) + "</h" + matcher.group(1) + ">";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            index++;
        }
        matcher.appendTail(sb);
        return HtmlSanitizer.sanitize(sb.toString());
    }

    public static String toPlainText(String markdown) {
        if (markdown == null) {
            return "";
        }
        return markdown.replaceAll("[#*`>_\\-\\[\\]()]", " ").replaceAll("\\s+", " ").trim();
    }

    public static int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    public static int estimateReadingMinutes(int wordCount) {
        return Math.max(1, (int) Math.ceil(wordCount / 200.0));
    }

    public static List<TocItemVO> extractToc(String markdown) {
        List<TocItemVO> items = new ArrayList<TocItemVO>();
        if (markdown == null || markdown.isEmpty()) {
            return items;
        }
        Set<String> usedIds = new HashSet<String>();
        for (String line : markdown.split("\n")) {
            Matcher matcher = HEADING_PATTERN.matcher(line.trim());
            if (!matcher.matches()) {
                continue;
            }
            int level = matcher.group(1).length();
            String text = matcher.group(2).trim();
            String id = slugifyHeading(text);
            String uniqueId = id;
            int suffix = 2;
            while (usedIds.contains(uniqueId)) {
                uniqueId = id + "-" + suffix++;
            }
            usedIds.add(uniqueId);
            items.add(TocItemVO.builder().id(uniqueId).text(text).level(level).build());
        }
        return items;
    }

    private static String slugifyHeading(String text) {
        String slug = text.toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
        return slug.isEmpty() ? "section" : slug;
    }
}
