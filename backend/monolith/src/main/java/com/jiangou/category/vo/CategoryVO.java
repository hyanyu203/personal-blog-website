package com.jiangou.category.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryVO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Integer postCount;
}
