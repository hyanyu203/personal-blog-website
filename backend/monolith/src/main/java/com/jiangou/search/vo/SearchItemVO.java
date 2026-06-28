package com.jiangou.search.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchItemVO {

    private String type;
    private String title;
    private String url;
    private String snippet;
    private Double score;
}
