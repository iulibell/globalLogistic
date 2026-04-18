package com.api;

import java.util.List;

import com.github.pagehelper.PageInfo;

import lombok.Data;

@Data
public class CommonPage<T> {
    private int pageNum;
    private int pageSize;
    private int totalPage;
    private long total;
    private List<T> list;

    /**
     * 转化为分页结果信息
     * @param <T>
     * @param list
     * @return
     */
    public static <T> CommonPage<T> commonPage(List<T> list){
        CommonPage<T> result = new CommonPage<>();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setTotalPage((int) Math.ceil(pageInfo.getTotal() / (double) pageInfo.getPageSize()));
        result.setTotal(pageInfo.getTotal());
        result.setList(pageInfo.getList());
        return result;
    }
}
