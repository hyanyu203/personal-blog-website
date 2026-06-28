package com.jiangou.common.util;

import com.jiangou.article.vo.TocItemVO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkdownUtilsTest {

    @Test
    void extractToc_parsesHeadings() {
        String md = "# Title\n\n## Section A\n\n### Sub A\n\nBody";
        List<TocItemVO> toc = MarkdownUtils.extractToc(md);
        assertEquals(3, toc.size());
        assertEquals("title", toc.get(0).getId());
        assertEquals("Title", toc.get(0).getText());
        assertEquals(1, toc.get(0).getLevel());
        assertEquals("section-a", toc.get(1).getId());
        assertEquals(2, toc.get(1).getLevel());
    }

    @Test
    void toHtmlWithAnchors_injectsIds() {
        String md = "## Hello World\n\nText";
        String html = MarkdownUtils.toHtmlWithAnchors(md);
        assertFalse(html.contains("<h2>Hello World</h2>"));
        assertTrue(html.contains("id=\"hello-world\""));
    }

    @Test
    void toHtml_stripsScriptTags() {
        String html = MarkdownUtils.toHtml("Hello<script>alert(1)</script>");
        assertFalse(html.contains("<script"));
        assertTrue(html.contains("Hello"));
    }
}
