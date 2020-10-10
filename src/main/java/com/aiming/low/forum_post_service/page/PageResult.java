package com.aiming.low.forum_post_service.page;

import com.github.pagehelper.PageInfo;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName PageResult
 * @Description 返回给前端的数据，带有分页信息
 * @Author aiminglow
 */
public class PageResult {
    // 当前页码
    private int pageNum;
    // 每页数量
    private int pageSize;
    // 当前页记录的数量
    private int currPageSize;
    // 记录总数
    private long totalSize;
    // 页码总数
    private int totalPages;
    // 数据
    private HashMap<String, List> content;

    public static PageResult fromPageInfo(PageInfo pageInfo) {
        PageResult pageResult = new PageResult();
        pageResult.pageNum = pageInfo.getPageNum();
        pageResult.pageSize = pageInfo.getPageSize();
        pageResult.currPageSize = pageInfo.getSize();
        pageResult.totalSize = pageInfo.getTotal();
        pageResult.totalPages = pageInfo.getPages();

        return pageResult;
    }

    public void setContent(HashMap<String, List> content) {
        this.content = content;
    }

    public int getPageNum() {
        return pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCurrPageSize() {
        return currPageSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public HashMap<String, List> getContent() {
        return content;
    }
}
