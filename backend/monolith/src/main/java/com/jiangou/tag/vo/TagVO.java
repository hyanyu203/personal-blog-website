package com.jiangou.tag.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagVO {

    private Long id;
    private String name;
    private String slug;
    private String color;
    private Integer usageCount;
}
