package com.jiangou.common.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public final class HtmlSanitizer {

    private static final PolicyFactory POLICY = new HtmlPolicyBuilder()
            .allowElements("p", "br", "hr", "div", "span",
                    "h1", "h2", "h3", "h4", "h5", "h6",
                    "ul", "ol", "li",
                    "blockquote", "pre", "code", "em", "strong", "del", "ins", "sub", "sup",
                    "a", "img", "table", "thead", "tbody", "tr", "th", "td")
            .allowUrlProtocols("http", "https", "mailto")
            .allowAttributes("href").onElements("a")
            .allowAttributes("src", "alt", "title").onElements("img")
            .allowAttributes("id", "class").globally()
            .toFactory();

    private HtmlSanitizer() {
    }

    public static String sanitize(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        return POLICY.sanitize(html);
    }
}
