package com.library.common;

import lombok.Data;

@Data
public class PageRequest {
    private int pageNum = 1; // 当前页码
    private int pageSize = 10; // 每页条数
    private String keyword; // 搜索关键词
    private String sortBy; // 排序字段
    private String orderBy = "DESC"; // 排序方式：ASC或DESC
}