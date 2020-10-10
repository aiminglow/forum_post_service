package com.aiming.low.forum_post_service.dao;

import com.aiming.low.forum_post_service.entity.ImmutablePost;

import java.util.List;

public interface ImmutablePostMapper {
    // 此部分代码为测试自定义的mybatis type handler所写，之后需要删除（其实也不一定非要删除，可以保留下来。测试完之后再看吧）
    List<ImmutablePost> selectById(Long postId);

    List<ImmutablePost> selectOrderByIdDesc(ImmutablePost post);

    int insert(ImmutablePost post);

    int updateById(ImmutablePost post);

    int deleteById(Long postId);

    // 此部分代码为测试自定义的mybatis type handler所写，之后需要删除

}
