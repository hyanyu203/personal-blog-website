package com.jiangou.article.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArchiveGroupVO {

    private Integer year;
    private Integer month;
    private List<ArchiveItemVO> articles;

    @Data
    @Builder
    public static class ArchiveItemVO {
        private Long id;
        private String title;
        private String slug;
        private String publishedAt;
    }
}
