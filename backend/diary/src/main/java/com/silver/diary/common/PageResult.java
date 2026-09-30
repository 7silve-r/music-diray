package com.silver.diary.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResult<T> {
    private Long total; //符合查询条件的总记录数
    private List<T> list;
}
