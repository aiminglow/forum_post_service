package com.aiming.low.forum_post_service.dao;

import com.aiming.low.forum_post_service.entity.ImmutablePost;
import com.aiming.low.forum_post_service.entity.ThreeStatus;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个类最初建立主要是为了验证自定义的mybatis的typeHandler和EnumOrdinalTypeHandler是否能够正常工作
 * 自定义的TypeHandler是 com.aiming.low.forum_post_service.dao.type_handler.DateIntegerHandler
 */
@MybatisTest
@ActiveProfiles("devutit")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration({PageHelperAutoConfiguration.class})
class ImmutablePostMapperIT {

    @Autowired
    ImmutablePostMapper postMapper;

    @Test
    @Sql(statements = "insert into post (post_id, click_number, last_reply_time, last_reply_user_id, post_title, post_user_id," +
            " reply_number, post_status, create_time)" +
            " values (4, 12, 1600771743, 123, 'post title', 321, 3, 1, 1600771562);")
    void selectById() {
        List<ImmutablePost> posts = postMapper.selectById(4L);
        assertFalse(posts.isEmpty());
        ImmutablePost actual = posts.get(0);
        // 没有deleteTime
        ImmutablePost expect = ImmutablePost.builder()
                .postId(4L).clickNumber(12).lastReplyTime(new Date((long) 1600771743 * 1000))
                .lastReplyUserId(Long.valueOf(123)).postTitle("post title").postUserId(321L)
                .replyNumber(3).postStatus(ThreeStatus.ACTIVE)
                // 这里如果不加 （long）的强制类型转换的话，得到的Date对象的fastTime是负数，也就是跑到1970年之前了
                // 1600771562 * 1000这个数字已经整形溢出了，溢出之后就变成了负数，所以也就出现了1970年之前的日期
                .createTime(new Date((long) 1600771562 * 1000))
                .build();

        assertTrue(expect.createTime().equals(actual.createTime()));
        System.out.println("expect:\n" + expect);
        System.out.println("actual:\n" + actual);
        assertTrue(expect.equals(actual));
    }

    @Test
    @Sql(scripts = {"classpath:test_sql/PostMapperIT-insert43posts.sql"})
    void selectOrderByIdDesc_withoutPageHelper() {
        List<ImmutablePost> postPageInfo = postMapper.selectOrderByIdDesc(null);
        assertFalse(postPageInfo.isEmpty());

        assertEquals(43, postPageInfo.size());
    }

    @Test
    @Sql(scripts = {"classpath:test_sql/PostMapperIT-insert43posts.sql"})
    void selectOrderByIdDesc_withPageHelper_page1() {
        PageHelper.startPage(1, 20);
        List<ImmutablePost> posts = postMapper.selectOrderByIdDesc(null);
        PageInfo<ImmutablePost> pageInfo = new PageInfo<>(posts);
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
    void selectOrderByIdDesc_withPageHelper_page3() {
        PageHelper.startPage(3, 20);
        List<ImmutablePost> posts = postMapper.selectOrderByIdDesc(null);
        PageInfo<ImmutablePost> pageInfo = new PageInfo<>(posts);
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
    void selectOrderByIdDesc_withPageHelper_page_negative1() {
        PageHelper.startPage(-1, 20);
        List<ImmutablePost> posts = postMapper.selectOrderByIdDesc(null);
        PageInfo<ImmutablePost> pageInfo = new PageInfo<>(posts);
        assertFalse(posts.isEmpty());
        assertEquals(1, pageInfo.getPageNum());
    }

    @Test
    @Sql(scripts = {"classpath:test_sql/PostMapperIT-insert43posts.sql"})
    void selectOrderByIdDesc_withPageHelper_page4() {
        PageHelper.startPage(4, 20);
        List<ImmutablePost> posts = postMapper.selectOrderByIdDesc(null);
        PageInfo<ImmutablePost> pageInfo = new PageInfo<>(posts);
        assertFalse(posts.isEmpty());
        assertEquals(3, pageInfo.getPageNum());
    }

    @Test
    void insert() {
        ImmutablePost expect = ImmutablePost.builder()
                .postId(111L).clickNumber(100).lastReplyTime(new Date((long) 1600958614 * 1000))
                .lastReplyUserId(456L).postTitle("title").postUserId(654L)
                .replyNumber(10).postStatus(ThreeStatus.NOT_ACTIVE)
                .createTime(new Date((long) 1600958614 * 1000))
                .build();
        int count = postMapper.insert(expect);
        assertEquals(1, count);

        List<ImmutablePost> posts = postMapper.selectById((long) 111);
        assertFalse(posts.isEmpty());
        ImmutablePost actual = posts.get(0);
        assertEquals(expect, actual);
    }

    @Test
    @Sql(statements = "insert into post (post_id, click_number, last_reply_time, last_reply_user_id, post_title, post_user_id," +
            " reply_number, post_status, create_time)" +
            " values (666, 700, 1600771743, 12, 'postTitle', 21, 70, 1, 1600771562);")
    void updateById() {
        // 这里date初始化的long数值如果不强转成long的话，这个数字作为integer会溢出，然后会出现负数，
        // 而负数在插入在int unsigned的column的时候会报超出范围的错：Data truncation: Out of range value for column ...
        Date date = new Date( (long) 1601037653 * 1000);
        ImmutablePost mod = ImmutablePost.builder().postId(666L).postTitle("new title")
                .postStatus(ThreeStatus.NOT_ACTIVE).deleteTime(date).build();
        int count = postMapper.updateById(mod);
        assertEquals(1, count);

        List<ImmutablePost> posts = postMapper.selectById(666L);
        assertFalse(posts.isEmpty());
        ImmutablePost actual = posts.get(0);

        ImmutablePost expect = ImmutablePost.builder()
                .postId(666L).clickNumber(700).lastReplyTime(new Date((long) 1600771743 * 1000))
                .lastReplyUserId(12L).postTitle("new title").postUserId(21L)
                .replyNumber(70).postStatus(ThreeStatus.NOT_ACTIVE)
                .createTime(new Date((long) 1600771562 * 1000)).deleteTime(date)
                .build();

        // 这里之前之所以出现了两个Date对象不相等的情况，是因为我们new Date()不带long参数的时候，Date对象里面用的是毫秒数，而且最后三位不是000，
        // 而是真实的毫秒数。而从数据库拿出来的毫秒数，其实已经经过除以1000的操作了，精度降低了，Date对象里面的fastTime的最后三位也不一样了。
        // 以后怎么防止这种问题的发生呢？
        // - 不需要防止，这本来就是一个debug就能解决的问题，但是lombok生成的代码不能debug。
        //
        // 不过就算上面的问题很麻烦，另一个问题也不能忽视，那就是Lombok生成的代码不能debug的问题，这会让我寻找问题很麻烦，怎么解决？
        // - 放弃lombok，使用可以最终debug生成类的方式，目前开始使用Immutable生成代码
        // assertEquals(expect.getDeleteTime(), actual.getDeleteTime());

        assertEquals(expect.hashCode(), actual.hashCode());
        assertEquals(expect, actual);
    }

    @Test
    @Sql(statements = "insert into post (post_id, click_number, last_reply_time, last_reply_user_id, post_title, post_user_id," +
            " reply_number, post_status, create_time)" +
            " values (888, 700, 1600771743, 12, 'postTitle', 21, 70, 1, 1600771562);")
    void deleteById() {
        List<ImmutablePost> posts = postMapper.selectById(888L);
        assertFalse(posts.isEmpty());

        int count = postMapper.deleteById(888L);
        assertEquals(1, count);

        List<ImmutablePost> posts1 = postMapper.selectById(888L);
        assertTrue(posts1.isEmpty());
    }
}