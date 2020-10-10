package com.aiming.low.forum_post_service.service.impl;

import com.aiming.low.forum_post_service.dao.ImmutablePostMapper;
import com.aiming.low.forum_post_service.entity.ImmutablePost;
import com.aiming.low.forum_post_service.service.PostService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName PostServiceImpl
 * @Description
 * @Author aiminglow
 */
@Service
public class PostServiceImpl implements PostService {

    private final ImmutablePostMapper postMapper;

    public PostServiceImpl(ImmutablePostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    public PageInfo<ImmutablePost> selectPostByPage(int pageNum, int offset) {
        PageHelper.startPage(pageNum, offset);
        List<ImmutablePost> posts = postMapper.selectOrderByIdDesc(null);
        return new PageInfo<>(posts);
    }

}
