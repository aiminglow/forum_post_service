package com.aiming.low.forum_post_service.service.impl;

import com.aiming.low.forum_post_service.entity.ImmutablePost;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@MybatisTest
@ActiveProfiles("devutit")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration({PageHelperAutoConfiguration.class})
@Import(PostServiceImpl.class)
class PostServiceImplIT {

    @Autowired
    PostServiceImpl postService;
    /**
     * 因为service层使用了pageHelper，而这个插件是需要和mybatis结合使用的，所以我并不能直接mock mapper，
     * mock了mapper的话就不能测试service层的分页功能了，所以我还是需要写service层和dao层的集成测试
     */
    @Test
    @Sql(scripts = {"classpath:test_sql/PostMapperIT-insert43posts.sql"})
    void selectPostByPage_page1() {
        PageInfo<ImmutablePost> pageInfo = postService.selectPostByPage(1, 20);
        List<ImmutablePost> posts = pageInfo.getList();

        assertFalse(posts.isEmpty());
        assertEquals(1, pageInfo.getPageNum());
        assertEquals(20, pageInfo.getPageSize());
        assertEquals(20, pageInfo.getSize());
        assertEquals(3, pageInfo.getPages());
        assertEquals(43, pageInfo.getTotal());

        assertEquals(43L, posts.get(0).postId());
    }

    @Test
    @Sql(scripts = {"classpath:test_sql/PostMapperIT-insert43posts.sql"})
    void selectPostByPage_page3() {
        PageInfo<ImmutablePost> pageInfo = postService.selectPostByPage(3, 20);
        List<ImmutablePost> posts = pageInfo.getList();
        assertFalse(posts.isEmpty());
        assertEquals(3, pageInfo.getPageNum());
        assertEquals(20, pageInfo.getPageSize());
        // 由于是最后一页，最后一页只剩下了3条记录
        assertEquals(3, pageInfo.getSize());
        assertEquals(3, pageInfo.getPages());
        assertEquals(43, pageInfo.getTotal());

        assertEquals(3, posts.size());
        assertEquals(3L, posts.get(0).postId());
        assertEquals(1L, posts.get(2).postId());
    }

    @Test
    @Sql(scripts = {"classpath:test_sql/PostMapperIT-insert43posts.sql"})
    void selectPostByPage_page_negative1() {
        PageHelper.startPage(-1, 20);
        PageInfo<ImmutablePost> pageInfo = postService.selectPostByPage(-1, 20);
        List<ImmutablePost> posts = pageInfo.getList();
        assertFalse(posts.isEmpty());
        assertEquals(1, pageInfo.getPageNum());
    }

    @Test
    @Sql(scripts = {"classpath:test_sql/PostMapperIT-insert43posts.sql"})
    void selectPostByPage_page4() {
        PageHelper.startPage(4, 20);
        PageInfo<ImmutablePost> pageInfo = postService.selectPostByPage(4, 20);
        List<ImmutablePost> posts = pageInfo.getList();
        assertFalse(posts.isEmpty());
        assertEquals(3, pageInfo.getPageNum());
    }
}