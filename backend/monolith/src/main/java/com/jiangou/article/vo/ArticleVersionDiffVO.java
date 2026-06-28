package com.jiangou.article.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVersionDiffVO {

    private Integer fromVersion;
    private Integer toVersion;
    private List<DiffLine> lines;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiffLine {
        /** EQUAL, INSERT, DELETE */
        private String type;
        private String content;
    }
}
