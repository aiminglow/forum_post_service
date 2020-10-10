package com.aiming.low.forum_post_service.controller;

import com.aiming.low.forum_post_service.page.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName HomeController
 * @Description
 * @Author aiminglow
 */
@RestController
public class HomeController {
    /**
     * 查询主页的post列表
     * 这个接口获得的post列表的排序是“最新被回复的帖子排在最前面”，注意与“最新被创建的列表”的区别
     */
    @GetMapping({"", "/", "/list", "/list/{pageNum}"})
    public PageResult getHomePostList(@PathVariable int pageNum) {
        return null;
    }
}
