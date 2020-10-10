package com.aiming.low.forum_post_service.service;


import com.aiming.low.forum_post_service.entity.ImmutablePost;
import com.github.pagehelper.PageInfo;

public interface PostService {
    PageInfo<ImmutablePost> selectPostByPage(int pageNum, int offset);
}
