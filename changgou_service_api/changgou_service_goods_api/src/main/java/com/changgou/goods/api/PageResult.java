package com.changgou.goods.api;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private Long total;//总记录数
    private List<T> rows;//记录
    private Integer pages;  // 总页数

    public PageResult(Long total, List<T> rows, Integer pages) {
        this.total = total;
        this.rows = rows;
        this.pages = pages;
    }
    public PageResult(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }
    public PageResult() {
    }

}